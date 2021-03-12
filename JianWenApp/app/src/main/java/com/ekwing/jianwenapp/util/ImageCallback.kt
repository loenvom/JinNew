package com.ekwing.jianwenapp.util

import android.graphics.Bitmap

interface ImageCallback {
    fun onFinsh(response: Bitmap)
    fun onError(e : Exception)
}