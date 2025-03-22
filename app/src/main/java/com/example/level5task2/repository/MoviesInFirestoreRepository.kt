package com.example.level5task2.repository
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

    suspend fun getFavoritesFromFirestore(): Resource<List<String>> {
        val historyList = arrayListOf<String>()
        try {
            withTimeout(5_000) {
                _moviesDocument
                    .get().addOnSuccessListener {
                        for (document in it) {
                            val text = document.getString("text")
                            historyList.add(text!!)
                        }
                    }
                    .await()
            }
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured while retrieving movie data from Firestore.")
        }
        return Resource.Success(historyList)
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