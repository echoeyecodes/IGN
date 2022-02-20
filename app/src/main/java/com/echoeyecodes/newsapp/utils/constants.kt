package com.echoeyecodes.newsapp.utils

import com.echoeyecodes.newsapp.models.GalleryModel
import com.echoeyecodes.newsapp.models.VideoModel

open class NewsArticleContainer : NewsArticle()
open class NewsHeader(val value: String) : NewsArticleContainer()

sealed class NewsArticle {
    class Paragraph(val value: String) : NewsArticle()
    class Quote(val value: String) : NewsArticleContainer()
    class Video(val model: VideoModel) : NewsArticleContainer()
    class Gallery(val model:GalleryModel) : NewsArticleContainer()
    class Header1(value: String): NewsHeader(value)
    class Header2(value: String): NewsHeader(value)
    class Header3(value: String): NewsHeader(value)
    class OrderedList(val items: List<String>): NewsArticleContainer()
    object Spacing : NewsArticle()
}