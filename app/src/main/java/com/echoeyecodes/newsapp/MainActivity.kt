package com.echoeyecodes.newsapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echoeyecodes.newsapp.adapters.NewsAdapter
import com.echoeyecodes.newsapp.databinding.ActivityMainBinding
import com.echoeyecodes.newsapp.utils.*
import com.echoeyecodes.newsapp.utils.articleparser.ArticleParser
import com.echoeyecodes.newsapp.utils.articleparser.ArticleParserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView

    companion object {
        const val URL =
            "https://www.ign.com/articles/james-gunn-peacemaker-bisexual-character-john-cena"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        val adapter = NewsAdapter()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        textView = binding.text

        AndroidUtilities.log("Init")
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text =
            "[*Peace maker in sight*](https://www.google.com) and _another platform besides this_ text [text2](https://www.google.com) requiem".styleArticleText(
                this
            )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val htmlFile = assets.open("ign2.html")

                val document = Jsoup.parse(htmlFile, null, "")
                val body = document.getElementsByClass("article-content")
                val articleParser = ArticleParser().apply {
                    articleParserConfig = ArticleParserConfig(3)
                }

                val data = articleParser.traverseElement(body[0])
                runOnUiThread {
                    adapter.submitList(data)
                }
            }
        }

    }
}