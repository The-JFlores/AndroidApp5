package com.androidapp2_0.superpodcast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidapp2_0.superpodcast.network.PodcastResponse
import com.androidapp2_0.superpodcast.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrofit API call to search podcasts with term "technology"
        RetrofitClient.instance.searchPodcasts("technology")
            .enqueue(object : Callback<PodcastResponse> {
                override fun onResponse(
                    call: Call<PodcastResponse>,
                    response: Response<PodcastResponse>
                ) {
                    if (response.isSuccessful) {
                        val podcasts = response.body()?.results ?: emptyList()
                        podcasts.forEach {
                            println("Podcast: ${it.collectionName} by ${it.artistName}")
                        }
                    } else {
                        println("API call failed with code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PodcastResponse>, t: Throwable) {
                    println("API call failed: ${t.message}")
                }
            })
    }
}