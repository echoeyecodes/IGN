package com.echoeyecodes.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.echoeyecodes.newsapp.R
import com.echoeyecodes.newsapp.databinding.LayoutArticleGalleryBinding
import com.echoeyecodes.newsapp.databinding.LayoutArticleParagraphBinding
import com.echoeyecodes.newsapp.databinding.LayoutArticleVideoBinding
import com.echoeyecodes.newsapp.models.GalleryModel
import com.echoeyecodes.newsapp.models.ImageModel
import com.echoeyecodes.newsapp.models.VideoModel
import com.echoeyecodes.newsapp.utils.NewsArticle
import com.echoeyecodes.newsapp.utils.NewsArticleItemCallback

class NewsAdapter() :
    ListAdapter<NewsArticle, NewsAdapter.BaseViewHolder>(NewsArticleItemCallback()) {

    companion object {
        const val PARAGRAPH = 0
        const val SPACER = 1
        const val VIDEO = 2
        const val GALLERY = 3
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
        } else if (getItem(position) is NewsArticle.Gallery) {
            GALLERY
        } else {
            PARAGRAPH
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
            is NewsAdapterGalleryViewHolder -> {
                holder.bind((getItem(position) as NewsArticle.Gallery).model)
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

    inner class NewsAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val textView = LayoutArticleParagraphBinding.bind(view).root

        fun bind(model: String) {
            textView.text = model
        }
    }

    inner class NewsAdapterGalleryViewHolder(view: View) : BaseViewHolder(view) {
        private val binding = LayoutArticleGalleryBinding.bind(view)
        private val viewPager = binding.viewPager
        private val title = binding.title

        fun bind(model:GalleryModel) {
            title.text = model.title

            val adapter = ArticleGalleryImageAdapter()
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.adapter = adapter
            adapter.submitList(model.images)
        }
    }
}