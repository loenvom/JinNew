package com.ekwing.jianwenapp.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.util.BitmapUtil
import com.ekwing.jianwenapp.util.DataUtil
import com.ekwing.jianwenapp.util.ProgressRequestBody
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * 注册页面的逻辑代码
 * 其中服务器入口为 /register
 */
class RegisterActivity : AppCompatActivity() {
    lateinit var bitmap : Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Photo.setOnClickListener {
            /**
             * 通过intent方法来传递需要获取的文件信息
             */
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 111)
        }

        RegisterBtn.setOnClickListener {
            // 读入注册信息
            val nickname = Nickname.text.toString().trim()
            val userid = Userid.text.toString().trim()
            val password = Password.text.toString().trim()
            val confirm = Confirm.text.toString().trim()

            //检测信息是否输入完成
            if(nickname.isEmpty() || userid.isEmpty() || password.isEmpty() || confirm.isEmpty() || !this::bitmap.isInitialized)
            {
                Toast.makeText(this, "请检查信息是否输入完整", Toast.LENGTH_LONG).show()
            }else {


                if(password != confirm)
                {
                    Toast.makeText(this, "两次密码不一致，请检查", Toast.LENGTH_LONG).show()
                }else{
                    Thread {
                        val client = OkHttpClient()
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            //携带一个表单参数
                            .addFormDataPart("nickname", nickname)
                            .addFormDataPart("userid", userid)
                            .addFormDataPart("password", password)
                            //设置参数名、文件名和文
                            .addFormDataPart(
                                "myfile", userid + ".jpg", ProgressRequestBody(
                                    MediaType.parse("image/*"),
                                    BitmapUtil.bmpToByteArray(bitmap, false)
                                )
                            )
                            .build()
                        val request = Request.Builder()
                            .url(DataUtil.REGISTERURl)
                            .post(requestBody)
                            .build()
                        val call = client.newCall(request)
                        try {
                            val response = call.execute()
                            if (response.isSuccessful) {
                                val body = response.body()
                                val state = body!!.string()
                                if(state == "REGISTER_SUCCESS")
                                {
                                    /**
                                     * 注册成功，跳转至登录页面
                                     */
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.putExtra("userid", userid)
                                    intent.putExtra("password", password)
                                    this.startActivity(intent)

                                }else if(state == "EXIST_USERID")
                                {
                                    try {
                                        Looper.prepare();
                                        Toast.makeText(this, "该账户已被占用，请尝试别的", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }catch(e:Exception)
                                    {
                                        e.printStackTrace()
                                    }

                                }
                            }
                            response.close()
                        } catch (e: Exception) {
                            Log.i("kang", "错误=" + e.toString())
                        }

                    }.start()


                }

            }
        }

    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * 重载函数，依据resultCode的值来处理获得数据
         * resultCode的值由自己设定，可以前后对应即可
         */
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            111 -> {
                /**
                 * 由于Data传回的是一个uri值，所以需要通过这个值来获取对应的图片信息
                 * 以下代码参考自网络
                 */
                bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), data?.data);
                /**
                 * 通过resizeImage压缩图片
                 * 其中涉及到dp单位到px单位的转换运算
                 * 由于在前端布局使用的是dp，而对于图片使用的是像素单位的压缩运算
                 * 所以需要进行换算
                 */
                bitmap = resizeImage(bitmap,
                    150 * this.resources.displayMetrics.density,
                    150 * this.resources.displayMetrics.density)
                Photo.setImageBitmap(bitmap)
            }

        }
    }

    fun resizeImage(bitmap: Bitmap, width: Float, height: Float): Bitmap {
        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val scaleWidth = width.toFloat() / bmpWidth
        val scaleHeight = height.toFloat() / bmpHeight

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true)
    }


}