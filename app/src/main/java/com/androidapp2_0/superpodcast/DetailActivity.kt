package com.androidapp2_0.superpodcast

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_DESCRIPTION = "extra_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "No Title"
        val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "No Description"

        findViewById<TextView>(R.id.textTitle).text = title
        findViewById<TextView>(R.id.textArtist).text = artist
        findViewById<TextView>(R.id.textDescription).text = description
    }
}