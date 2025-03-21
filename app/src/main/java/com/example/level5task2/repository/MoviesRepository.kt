package com.example.level5task2.repository

import android.util.Log
import com.example.level5task2.data.api.MoviesApi
import com.example.level5task2.data.api.MoviesApiService
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.MoviesResponse
import kotlinx.coroutines.withTimeout

class MoviesRepository {
    private val _moviesApiService: MoviesApiService = MoviesApi.createApi()

    suspend fun getMovies(query: String, page: Int): Resource<MoviesResponse> {
        val response = try {
            withTimeout(5_000) {
                _moviesApiService.getMovies(query, page)
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", e.message ?: "No exception message available")
            return Resource.Error("An unknown error occurred while fetching data from the movies api.")
        }

        return Resource.Success(response)
    }
}