package com.echoeyecodes.newsapp.utils.articleparser

import com.echoeyecodes.newsapp.models.*
import com.echoeyecodes.newsapp.utils.*
import kotlinx.coroutines.*
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

class ArticleParser {
    var article = ArrayList<NewsArticle>()
    var articlePiece: NewsArticle? = null
    var articleParserConfig = ArticleParserConfig()

    companion object {
        const val URL =
            "https://www.ign.com/articles/james-gunn-peacemaker-bisexual-character-john-cena"
    }


    fun addToArticle() {
        articlePiece?.let {
            this.article.add(it)
        }
        articlePiece = null
    }

    fun addSpacer() {
        this.article.add(NewsArticle.Spacing)
    }

    private fun resolveArticleParagraph(word: String): NewsArticle {
        val temp = word.replace("\\\\s+", "")
        return if (temp.isNotEmpty()) {
            return if (articlePiece != null && articlePiece is NewsArticle.Paragraph) {
                NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus(word))
            } else {
                NewsArticle.Paragraph(word)
            }
        } else {
            NewsArticle.Paragraph("")
        }
    }

    private fun openArticleLink(): NewsArticle {
        return if (articlePiece != null) {
            if (articlePiece is NewsArticle.Paragraph) {
                NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("["))
            } else {
                //when a paragraph starts with a link sentence
                addToArticle()
                NewsArticle.Paragraph("[")
            }
        } else {
            NewsArticle.Paragraph("[")
        }
    }

    private fun openArticleItalic(): NewsArticle {
        return if (articlePiece != null) {
            if (articlePiece is NewsArticle.Paragraph) {
                NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("_"))
            } else {
                //when a paragraph starts with an italic sentence
                addToArticle()
                NewsArticle.Paragraph("_")
            }
        } else {
            NewsArticle.Paragraph("_")
        }
    }

    private fun openArticleBold(): NewsArticle {
        return if (articlePiece != null) {
            if (articlePiece is NewsArticle.Paragraph) {
                NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("*"))
            } else {
                //when a paragraph starts with an italic sentence
                addToArticle()
                NewsArticle.Paragraph("*")
            }
        } else {
            NewsArticle.Paragraph("*")
        }
    }

    private fun closeArticleLink(link: String): NewsArticle {
        return if (articlePiece != null && articlePiece is NewsArticle.Paragraph) {
            NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("]".plus("(${link})")))
        } else {
            NewsArticle.Paragraph("")
        }
    }

    private fun closeArticleItalic(): NewsArticle {
        return if (articlePiece != null && articlePiece is NewsArticle.Paragraph) {
            NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("_"))
        } else {
            NewsArticle.Paragraph("")
        }
    }

    private fun closeArticleBold(): NewsArticle {
        return if (articlePiece != null && articlePiece is NewsArticle.Paragraph) {
            NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("*"))
        } else {
            NewsArticle.Paragraph("")
        }
    }

    private fun resolveArticleVideo(model: VideoModel): NewsArticle {
        return NewsArticle.Video(model)
    }

    private fun resolveArticleGallery(model: GalleryModel): NewsArticle {
        return NewsArticle.Gallery(model)
    }

    private fun resolveArticleQuote(value: String): NewsArticle {
        return NewsArticle.Quote(value)
    }

    private fun resolveArticleHeader(tag: String, value: String): NewsArticle {
        return when (tag[1]) {
            '1' -> NewsArticle.Header1(value)
            '2' -> NewsArticle.Header2(value)
            else -> NewsArticle.Header3(value)
        }
    }

    private fun resolveArticleList(model: List<String>): NewsArticle {
        return NewsArticle.OrderedList(model)
    }

    private fun shouldPauseTraverse(): Boolean {
        return articleParserConfig.shouldPause
    }

    fun enablePause() {
        articleParserConfig.shouldPause = true
    }

    fun disablePause() {
        articleParserConfig.shouldPause = false
    }

    suspend fun traverseElement(element: Node): List<NewsArticle> {
        return withContext(Dispatchers.IO) {
            element.traverse(object : NodeVisitor {
                override fun head(node: Node, depth: Int) {
                    if (shouldPauseTraverse()) {
                        return
                    }
                    if (node is TextNode) {
                        val word = node.text()
                        articlePiece = resolveArticleParagraph(word)
                    } else if (node is Element) {
                        if (node.isContainer()) {
                            enablePause()
                        }
                        if (node.isText()) {
                            if (node.getTextType() is LinkText) {
                                articlePiece = openArticleLink()
                            } else if (node.getTextType() is ItalicText) {
                                articlePiece = openArticleItalic()
                            } else if (node.getTextType() is BoldText) {
                                articlePiece = openArticleBold()
                            }
                        } else if (node.isVideo()) {
                            val videoElement = node.allElements.findLast { it.tagName() == "video" }
                            if (videoElement != null) {
                                val thumbnail = videoElement.attr("thumbnail")
                                val videoDetailsContainer =
                                    node.allElements.findLast { it.hasClass("video-header") }!!
                                val videoDurationContainer =
                                    node.allElements.findLast { it.hasClass("duration") }!!
                                val title = videoDetailsContainer.children()[0].text()
                                val url = videoDetailsContainer.children()[0].attr("href")
                                val duration = videoDurationContainer.text()

                                articlePiece =
                                    resolveArticleVideo(VideoModel(title, thumbnail, url, duration))
                            }
                        } else if (node.isGallery()) {
                            val title =
                                node.allElements.find { it.classNames().contains("title") }!!.text()
                            val imageElements = node.allElements.filter { it.tagName() == "img" }
                            val images = imageElements.map {
                                val url = it.attr("src")
                                val alt = it.attr("alt")
                                ImageModel(url, alt)
                            }
                            articlePiece = resolveArticleGallery(GalleryModel(title, images))
                        } else if (node.isQuoteContainer()) {
                            if (node.childNodeSize() > 0 && node.childNode(0) is TextNode) {
                                val text = (node.childNode(0) as TextNode).text()
                                articlePiece = resolveArticleQuote(text)
                            }
                        } else if (node.isHeaderContainer()) {
                            val text = node.text()
                            articlePiece = resolveArticleHeader(node.tagName(), text)
                        } else if (node.isList()) {
                            val listTags = node.allElements.filter { it.tagName() == "li" }
                            val listItems = ArrayList<String>()

                            runBlocking(Dispatchers.IO) {
                                val temp = listTags.map {
                                    val articleParser = ArticleParser().apply {
                                        articleParserConfig = ArticleParserConfig()
                                    }
                                    articleParser.traverseElement(it)
                                }
                                temp.forEach {
                                    listItems.addAll(it.filterIsInstance<NewsArticle.Paragraph>()
                                        .map { it.value })
                                }
                                articlePiece = resolveArticleList(listItems)
                            }
                        }
                    }
                }

                override fun tail(node: Node, depth: Int) {
                    if (node is Element) {
                        if (node.isContainer()) {
                            disablePause()
                        }
                        if (!shouldPauseTraverse()) {
                            if (node.isText()) {
                                if (node.getTextType() is LinkText) {
                                    val link = node.attr("href")
                                    articlePiece = closeArticleLink(link)
                                } else if (node.getTextType() is ItalicText) {
                                    articlePiece = closeArticleItalic()
                                } else if (node.getTextType() is BoldText) {
                                    articlePiece = closeArticleBold()
                                }
                            }
                        }
                    }
                    if (depth == articleParserConfig.breakDepth) {
                        if(node is Element && node.wholeText().isEmpty()){
                            return
                        }
                        addToArticle()
                        addSpacer()
                    }
                }
            })

            article
        }
    }
}