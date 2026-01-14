package com.androidapp2_0.superpodcast

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_PREVIEW_URL = "extra_preview_url"
        const val EXTRA_ARTWORK_URL = "extra_artwork_url"
    }

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playPauseButton: Button
    private lateinit var imageArtwork: ImageView
    private lateinit var buttonVolumeUp: Button
    private lateinit var buttonVolumeDown: Button
    private lateinit var buttonBack: Button
    private lateinit var audioManager: AudioManager
    private lateinit var seekBarAudio: SeekBar
    private lateinit var progressBarAudio: ProgressBar

    private var isPlaying = false
    private var previewUrl: String? = null
    private var updateSeekBarJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        imageArtwork = findViewById(R.id.imageArtwork)
        playPauseButton = findViewById(R.id.buttonPlayPause)
        buttonVolumeUp = findViewById(R.id.buttonVolumeUp)
        buttonVolumeDown = findViewById(R.id.buttonVolumeDown)
        buttonBack = findViewById(R.id.buttonBack)
        seekBarAudio = findViewById(R.id.seekBarAudio)
        progressBarAudio = findViewById(R.id.progressBarAudio)

        buttonBack.setOnClickListener {
            finish()
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "No Title"
        val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "No Description"
        previewUrl = intent.getStringExtra(EXTRA_PREVIEW_URL)
        val artworkUrl = intent.getStringExtra(EXTRA_ARTWORK_URL)

        findViewById<TextView>(R.id.textTitle).text = title
        findViewById<TextView>(R.id.textArtist).text = artist
        findViewById<TextView>(R.id.textDescription).text = description

        if (!artworkUrl.isNullOrEmpty()) {
            Glide.with(this).load(artworkUrl).into(imageArtwork)
        }

        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        playPauseButton.setOnClickListener {
            previewUrl?.let { url ->
                if (isPlaying) {
                    pauseAudio()
                } else {
                    if (url.endsWith(".mp3") || url.endsWith(".m4a")) {
                        playAudio(url)
                    } else {
                        loadAudioFromFeed(url)
                    }
                }
            } ?: Toast.makeText(this, "Audio URL not available", Toast.LENGTH_SHORT).show()
        }

        buttonVolumeUp.setOnClickListener {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI
            )
        }

        buttonVolumeDown.setOnClickListener {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
        }
    }

    private fun loadAudioFromFeed(feedUrl: String) {
        Thread {
            val audioUrl = getFirstAudioUrlFromFeed(feedUrl)

            runOnUiThread {
                if (audioUrl != null) {
                    playAudio(audioUrl)
                } else {
                    Toast.makeText(this, "No audio found in feed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getFirstAudioUrlFromFeed(feedUrl: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(feedUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return null

            val xml = response.body?.string() ?: return null

            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "enclosure") {
                    val url = parser.getAttributeValue(null, "url")
                    if (url != null && (url.endsWith(".mp3") || url.endsWith(".m4a"))) {
                        return url
                    }
                }
                eventType = parser.next()
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun playAudio(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    it.start()
                    this@DetailActivity.isPlaying = true
                    playPauseButton.text = "Pause"
                    progressBarAudio.visibility = ProgressBar.GONE
                    seekBarAudio.max = it.duration

                    updateSeekBarJob = lifecycleScope.launch {
                        while (isPlaying) {
                            seekBarAudio.progress = it.currentPosition
                            delay(500)
                        }
                    }
                }
                setOnCompletionListener {
                    this@DetailActivity.isPlaying = false
                    playPauseButton.text = "Play"
                    seekBarAudio.progress = 0
                    updateSeekBarJob?.cancel()
                }
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(this@DetailActivity, "Error playing audio", Toast.LENGTH_SHORT).show()
                    this@DetailActivity.isPlaying = false
                    playPauseButton.text = "Play"
                    updateSeekBarJob?.cancel()
                    progressBarAudio.visibility = ProgressBar.GONE
                    true
                }
                prepareAsync()
                progressBarAudio.visibility = ProgressBar.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Cannot play audio", Toast.LENGTH_SHORT).show()
            progressBarAudio.visibility = ProgressBar.GONE
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        playPauseButton.text = "Play"
        updateSeekBarJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        updateSeekBarJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}