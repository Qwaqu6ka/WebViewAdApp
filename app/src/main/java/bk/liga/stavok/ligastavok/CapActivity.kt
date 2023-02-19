package bk.liga.stavok.ligastavok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import bk.liga.stavok.ligastavok.api.NewsFetcher
import bk.liga.stavok.ligastavok.api.models.NewsSiteResponse
import bk.liga.stavok.ligastavok.databinding.ActivityCapBinding
import bk.liga.stavok.ligastavok.recycler.RecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)

        NewsFetcher.getApi.getNews("https://newsapi.org/v2/top-headlines?country=us&category=sport&apiKey=076eba9fd7ea4c09b516dc90bb537309")
            .enqueue(object : Callback<NewsSiteResponse> {
                override fun onResponse(call: Call<NewsSiteResponse>, response: Response<NewsSiteResponse>) {
                    if (response.isSuccessful) {
                        val list = response.body()?.articles
                        if (list != null)
                            binding.recycler.adapter = RecyclerAdapter(list)
                        binding.recycler.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.GONE
                    } else {
                        Toast.makeText(
                            this@CapActivity,
                            "An error occurred while downloading from the server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NewsSiteResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CapActivity,
                        "An error occurred while downloading from the server",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}