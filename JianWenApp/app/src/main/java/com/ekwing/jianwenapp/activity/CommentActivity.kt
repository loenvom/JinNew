package com.ekwing.jianwenapp.activity

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.bean.CommentData
import com.ekwing.jianwenapp.util.CommentAdapter
import com.ekwing.jianwenapp.util.DataUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.activity_comment.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class CommentActivity : AppCompatActivity() {
    var CommentList:ArrayList<CommentData> = ArrayList<CommentData>()
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val uniquekey = intent.getStringExtra("uniquekey")
        userid = intent.getStringExtra("userid")
        val nickname = intent.getStringExtra("nickname")

        val layoutManager = LinearLayoutManager(this)
        recyclerview_comment.layoutManager = layoutManager
        val adapter = CommentAdapter(CommentList, this)
        recyclerview_comment.adapter = adapter
        setupItemTouchHelper(this)

        InitCommentList(uniquekey, adapter)

        SendBtn.setOnClickListener {
            val words = CommentText.text.toString()
            val time = getNowDateTime()
            if(words.length == 0)
            {
                Toast.makeText(this, "发送的内容不允许为空", Toast.LENGTH_LONG).show()
            }else
            {
                thread {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(DataUtil.SEND + "?uniquekey=" + uniquekey + "&userid=" + userid + "&words=" + words + "&time=" + time)
                            .build()

                        val response = client.newCall(request).execute()
                        val responseData = response.body()?.string()

                        if(responseData!=null)
                        {
                            if(responseData == "polite")
                            {
                                CommentList.add(CommentData(userid, uniquekey, words, nickname, time))
                                runOnUiThread{
                                    adapter.notifyDataSetChanged()
                                }

                            }else
                            {
                                runOnUiThread{
                                    Toast.makeText(this, "请注意您的言辞", Toast.LENGTH_LONG).show()
                                }

                            }
                        }

                    }catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                }
            }
        }


    }

    private fun InitCommentList(uniquekey: String, adapter: CommentAdapter)
    {
        Thread{
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(DataUtil.COMMENT + "?uniquekey=" + uniquekey)
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body()?.string()
                if(responseData != null)
                {
                    val gson = Gson()
                    val typeOf = object : TypeToken<List<CommentData>>(){}.type
                    val list = gson.fromJson<List<CommentData>>(responseData, typeOf)
                    for(comment in list)
                    {
                        Log.d("debug add", comment.toString())
                        CommentList.add(CommentData(comment.userid, comment.uniquekey, comment.words, comment.nickname, comment.time))
                    }

                    adapter.notifyDataSetChanged()
                }
            }catch (e : Exception)
            {
                e.printStackTrace()
            }
        }.start()



    }

    fun getNowDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date())
    }

    private fun setupItemTouchHelper(context: Context){
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

                var tmp =  CommentList[viewHolder.adapterPosition]

                if(tmp.userid != userid)
                {
                    Toast.makeText(context, "您不能删除不属于您的评论", Toast.LENGTH_LONG).show()
                    recyclerview_comment.adapter?.notifyDataSetChanged()
                    return
                }

                val url = DataUtil.REMOVECOMMENT+ "?uniquekey=" + tmp.uniquekey + "&userid=" + tmp.userid + "&words=" + tmp.words + "&time=" + tmp.time

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
                                    Toast.makeText(context, "已为您删去该条评论", Toast.LENGTH_LONG).show()
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

                    CommentList.removeAt(viewHolder.adapterPosition)
                    runOnUiThread {
                        recyclerview_comment.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                    }

                }.start()


            }
        })






        helper.attachToRecyclerView(recyclerview_comment)
    }


}