package com.echoeyecodes.newsapp.utils

import android.util.Log

class AndroidUtilities{

    companion object{
        fun log(message: Any?) = Log.d("CARRR", message.toString())
    }
}