package com.androidapp2_0.superpodcast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PodcastAdapter
    private lateinit var editSearch: EditText
    private lateinit var buttonSearch: Button

    private val client = OkHttpClient()
    private val podcastList = mutableListOf<Podcast>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerPodcasts)
        editSearch = findViewById(R.id.editSearch)
        buttonSearch = findViewById(R.id.buttonSearch)

        adapter = PodcastAdapter(podcastList) { podcast ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_TITLE, podcast.collectionName)
                putExtra(DetailActivity.EXTRA_ARTIST, podcast.artistName)
                putExtra(DetailActivity.EXTRA_DESCRIPTION, podcast.description ?: "")
                putExtra(DetailActivity.EXTRA_PREVIEW_URL, podcast.feedUrl)
                putExtra(DetailActivity.EXTRA_ARTWORK_URL, podcast.artworkUrl100)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonSearch.setOnClickListener {
            val term = editSearch.text.toString().trim()
            if (term.isNotEmpty()) {
                searchPodcasts(term)
            }
        }

        searchPodcasts("news") // búsqueda por defecto fija
    }

    private fun searchPodcasts(term: String) {
        val url = "https://itunes.apple.com/search?entity=podcast&term=${term}"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error searching podcasts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error searching podcasts", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val body = response.body?.string() ?: ""

                val json = JSONObject(body)
                val results = json.getJSONArray("results")

                podcastList.clear()

                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    val feedUrl = item.optString("feedUrl", "")
                    if (feedUrl.isEmpty()) continue // Ignorar este item si no tiene feedUrl

                    val podcast = Podcast(
                        collectionName = item.getString("collectionName"),
                        artistName = item.getString("artistName"),
                        feedUrl = feedUrl,
                        description = item.optString("description", ""),
                        artworkUrl100 = item.optString("artworkUrl100", "")
                    )
                    podcastList.add(podcast)
                }

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    data class Podcast(
        val collectionName: String,
        val artistName: String,
        val feedUrl: String,
        val description: String?,
        val artworkUrl100: String?
    )
}