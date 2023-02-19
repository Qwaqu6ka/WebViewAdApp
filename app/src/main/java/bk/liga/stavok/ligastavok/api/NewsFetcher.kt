package bk.liga.stavok.ligastavok.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsFetcher {
    companion object {
        val getApi: SportNewsApi by lazy {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://newsapi.org/")
                .build()
            retrofit.create(SportNewsApi::class.java)
        }
    }
}