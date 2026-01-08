package com.androidapp2_0.superpodcast.network

import com.google.gson.annotations.SerializedName

// Root response from iTunes API search
data class PodcastResponse(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<Podcast>
)

// Podcast model data
data class Podcast(
    @SerializedName("collectionId")
    val collectionId: Long,
    @SerializedName("collectionName")
    val collectionName: String,
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String,
    @SerializedName("feedUrl")
    val feedUrl: String?
)