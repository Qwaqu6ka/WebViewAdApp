package com.example.webviewadapp.recycler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.webviewadapp.SportNewsDetails
import com.example.webviewadapp.api.NewsFetcher
import com.example.webviewadapp.api.models.NewsModel
import com.example.webviewadapp.databinding.NewsItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecyclerAdapter(private val list: List<NewsModel>) :
    RecyclerView.Adapter<RecyclerAdapter.NewsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(layoutInflater, parent, false)
        return NewsHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val newsItem = list[position]
        holder.bind(newsItem)
        val imageUrl = newsItem.urlToImage
        if (imageUrl != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bitmapDef = CoroutineScope(Dispatchers.Default).async {
                        val response = NewsFetcher.getApi.getImage(imageUrl).execute()
                        response.body()?.byteStream().use(BitmapFactory::decodeStream)
                    }
                    val bitmap = bitmapDef.await()
                    if (bitmap != null)
                        holder.bindImage(bitmap)
                } catch (e: Exception) {
                    Log.d("debug", "Error with image loading", e)
                }
            }
        }
        holder.itemView.setOnClickListener { view ->
            val intent = SportNewsDetails.getIntent(view.context, newsItem)
            view.context.startActivity(intent)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NewsHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsModel) {
            binding.newsTitle.text = news.title
            binding.newsDescription.text = news.description
        }

        fun bindImage(image: Bitmap) {
            binding.newsImg.setImageBitmap(image)
        }
    }
}