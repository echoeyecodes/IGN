package com.echoeyecodes.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.echoeyecodes.newsapp.R
import com.echoeyecodes.newsapp.databinding.*
import com.echoeyecodes.newsapp.models.GalleryModel
import com.echoeyecodes.newsapp.models.ImageModel
import com.echoeyecodes.newsapp.models.VideoModel
import com.echoeyecodes.newsapp.utils.NewsArticle
import com.echoeyecodes.newsapp.utils.NewsArticleItemCallback
import com.echoeyecodes.newsapp.utils.NewsHeader
import com.echoeyecodes.newsapp.utils.styleArticleText

class NewsAdapter() :
    ListAdapter<NewsArticle, NewsAdapter.BaseViewHolder>(NewsArticleItemCallback()) {

    companion object {
        const val PARAGRAPH = 0
        const val SPACER = 1
        const val VIDEO = 2
        const val GALLERY = 3
        const val QUOTE = 4
        const val HEADER = 5
        const val LIST = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = when (viewType) {
            SPACER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_spacer, parent, false)
                NewsAdapterSpacerViewHolder(view)
            }
            VIDEO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_video, parent, false)
                NewsAdapterVideoViewHolder(view)
            }
            GALLERY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_gallery, parent, false)
                NewsAdapterGalleryViewHolder(view)
            }
            QUOTE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_quote, parent, false)
                NewsAdapterQuoteViewHolder(view)
            }
            HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_header, parent, false)
                NewsAdapterHeaderViewHolder(view)
            }
            LIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_list, parent, false)
                NewsAdapterListViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_paragraph, parent, false)
                NewsAdapterViewHolder(view)
            }
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            getItem(position) is NewsArticle.Spacing -> {
                SPACER
            }
            getItem(position) is NewsArticle.Video -> {
                VIDEO
            }
            getItem(position) is NewsArticle.Gallery -> {
                GALLERY
            }
            getItem(position) is NewsArticle.Quote -> {
                QUOTE
            }
            getItem(position) is NewsHeader -> {
                HEADER
            }
            getItem(position) is NewsArticle.OrderedList -> {
                LIST
            }
            else -> {
                PARAGRAPH
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is NewsAdapterViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.Paragraph).value)
            }
            is NewsAdapterVideoViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.Video).model)
            }
            is NewsAdapterQuoteViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.Quote).value)
            }
            is NewsAdapterHeaderViewHolder -> {
                val header = (getItem(position)) as NewsHeader
                holder.bind(header, header.value)
            }
            is NewsAdapterGalleryViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.Gallery).model)
            }
            is NewsAdapterListViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.OrderedList).items)
            }
        }
    }

    open inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class NewsAdapterSpacerViewHolder(view: View) : BaseViewHolder(view)

    inner class NewsAdapterVideoViewHolder(view: View) : BaseViewHolder(view) {
        private val binding = LayoutArticleVideoBinding.bind(view)
        private val image = binding.image
        private val title = binding.title
        private val timestamp = binding.timestamp

        fun bind(model: VideoModel) {
            Glide.with(image).load(model.thumbnail).into(image)
            title.text = model.title
            timestamp.text = model.duration
        }
    }

    inner class NewsAdapterViewHolder(private val view: View) : BaseViewHolder(view) {
        private val textView = LayoutArticleParagraphBinding.bind(view).root

        fun bind(model: String) {
            textView.text = model.styleArticleText(view.context)
        }
    }

    inner class NewsAdapterQuoteViewHolder(private val view: View) : BaseViewHolder(view) {
        private val textView = LayoutArticleQuoteBinding.bind(view).root

        fun bind(model: String) {
            textView.text = "".plus("\"").plus(model).plus("\"")
        }
    }

    inner class NewsAdapterListViewHolder(private val view: View) : BaseViewHolder(view) {
        private val recyclerView = LayoutArticleListBinding.bind(view).root

        fun bind(model: List<String>) {
            val  layoutManager = LinearLayoutManager(view.context)
            val adapter = ArticleListAdapter()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.submitList(model)
        }
    }

    inner class NewsAdapterGalleryViewHolder(view: View) : BaseViewHolder(view) {
        private val binding = LayoutArticleGalleryBinding.bind(view)
        private val viewPager = binding.viewPager
        private val title = binding.title

        fun bind(model: GalleryModel) {
            title.text = model.title

            val adapter = ArticleGalleryImageAdapter()
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.adapter = adapter
            adapter.submitList(model.images)
        }
    }

    inner class NewsAdapterHeaderViewHolder(private val view: View) : BaseViewHolder(view) {
        private val textView = LayoutArticleHeaderBinding.bind(view).root

        fun bind(header: NewsHeader, value: String) {
            //use {header} variable to determine the text size like h1, h2, h3
            textView.text = value
        }
    }
}