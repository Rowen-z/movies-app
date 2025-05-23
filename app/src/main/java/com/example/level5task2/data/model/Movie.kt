package com.example.level5task2.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int
)
