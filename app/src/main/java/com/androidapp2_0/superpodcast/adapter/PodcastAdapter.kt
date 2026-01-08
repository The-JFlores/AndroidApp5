package com.androidapp2_0.superpodcast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidapp2_0.superpodcast.R
import com.androidapp2_0.superpodcast.network.Podcast
import com.squareup.picasso.Picasso

class PodcastAdapter(
    private val podcasts: List<Podcast>,
    private val onItemClick: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageArtwork: ImageView = itemView.findViewById(R.id.imageArtwork)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textArtist: TextView = itemView.findViewById(R.id.textArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_podcast, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
        holder.textTitle.text = podcast.collectionName
        holder.textArtist.text = podcast.artistName
        Picasso.get().load(podcast.artworkUrl100).into(holder.imageArtwork)
        holder.itemView.setOnClickListener {
            onItemClick(podcast)
        }
    }

    override fun getItemCount(): Int = podcasts.size
}