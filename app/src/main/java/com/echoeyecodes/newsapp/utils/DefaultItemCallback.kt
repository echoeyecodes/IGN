package com.echoeyecodes.newsapp.utils

import androidx.recyclerview.widget.DiffUtil

class DefaultItemCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class NewsArticleItemCallback : DiffUtil.ItemCallback<NewsArticle>() {
    override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
        return oldItem == newItem
    }
}