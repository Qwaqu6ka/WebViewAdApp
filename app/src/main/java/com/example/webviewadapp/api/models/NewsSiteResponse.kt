package com.example.webviewadapp.api.models

data class NewsSiteResponse(
    val status: String,
    val totalResults: Long,
    val articles: List<NewsModel>
)
