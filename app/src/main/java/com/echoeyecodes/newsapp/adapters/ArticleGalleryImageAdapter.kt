package com.echoeyecodes.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoeyecodes.newsapp.R
import com.echoeyecodes.newsapp.databinding.LayoutArticleGalleryItemBinding
import com.echoeyecodes.newsapp.models.ImageModel
import com.echoeyecodes.newsapp.utils.ImageModelItemCallback

class ArticleGalleryImageAdapter() :
    ListAdapter<ImageModel, ArticleGalleryImageAdapter.ArticleGalleryImageAdapterViewHolder>(ImageModelItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleGalleryImageAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_article_gallery_item, parent, false)
        return ArticleGalleryImageAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleGalleryImageAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleGalleryImageAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = LayoutArticleGalleryItemBinding.bind(view)
        private val image = binding.root

        fun bind(model: ImageModel) {
            Glide.with(image).load(model.getThumbnail()).into(image)
        }
    }
}