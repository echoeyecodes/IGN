package com.echoeyecodes.newsapp.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.echoeyecodes.newsapp.models.BoldText
import com.echoeyecodes.newsapp.models.ItalicText
import com.echoeyecodes.newsapp.models.LinkText
import com.echoeyecodes.newsapp.models.RegularText
import org.jsoup.nodes.Element
import java.util.regex.Pattern


/*
Use this method to determine if we intend to grab a couple
of child elements from a node element, that should be skipped during
traversing the DOM
 */
fun NewsArticle.isContainer(): Boolean {
    return this is NewsArticleContainer
}

fun Element.isText(): Boolean {
    val tag = this.tagName()
    return tag == "a" || tag == "em" || tag == "i" || tag == "p" || tag == "strong"
}

fun Element.isVideo(): Boolean {
    val className = this.className()
    return className == "article-video-container"
}

fun Element.isQuoteContainer(): Boolean {
    val className = this.className()
    return className == "quote-container"
}

fun Element.isHeaderContainer(): Boolean {
    val tagName = this.tagName()
    return tagName.startsWith("h") && tagName.length == 2
}

fun Element.isGallery(): Boolean {
    val isGalleryParent = this.className() == "widget-container"
    return isGalleryParent && (this.allElements.find {
        it.classNames().contains("images-container")
    } != null)
}

fun Element.isImage(): Boolean {
    val tag = this.tagName()
    return tag == "img"
}

fun Element.isList():Boolean{
    val tag = this.tagName()
    return tag == "ul"
}

fun Element.isContainer():Boolean{
    return isList() || isImage() || isGallery() || isHeaderContainer() || isQuoteContainer() || isVideo()
}

fun Element.getTextType(): RegularText {
    val tag = this.tagName()
    val text = this.text()

    return when (tag) {
        "a" -> LinkText(text)
        "i", "em" -> ItalicText(text)
        "strong" -> BoldText(text)
        else -> RegularText(text)
    }
}

fun SpannableStringBuilder.styleHyperlink(context: Context){
    val word = this.toString()
    val matcher = Pattern.compile("\\[(.*?)\\)").matcher(word)
    var diff = 0

    while(matcher.find()){
        val start = matcher.start() - diff
        val end = matcher.end() - diff

        val value = matcher.group()
        val subMatcher = Pattern.compile("\\[(.*?)]").matcher(value)
        while (subMatcher.find()){
            val text = subMatcher.group().replace("[\\[\\]]".toRegex(), "")

            this.setSpan(
                CustomClickableSpan(text, context),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            this.replace(
                start,
                end,
                text
            )
            diff = word.length - this.length
        }
    }
}

fun SpannableStringBuilder.styleBoldText(){
    val word = this.toString()
    val matcher = Pattern.compile("\\*(.*?)\\*").matcher(word)
    var diff = 0

    while(matcher.find()){
        val start = matcher.start() - diff
        val end = matcher.end() - diff

        val value = matcher.group()
        val text = value.replace("*", "")

        val styleSpan = StyleSpan(Typeface.BOLD)
        this.setSpan(styleSpan, start,
            end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        this.replace(
            start,
            end,
            text
        )
        diff = word.length - this.length
    }
}

fun SpannableStringBuilder.styleItalicText(){
    val word = this.toString()
    val matcher = Pattern.compile("_(.*?)_").matcher(word)
    var diff = 0

    while(matcher.find()){
        val start = matcher.start() - diff
        val end = matcher.end() - diff

        val value = matcher.group()
        val text = value.substring(1, value.length-1)

        val styleSpan = StyleSpan(Typeface.ITALIC)
        this.setSpan(styleSpan, start,
            end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        this.replace(
            start,
            end,
            text
        )
        diff = word.length - this.length
    }
}


fun String.styleArticleText(context: Context): Spannable {
    val spannable = SpannableStringBuilder(this)
    spannable.styleBoldText()
    spannable.styleItalicText()
    spannable.styleHyperlink(context)

    return spannable
}