package com.ekwing.jianwenapp.activity

import android.content.ClipData
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.adapter.MyRecyclerViewAdapter
import com.ekwing.jianwenapp.bean.CommentData
import com.ekwing.jianwenapp.bean.NewData
import com.ekwing.jianwenapp.util.DataUtil
import com.ekwing.jianwenapp.util.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread


class CollectActivity : AppCompatActivity() {

    var dataNews = NewData()
    var nickname = ""
    var userid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)

        userid = intent.getStringExtra("userid")
        nickname = intent.getStringExtra("nickname")

        getInternetData(DataUtil.GETCOLLECT + "?userid=" + userid, userid)

    }

    /**
     * RecyclerView的初始化
     */
    private fun initRecyclerView(dAta: NewData, userid : String) {
        runOnUiThread {
            Log.d("debug_nickanme", nickname)
            collect_list.layoutManager = LinearLayoutManager(this)
            collect_list.adapter = MyRecyclerViewAdapter(
                this,
                dataNews.result!!.data as ArrayList<NewData.ResultBean.DataBean>,
                userid, nickname
            )
            (collect_list.adapter as MyRecyclerViewAdapter).notifyDataSetChanged()
            setupItemTouchHelper(this)
        }
    }

    /**
     * 请求网络数据
     */
    private fun getInternetData(url: String, userid :String) {
        Log.d("debug _ url", url)
        HttpClient.sendOkHttpRequest(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val mGson = Gson()
                dataNews =
                    mGson.fromJson<NewData>(
                        response.body()?.string(),
                        object : TypeToken<NewData>() {
                        }.type
                    )
                Log.d("debug", dataNews.result!!.data.toString())
                initRecyclerView(dataNews, userid)
            }
        })
    }

    private fun setupItemTouchHelper(context:Context){
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var list = dataNews.result!!.data

                var tmp = list!![viewHolder.adapterPosition]

                val url = DataUtil.REMOVECOLLECT + "?uniquekey=" + tmp.uniquekey + "&userid=" + userid
                Log.d("debug", url)

                Thread{

                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        val response = client.newCall(request).execute()
                        val responseData = response.body()?.string()

                        if(responseData!=null)
                        {
                            if(responseData == "true")
                            {
                                runOnUiThread {
                                    Toast.makeText(context, "已为您删去该条新闻", Toast.LENGTH_LONG).show()
                                }
                            }else if(responseData == "false")
                            {
                                runOnUiThread {
                                    Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show()
                                }
                            }
                        }else
                        {
                            Log.d("debug", "responsedata is null")
                        }

                    }catch (e : java.lang.Exception)
                    {
                        e.printStackTrace()
                    }

                    list.removeAt(viewHolder.adapterPosition)
                    runOnUiThread {
                        collect_list.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                    }

                }.start()


            }
        })


        helper.attachToRecyclerView(collect_list)
    }
}