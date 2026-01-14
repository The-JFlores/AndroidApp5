package com.androidapp2_0.superpodcast

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidapp2_0.superpodcast.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PodcastAdapter
    private lateinit var editSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var progressBar: ProgressBar

    private val podcastList = mutableListOf<Podcast>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerPodcasts)
        editSearch = findViewById(R.id.editSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        progressBar = findViewById(R.id.progressBar)

        adapter = PodcastAdapter(podcastList) { podcast ->
            val intent = android.content.Intent(this, DetailActivity::class.java).apply {
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
            } else {
                Toast.makeText(this, "Ingrese un término para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        searchPodcasts("news") // búsqueda por defecto fija
    }

    private fun searchPodcasts(term: String) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.searchPodcasts(term).execute()
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    podcastList.clear()
                    body?.results?.forEach { podcast ->
                        if (!podcast.feedUrl.isNullOrEmpty()) {
                            podcastList.add(
                                Podcast(
                                    collectionName = podcast.collectionName,
                                    artistName = podcast.artistName,
                                    feedUrl = podcast.feedUrl ?: "",
                                    description = "", // Podrías agregar descripción si la tienes
                                    artworkUrl100 = podcast.artworkUrl100
                                )
                            )
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al buscar podcasts: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    data class Podcast(
        val collectionName: String,
        val artistName: String,
        val feedUrl: String,
        val description: String?,
        val artworkUrl100: String?
    )
}