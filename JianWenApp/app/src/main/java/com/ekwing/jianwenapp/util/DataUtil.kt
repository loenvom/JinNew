package com.ekwing.jianwenapp.util

import com.ekwing.jianwenapp.activity.LoginActivity

class DataUtil {
    companion object{

        var TOKEN = "fcf4dd7c8463129c0da7541068dc4323"
        val TOPURL = "http://v.juhe.cn/toutiao/index?type=top&key=" + TOKEN
        val SHEHUIURL =
            "http://v.juhe.cn/toutiao/index?type=shehui&key=" + TOKEN
        val GUONEIURL =
            "http://v.juhe.cn/toutiao/index?type=guonei&key=" + TOKEN
        val YULEURL =
            "http://v.juhe.cn/toutiao/index?type=yule&key=" + TOKEN
        val TIYUURL =
            "http://v.juhe.cn/toutiao/index?type=tiyu&key=" + TOKEN
        val JUNSHIURL =
            "http://v.juhe.cn/toutiao/index?type=junshi&key=" + TOKEN
        val KEJIURL =
            "http://v.juhe.cn/toutiao/index?type=keji&key=" + TOKEN
        val CAIJINGURL =
            "http://v.juhe.cn/toutiao/index?type=caijing&key=" + TOKEN
        val SHISHANGURL =
            "http://v.juhe.cn/toutiao/index?type=shishang&key=" + TOKEN


        val BASE = "http://192.168.212.35:8080/"

        val REGISTERURl =
            BASE + "register"

        val UPLOAD =
            BASE + "uploads/"


        val LOGIN =
            BASE + "login"

        val COMMENT =
            BASE + "comment"

        val SEND =
            BASE + "send"

        val COLLECT =
            BASE + "collect"

        val GETCOLLECT =
            BASE + "get_collect"

        val REMOVECOLLECT =

            BASE + "remove_collect"


        val REMOVECOMMENT =
            BASE + "remove_comment"

    }

}