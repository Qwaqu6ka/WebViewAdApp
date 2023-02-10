package com.example.webviewadapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.webviewadapp.databinding.NewsItemBinding
import com.example.webviewadapp.api.models.NewsModel

class RecyclerAdapter(private val list: List<NewsModel>): RecyclerView.Adapter<RecyclerAdapter.NewsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(layoutInflater, parent, false)
        return NewsHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(list[position])
    }

    class NewsHolder(private val binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsModel) {
            binding.newsTitle.text = news.title
            binding.newsDescription.text = news.description
        }
    }
}