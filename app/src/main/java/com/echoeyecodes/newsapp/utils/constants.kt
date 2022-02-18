package com.echoeyecodes.newsapp.utils

import com.echoeyecodes.newsapp.models.ImageModel
import com.echoeyecodes.newsapp.models.VideoModel

open class NewsArticleContainer : NewsArticle()

sealed class NewsArticle {
    class Paragraph(val value: String) : NewsArticle()
    class Video(val model: VideoModel) : NewsArticleContainer()
    class Gallery(val images: List<ImageModel>) : NewsArticleContainer()
    object Spacing : NewsArticle()
}