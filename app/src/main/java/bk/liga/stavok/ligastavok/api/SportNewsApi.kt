package bk.liga.stavok.ligastavok.api

import bk.liga.stavok.ligastavok.api.models.NewsSiteResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface SportNewsApi {
    @GET
    fun getNews(@Url url: String): Call<NewsSiteResponse>

    @GET
    fun getImage(@Url url: String): Call<ResponseBody>
}