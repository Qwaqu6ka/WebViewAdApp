package bk.liga.stavok.ligastavok.api.models

data class NewsSiteResponse(
    val status: String,
    val totalResults: Long,
    val articles: List<NewsModel>
)
