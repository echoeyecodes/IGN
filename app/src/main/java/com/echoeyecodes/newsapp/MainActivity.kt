package com.echoeyecodes.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echoeyecodes.newsapp.adapters.NewsAdapter
import com.echoeyecodes.newsapp.databinding.ActivityMainBinding
import com.echoeyecodes.newsapp.models.*
import com.echoeyecodes.newsapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var recyclerView: RecyclerView
    var article = ArrayList<NewsArticle>()
    var articlePiece: NewsArticle? = null

    companion object {
        const val URL =
            "https://www.ign.com/articles/james-gunn-peacemaker-bisexual-character-john-cena"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        val adapter = NewsAdapter()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        AndroidUtilities.log("Init")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val htmlFile = assets.open("ign.html")

                val document = Jsoup.parse(htmlFile, null, "")
                val body = document.getElementsByClass("article-page")

                traverseElement(body[0])
                runOnUiThread {
                    adapter.submitList(article)
                }
            }
        }

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

    private fun closeArticleLink(link: String): NewsArticle {
        return if (articlePiece != null && articlePiece is NewsArticle.Paragraph) {
            NewsArticle.Paragraph((articlePiece as NewsArticle.Paragraph).value.plus("]".plus("(${link}) ")))
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

    private fun resolveArticleVideo(model: VideoModel): NewsArticle {
        return NewsArticle.Video(model)
    }

    private fun resolveArticleGallery(model: GalleryModel): NewsArticle {
        return NewsArticle.Gallery(model)
    }

    private fun shouldPauseTraverse(): Boolean {
        return articlePiece != null && articlePiece!!.isContainer()
    }

    private fun traverseElement(element: Node) {

        element.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                if (shouldPauseTraverse()) {
                    return
                }
                if (node is TextNode) {
                    val word = node.text()
                    articlePiece = resolveArticleParagraph(word)
                } else if (node is Element) {
                    if (node.isText()) {
                        if (node.getTextType() is LinkText) {
                            articlePiece = openArticleLink()
                        } else if (node.getTextType() is ItalicText) {
                            articlePiece = openArticleItalic()
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
                        val title = node.allElements.find { it.classNames().contains("title") }!!.text()
                        val imageElements = node.allElements.filter { it.tagName() == "img" }
                        val images = imageElements.map {
                            val url = it.attr("src")
                            val alt = it.attr("alt")
                            ImageModel(url, alt)
                        }
                        articlePiece = resolveArticleGallery(GalleryModel(title, images))
                    }
                }
            }

            override fun tail(node: Node, depth: Int) {
                if (!shouldPauseTraverse()) {
                    if (node is Element) {
                        if (node.isText()) {
                            if (node.getTextType() is LinkText) {
                                val link = node.attr("href")
                                articlePiece = closeArticleLink(link)
                            } else if (node.getTextType() is ItalicText) {
                                articlePiece = closeArticleItalic()
                            }
                        }
                    }
                }
                if (depth == 1) {
                    addToArticle()
                    addSpacer()
                }
            }
        })
    }
}