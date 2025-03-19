package com.example.level5task2.data.api

import com.example.level5task2.data.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {
    @GET("search/movie")
    suspend fun getMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MoviesResponse
}