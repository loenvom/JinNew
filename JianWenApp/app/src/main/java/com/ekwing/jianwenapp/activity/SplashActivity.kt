package com.ekwing.jianwenapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ekwing.jianwenapp.R
import java.lang.Thread.sleep
import java.util.*

class SplashActivity : AppCompatActivity() {

    class MyTask(val context: Context) : TimerTask(){
        override fun run() {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var timer= Timer()

        Log.d("debug", "have been there")

        var myTask = MyTask(this)
        timer.schedule(myTask, 1000)


    }
}