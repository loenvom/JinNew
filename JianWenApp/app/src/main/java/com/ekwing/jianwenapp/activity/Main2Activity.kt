package com.ekwing.jianwenapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.bean.CommentData
import com.ekwing.jianwenapp.util.DataUtil
import kotlinx.android.synthetic.main.activity_main2.*
import okhttp3.OkHttpClient
import okhttp3.Request


class Main2Activity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //跳转传过来的数据
        val title = intent.getStringExtra("title")
        val image = intent.getStringExtra("image")
        val content = intent.getStringExtra("url")
        val uniquekey = intent.getStringExtra("uniquekey")
        val userid = intent.getStringExtra("userid")
        val nickname = intent.getStringExtra("nickname")
        //图片展示
        Glide.with(this).load(image).into(image_top)
        //标题展示
        coll_too_bar.title = title
        //设置toolbar
        setSupportActionBar(toolbar_top)
        //顶部返回按钮
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //加载url
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = WebViewClient()
        web_view.settings.javaScriptEnabled = true
        web_view.settings.defaultTextEncodingName = "utf-8"
        web_view.loadUrl(content)

        BtnComment.setOnClickListener {
            /**
             * 弹出评论区
             */
            try {
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("uniquekey", uniquekey)
                intent.putExtra("userid", userid)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }catch (e:Exception)
            {
                Log.d("debug wrong", e.printStackTrace().toString())
            }
            
        }

        BtnCollect.setOnClickListener {
            Log.d("debug", "BtnCollect")
            Thread{
                Log.d("debug", "BtnCollect in thread")
                Log.d("confirm info", uniquekey + " " + userid + " " + title + " " +  image + " " + content)
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(DataUtil.COLLECT + "?uniquekey=" + uniquekey + "&userid=" + userid + "&title=" + title + "&image_src=" + image + "&content_url=" + content)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseData = response.body()?.string()

                    if(responseData!=null)
                    {
                        if(responseData == "collect_exist")
                        {
                            runOnUiThread {
                                Toast.makeText(this, "您已经收藏过该条新闻了", Toast.LENGTH_LONG).show()
                            }
                        }else if(responseData == "true")
                        {
                            runOnUiThread {
                                Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show()
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
            }.start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
