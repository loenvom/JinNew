package com.ekwing.jianwenapp.util

import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object MapUtil {

    fun streamToByte(`is`: InputStream): ByteArray? {
        val buffer = ByteArrayOutputStream()
        var nRead: Int
        val data = ByteArray(16384)
        try {
            while (`is`.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return buffer.toByteArray()
    }
    @Throws(IOException::class)
    fun getBitmap(path: String?, callback : ImageCallback) {
        Thread{
            try {
                val url = URL(path)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.setConnectTimeout(5000)
                conn.setRequestMethod("GET")
                if (conn.getResponseCode() === 200) {
                    val inputStream: InputStream = conn.getInputStream()
                    if(inputStream == null)
                    Log.d("debug", "your guess is right")
                    val bytes = streamToByte(inputStream)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?:Log.d("debug", "no bitmap")
                    callback.onFinsh(bitmap)
                }
            }catch (e:Exception)
            {
                callback.onError(e)
            }

        }.start()



    }
}