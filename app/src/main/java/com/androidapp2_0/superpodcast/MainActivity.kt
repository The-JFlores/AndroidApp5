package com.androidapp2_0.superpodcast

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidapp2_0.superpodcast.adapter.PodcastAdapter
import com.androidapp2_0.superpodcast.network.PodcastResponse
import com.androidapp2_0.superpodcast.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var podcastAdapter: PodcastAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerPodcasts)
        val editSearch = findViewById<EditText>(R.id.editSearch)
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)

        podcastAdapter = PodcastAdapter(emptyList()) { podcast ->
            // TODO: handle click, e.g., open details or play podcast
        }

        recyclerView.adapter = podcastAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonSearch.setOnClickListener {
            val query = editSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPodcasts(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        // Load initial data with default search term
        searchPodcasts("technology")
    }

    private fun searchPodcasts(term: String) {
        RetrofitClient.instance.searchPodcasts(term)
            .enqueue(object : Callback<PodcastResponse> {
                override fun onResponse(call: Call<PodcastResponse>, response: Response<PodcastResponse>) {
                    if (response.isSuccessful) {
                        val podcasts = response.body()?.results ?: emptyList()
                        podcastAdapter = PodcastAdapter(podcasts) { podcast ->
                            // TODO: handle click on podcast item
                        }
                        recyclerView.adapter = podcastAdapter
                    } else {
                        Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PodcastResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}