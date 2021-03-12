package com.ekwing.jianwenapp.util

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

class ProgressRequestBody(private val contentType: MediaType?,
                          private val file: ByteArray) : RequestBody() {
    override fun contentType(): MediaType? {
        return contentType
    }
    override fun contentLength(): Long {
        return file.size.toLong()
    }
    override fun writeTo(sink: BufferedSink) {
        try {
            val max = contentLength()
            var current = 0L
            //listenser做监听 这里我只放到这里 不写监听了 这个参数可以不用

            //写入文件
            sink.write(file)

        } catch (e: Exception) {

        }
    }
}