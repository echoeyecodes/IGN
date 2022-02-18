package com.echoeyecodes.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoeyecodes.newsapp.R
import com.echoeyecodes.newsapp.databinding.LayoutArticleParagraphBinding
import com.echoeyecodes.newsapp.databinding.LayoutArticleVideoBinding
import com.echoeyecodes.newsapp.models.VideoModel
import com.echoeyecodes.newsapp.utils.NewsArticle
import com.echoeyecodes.newsapp.utils.NewsArticleItemCallback

class NewsAdapter() :
    ListAdapter<NewsArticle, NewsAdapter.BaseViewHolder>(NewsArticleItemCallback()) {

    companion object {
        const val PARAGRAPH = 0
        const val SPACER = 1
        const val VIDEO = 2
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
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_article_paragraph, parent, false)
                NewsAdapterViewHolder(view)
            }
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is NewsArticle.Spacing) {
            SPACER
        } else if (getItem(position) is NewsArticle.Video) {
            VIDEO
        } else {
            PARAGRAPH
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is NewsAdapterViewHolder) {
            holder.bind((getItem(position) as NewsArticle.Paragraph).value)
        } else if (holder is NewsAdapterVideoViewHolder) {
            holder.bind((getItem(position) as NewsArticle.Video).model)
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

    inner class NewsAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val textView = LayoutArticleParagraphBinding.bind(view).root

        fun bind(model: String) {
            textView.text = model
        }
    }
}