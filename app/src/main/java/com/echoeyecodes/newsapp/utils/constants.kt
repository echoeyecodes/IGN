package com.echoeyecodes.newsapp.utils

import com.echoeyecodes.newsapp.models.GalleryModel
import com.echoeyecodes.newsapp.models.VideoModel

open class NewsArticleContainer : NewsArticle()

sealed class NewsArticle {
    class Paragraph(val value: String) : NewsArticle()
    class Video(val model: VideoModel) : NewsArticleContainer()
    class Gallery(val model:GalleryModel) : NewsArticleContainer()
    object Spacing : NewsArticle()
}