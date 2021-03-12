package com.ekwing.jianwenapp.activity

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.util.DataUtil
import com.ekwing.jianwenapp.util.MapUtil
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.DataInput
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(intent.hasExtra("userid"))
        {
            LoginUserid.setText(intent.getStringExtra("userid"))
        }
        if(intent.hasExtra("password"))
        {
            LoginPassword.setText(intent.getStringExtra("password"))
        }

        LoginBtn.setOnClickListener {

            val userid = LoginUserid.text.toString()
            val passwrod = LoginPassword.text.toString()

            Thread{
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(DataUtil.LOGIN + "?userid=" + userid + "&password=" + passwrod)
                        .build()
                    val response = client.newCall(request).execute()
                    val responseData = response.body()?.string()
                    if(responseData == "USERID_NOT_EXIST")
                    {
                        Looper.prepare();
                        Toast.makeText(this, "该用户不存在，请确认", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }else if(responseData == "WRONG_PASSWORD")
                    {
                        Looper.prepare();
                        Toast.makeText(this, "密码错误，请重试", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }else
                    {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("nickname", responseData)
                        intent.putExtra("userid", userid)
                        Log.d("debug-userid", userid)
                        startActivity(intent)
                    }
                }catch (e:Exception)
                {

                }

            }.start()
        }

        GoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
        }
    }
}