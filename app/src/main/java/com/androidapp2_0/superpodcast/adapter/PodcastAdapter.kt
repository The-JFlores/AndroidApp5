package com.androidapp2_0.superpodcast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PodcastAdapter(
    private val podcasts: List<MainActivity.Podcast>,
    private val onItemClick: (MainActivity.Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_podcast, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind(podcasts[position])
    }

    override fun getItemCount() = podcasts.size

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageArtwork: ImageView = itemView.findViewById(R.id.imageArtwork)
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textArtist: TextView = itemView.findViewById(R.id.textArtist)

        fun bind(podcast: MainActivity.Podcast) {
            textTitle.text = podcast.collectionName
            textArtist.text = podcast.artistName

            if (!podcast.artworkUrl100.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(podcast.artworkUrl100)
                    .into(imageArtwork)
            } else {
                imageArtwork.setImageResource(android.R.color.darker_gray)
            }

            itemView.setOnClickListener {
                onItemClick(podcast)
            }
        }
    }
}