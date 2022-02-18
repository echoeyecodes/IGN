package com.echoeyecodes.newsapp.models

data class ImageModel(val url: String, val alt: String) {

    /*
    Use this method to get the image path in the original quality
     */
    fun getOriginalUrl(): String {
        return url.split("?").first()
    }

    /*
    Use this method to get the image thumbnail to render in small places
     */
    fun getThumbnail():String{
        return getOriginalUrl().plus("?width=500")
    }
}