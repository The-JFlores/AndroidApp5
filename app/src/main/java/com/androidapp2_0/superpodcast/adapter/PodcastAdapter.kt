package com.androidapp2_0.superpodcast.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidapp2_0.superpodcast.DetailActivity
import com.androidapp2_0.superpodcast.R
import com.androidapp2_0.superpodcast.network.Podcast
import com.squareup.picasso.Picasso

class PodcastAdapter(
    private val context: Context,
    private val podcasts: List<Podcast>,
    private val itemClickListener: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_podcast, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
        holder.bind(podcast)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_TITLE, podcast.trackName)
                putExtra(DetailActivity.EXTRA_ARTIST, podcast.artistName)
                putExtra(DetailActivity.EXTRA_DESCRIPTION, podcast.description ?: "No description")
            }
            context.startActivity(intent)
            itemClickListener(podcast) // If you want to handle more click actions
        }
    }

    override fun getItemCount(): Int = podcasts.size

    class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textArtist: TextView = itemView.findViewById(R.id.textArtist)
        private val imageArtwork: ImageView = itemView.findViewById(R.id.imageArtwork)

        fun bind(podcast: Podcast) {
            textTitle.text = podcast.trackName
            textArtist.text = podcast.artistName
            Picasso.get().load(podcast.artworkUrl100).into(imageArtwork)
        }
    }
}