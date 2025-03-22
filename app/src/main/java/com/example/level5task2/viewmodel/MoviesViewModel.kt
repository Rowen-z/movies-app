package com.example.level5task2.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.Movie
import com.example.level5task2.data.model.MoviesResponse
import com.example.level5task2.repository.MoviesInFirestoreRepository
import com.example.level5task2.repository.MoviesRepository
import kotlinx.coroutines.launch

class MoviesViewModel(application: Application): AndroidViewModel(application) {
    private val _moviesRepository = MoviesRepository()
    private val _moviesInFirestoreRepository = MoviesInFirestoreRepository()

    private val _moviesResource: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData(Resource.Empty())
    private val _moviesFromFirestoreResource: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Empty())
    private val _movieToFirestoreResource: MutableLiveData<Resource<String>> = MutableLiveData(Resource.Empty())

    private val _movies = mutableStateOf<List<Movie>>(emptyList())
    private val _page = mutableIntStateOf(1)
    private val _selectedMovie = mutableStateOf<Movie?>(null)
    private val _favoriteStatusMap = mutableStateOf<Map<Int, Boolean>>(emptyMap())

    val moviesResource: LiveData<Resource<MoviesResponse>>
        get() = _moviesResource

    val moviesFromFirestoreResource: LiveData<Resource<List<Movie>>>
        get() = _moviesFromFirestoreResource

    val movieToFirestoreResource: LiveData<Resource<String>>
        get() = _movieToFirestoreResource

    fun getMovies(query: String, page: Int = 1) {
        _moviesResource.value = Resource.Loading()

        viewModelScope.launch {
            _moviesResource.value = _moviesRepository.getMovies(query, page)
        }
    }

    fun readMovies(): List<Movie> {
        return _movies.value
    }

    fun addMovies(newMovies: List<Movie>) {
        if (_page.intValue == 1) {
            _movies.value = newMovies
        } else {
            _movies.value = _movies.value + newMovies
        }
    }

    fun addMovieToFirestore(movie: Movie) {
        _movieToFirestoreResource.value = Resource.Loading()

        viewModelScope.launch {
            _movieToFirestoreResource.value =
                _moviesInFirestoreRepository.addFavoriteMovieToFirestore(movie)
        }

        updateFavoriteStatus(movie.id, true)
    }

    fun getFavoritesFromFirestore() {
        _moviesFromFirestoreResource.value = Resource.Loading()

        viewModelScope.launch {
            _moviesFromFirestoreResource.value =
                _moviesInFirestoreRepository.getFavoritesFromFirestore()
        }
    }

    fun deleteFavoriteFromFirestore(movieId: Int) {
        viewModelScope.launch {
            val result = _moviesInFirestoreRepository.deleteFavoriteFromFirestore(movieId)
            if (result is Resource.Success) {
                updateFavoriteStatus(movieId, false)
            }
            _movieToFirestoreResource.value = result
        }
    }

    fun deleteFavorites() {
        viewModelScope.launch {
            _moviesInFirestoreRepository.deleteFavorites()
        }
    }

    fun readPage(): Int {
        return _page.intValue
    }

    fun setPage(page: Int) {
        _page.intValue = page
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun readSelectedMovie(): Movie? {
        return _selectedMovie.value
    }

    fun isFavorite(movieId: Int): Boolean {
        return _favoriteStatusMap.value[movieId] == true
    }

    private fun updateFavoriteStatus(movieId: Int, isFavorite: Boolean) {
        _favoriteStatusMap.value = _favoriteStatusMap.value.toMutableMap().apply {
            put(movieId, isFavorite)
        }
    }
}