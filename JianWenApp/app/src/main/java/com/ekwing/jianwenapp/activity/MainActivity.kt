package com.ekwing.jianwenapp.activity

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.adapter.MyRecyclerViewAdapter
import com.ekwing.jianwenapp.bean.NewData
import com.ekwing.jianwenapp.util.DataUtil
import com.ekwing.jianwenapp.util.HttpClient.Companion.sendOkHttpRequest
import com.ekwing.jianwenapp.util.ImageCallback
import com.ekwing.jianwenapp.util.MapUtil
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_rv_list.view.*
import kotlinx.android.synthetic.main.layout_draw.*
import okhttp3.*
import org.w3c.dom.Text
import java.io.IOException
import kotlin.collections.ArrayList

/**
 * 主页面的逻辑代码
 * 当前的IP地址
 * 192.168.212.107
 */
class MainActivity : AppCompatActivity(), OnClickListener {


    var dataNews = NewData()

    var nickname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tool_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val userid = intent.getStringExtra("userid")
        nickname = intent.getStringExtra("nickname")

        getInternetData(DataUtil.TOPURL, userid)
        /**
         * 在导航栏中的VIEW无法直接访问
         * 需要使用以下代码获取VIEW
         */
        val mNavigationView:NavigationView = findViewById(R.id.nav_gation)
        val nav_gation_layout = mNavigationView.getHeaderView(0);
        val myphoto : ImageView = nav_gation_layout.findViewById(R.id.UserPhoto)
        /**
         * 导入用户的头像
         */

        Log.d("user photo", DataUtil.UPLOAD + userid + ".jpg")

        Glide.with(this).load(DataUtil.UPLOAD + userid + ".jpg").into(myphoto)


        val mynickname : TextView = nav_gation_layout.findViewById(R.id.tv_nickname)
        mynickname.setText(nickname)
        navListener(mode,  userid)
    }



    /**
     * 侧滑栏的点击事件
     */
    private fun navListener(mode: Int, userid : String) {
        nav_gation.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_first -> {
                    getInternetData(DataUtil.TOPURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_second -> {
                    getInternetData(DataUtil.SHEHUIURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_third -> {
                    getInternetData(DataUtil.GUONEIURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_fourth -> {
                    getInternetData(DataUtil.YULEURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_fivth -> {
                    getInternetData(DataUtil.TIYUURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_six -> {
                    getInternetData(DataUtil.JUNSHIURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_seven -> {
                    getInternetData(DataUtil.KEJIURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_eight -> {
                    getInternetData(DataUtil.CAIJINGURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.nav_nine -> {
                    getInternetData(DataUtil.SHISHANGURL, userid)
                    draw_layout.closeDrawers()
                }
                R.id.getcollect -> {
                    val intent = Intent(this, CollectActivity::class.java)
                    intent.putExtra("userid", userid)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                }
                R.id.tuichu -> finish()
            }
            true
        }
    }

    /**
     * RecyclerView的初始化
     */
    private fun initRecyclerView(dAta: NewData, userid : String) {
        runOnUiThread {
            Log.d("debug_nickanme", nickname)
            rv_list.layoutManager = GridLayoutManager(this, 2)
            rv_list.adapter = MyRecyclerViewAdapter(
                this,
                dataNews.result!!.data as ArrayList<NewData.ResultBean.DataBean>,
                userid, nickname
            )
            (rv_list.adapter as MyRecyclerViewAdapter).notifyDataSetChanged()
        }
    }

    /**
     * 请求网络数据
     * url 参数为请求地址
     */
    private fun getInternetData(url: String, userid :String) {
        // 通过回调请求获取参数
        sendOkHttpRequest(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }
            override fun onResponse(call: Call, response: Response) {
                // 使用Gson 对象完成对于数据的解析
                val mGson = Gson()
                dataNews =
                    mGson.fromJson<NewData>(
                        response.body()?.string(),
                        object : TypeToken<NewData>() {
                        }.type
                    )
                initRecyclerView(dataNews, userid)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_right, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)

        dialog.setMessage(R.string.helpdoc)
        dialog.setNegativeButton("取消", null)

        when (item.itemId) {
            R.id.jianjie -> dialog.show()
            android.R.id.home -> draw_layout.openDrawer(Gravity.LEFT)
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            android.R.id.home -> draw_layout.openDrawer(Gravity.RIGHT)
        }
    }

}
