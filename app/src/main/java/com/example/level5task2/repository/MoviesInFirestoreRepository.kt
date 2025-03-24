package com.example.level5task2.repository
import android.util.Log
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class MoviesInFirestoreRepository {
    private val _firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _moviesDocument =
        _firestore.collection("movies")

    suspend fun addFavoriteMovieToFirestore(movie: Movie): Resource<String> {
        val data = hashMapOf(
            "id" to movie.id,
            "title" to movie.title,
            "original_title" to movie.originalTitle,
            "overview" to movie.overview,
            "poster_path" to movie.posterPath,
            "backdrop_path" to movie.backdropPath,
            "media_type" to movie.mediaType,
            "adult" to movie.adult,
            "original_language" to movie.originalLanguage,
            "genre_ids" to movie.genreIds,
            "popularity" to movie.popularity,
            "release_date" to movie.releaseDate,
            "video" to movie.video,
            "vote_average" to movie.voteAverage,
            "vote_count" to movie.voteCount
        )
        try {
            withTimeout(5_000) {
                _moviesDocument
                    .add(data)
                    .await()
            }
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured while uploading movie to Firestore.")
        }
        return Resource.Success("Success")
    }

    suspend fun getFavoritesFromFirestore(): Resource<List<Movie>> {
        try {
            val snapshot = withTimeout(5_000) {
                _moviesDocument
                    .get()
                    .await()
            }

            val movies = snapshot.documents.mapNotNull { document ->
                val id = document.getLong("id")?.toInt() ?: 0
                val title = document.getString("title") ?: ""
                val originalTitle = document.getString("original_title") ?: ""
                val overview = document.getString("overview") ?: ""
                val posterPath = document.getString("poster_path") ?: ""
                val backdropPath = document.getString("backdrop_path") ?: ""
                val mediaType = document.getString("media_type") ?: ""
                val adult = document.getBoolean("adult") ?: false
                val originalLanguage = document.getString("original_language") ?: ""
                val genreIds = document.get("genre_ids") as? List<Int> ?: emptyList()
                val popularity = document.getDouble("popularity") ?: 0.0
                val releaseDate = document.getString("release_date") ?: ""
                val video = document.getBoolean("video") ?: false
                val voteAverage = document.getDouble("vote_average") ?: 0.0
                val voteCount = document.getLong("vote_count")?.toInt() ?: 0

                Movie(
                    id,
                    title,
                    originalTitle,
                    overview,
                    posterPath,
                    backdropPath,
                    mediaType,
                    adult,
                    originalLanguage,
                    genreIds,
                    popularity,
                    releaseDate,
                    video,
                    voteAverage,
                    voteCount
                )
            }
            return Resource.Success(movies)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured while retrieving the movies from Firestore.")
        }
    }

    suspend fun deleteFavoriteFromFirestore(movieId: Int): Resource<String> {
        return try {
            withTimeout(5_000) {
                val snapshot = _moviesDocument
                    .whereEqualTo("id", movieId)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    throw Exception("Movie not found in Firestore.")
                }

                for (document in snapshot.documents) {
                    document.reference.delete().await()
                }
            }
            Resource.Success("Movie deleted successfully")
        } catch (e: Exception) {
            Resource.Error("An error occurred while deleting the movie: ${e.message}")
        }
    }

    suspend fun deleteFavorites(): Resource<String> {
        try {
            withTimeout(5_000) {
                _moviesDocument
                    .get().addOnSuccessListener {
                        for (document in it) {
                            document.reference.delete()
                        }
                    }
                    .await()
            }
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured while deleting history from Firestore.")
        }
        return Resource.Success("Success")
    }
}