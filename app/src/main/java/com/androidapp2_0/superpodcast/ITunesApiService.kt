package com.androidapp2_0.superpodcast.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interface for iTunes Search API
interface ITunesApiService {

    // Search podcasts endpoint with query param "term"
    @GET("search?entity=podcast")
    fun searchPodcasts(
        @Query("term") term: String
    ): Call<PodcastResponse>
}