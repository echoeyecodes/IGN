package com.echoeyecodes.newsapp.utils

import com.echoeyecodes.newsapp.models.BoldText
import com.echoeyecodes.newsapp.models.ItalicText
import com.echoeyecodes.newsapp.models.LinkText
import com.echoeyecodes.newsapp.models.RegularText
import org.jsoup.nodes.Element


/*
Use this method to determine if we intend to grab a couple
of child elements from a node element, that should be skipped during
traversing the DOM
 */
fun NewsArticle.isContainer():Boolean{
    return this is NewsArticleContainer
}

fun Element.isText(): Boolean{
    val tag = this.tagName()
    return tag == "a" || tag == "em" || tag == "i" || tag == "p" || tag == "strong"
}

fun Element.isVideo(): Boolean{
    val className = this.className()
    return className == "article-video-container"
}

fun Element.isGallery():Boolean{
    return this.classNames().contains("images-container")
}

fun Element.isImage(): Boolean{
    val tag = this.tagName()
    return tag == "img"
}

fun Element.getTextType(): RegularText{
    val tag = this.tagName()
    val text = this.text()

    return when(tag){
        "a" -> LinkText(text)
        "i", "em" -> ItalicText(text)
        "strong" -> BoldText(text)
        else -> RegularText(text)
    }
}