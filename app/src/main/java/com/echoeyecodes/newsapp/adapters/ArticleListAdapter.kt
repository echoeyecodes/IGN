package com.echoeyecodes.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echoeyecodes.newsapp.R
import com.echoeyecodes.newsapp.databinding.LayoutArticleListItemBinding
import com.echoeyecodes.newsapp.utils.DefaultItemCallback

class ArticleListAdapter() :
    ListAdapter<String, ArticleListAdapter.ArticleListAdapterViewHolder>(DefaultItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_article_list_item, parent, false)
        return ArticleListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleListAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleListAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = LayoutArticleListItemBinding.bind(view)
        private val textView = binding.text

        fun bind(model: String) {
            textView.text = model
        }
    }
}