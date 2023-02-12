package com.example.webviewadapp.api.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsModel (
        val author: String?,
        val title: String?,
        val description: String?,
        val url: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val content: String?
) : Parcelable