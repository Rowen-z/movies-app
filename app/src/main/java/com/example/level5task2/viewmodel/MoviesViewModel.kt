package com.example.level5task2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.MoviesResponse
import com.example.level5task2.repository.MoviesRepository
import kotlinx.coroutines.launch

class MoviesViewModel(application: Application): AndroidViewModel(application) {
    private val _moviesRepository = MoviesRepository()

    private val _moviesResource: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData(Resource.Empty())

    val moviesResource: LiveData<Resource<MoviesResponse>>
        get() = _moviesResource

    fun getMovies(query: String, page: Int = 1) {
        _moviesResource.value = Resource.Loading()

        viewModelScope.launch {
            _moviesResource.value = _moviesRepository.getMovies(query, page)
        }
    }
}