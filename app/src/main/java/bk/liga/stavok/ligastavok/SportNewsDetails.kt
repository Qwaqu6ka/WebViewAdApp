package bk.liga.stavok.ligastavok

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bk.liga.stavok.ligastavok.api.NewsFetcher
import bk.liga.stavok.ligastavok.api.models.NewsModel
import bk.liga.stavok.ligastavok.databinding.ActivitySportNewsDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val NEWS_EXTRA = "SportNewsDetailsNewsExtraKey"

class SportNewsDetails : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context, news: NewsModel) =
            Intent(context, SportNewsDetails::class.java).putExtra(NEWS_EXTRA, news)
    }

    private lateinit var binding: ActivitySportNewsDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val news = intent.getParcelableExtra<NewsModel>(NEWS_EXTRA)!!
        binding.title.text = news.title ?: ""
        binding.content.text = news.content ?: ""
        news.urlToImage?.let {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val scope = CoroutineScope(Dispatchers.Default)
                    val bitmapDef = scope.async {
                        val response = NewsFetcher.getApi.getImage(it).execute()
                        response.body()?.byteStream().use(BitmapFactory::decodeStream)
                    }
                    val bitmap = bitmapDef.await()
                    binding.image.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.d("debug", "Error with image loading", e)
                }
            }
        }
    }
}