package com.example.level5task2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.level5task2.R
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.Movie
import com.example.level5task2.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesFavoriteScreen(nc: NavController, vm: MoviesViewModel) {
    // When opening the screen, retrieve favorite movies from firestore.
    LaunchedEffect(Unit) {
        vm.getFavoritesFromFirestore()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {nc.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { innerPadding -> ScreenContent(Modifier.padding(innerPadding), vm, nc) },
    )
}

@Composable
private fun ScreenContent(modifier: Modifier, vm: MoviesViewModel, nc: NavController) {
    val moviesFromFirestoreResource: Resource<List<Movie>> = vm.moviesFromFirestoreResource.observeAsState(Resource.Empty()).value

    Column(
        modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.favorite_movies),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        when (moviesFromFirestoreResource) {
            is Resource.Success -> {
                val movies = moviesFromFirestoreResource.data ?: emptyList()

                if (movies.isEmpty()) {
                    Text(
                        text = stringResource(R.string.empty_movies_list),
                        modifier = Modifier.padding(16.dp),
                    )
                }

                MovieGrid(movies, nc, vm)
            }

            is Resource.Error -> {
                Text(
                    text = moviesFromFirestoreResource.message ?: stringResource(R.string.something_wrong_state),
                    modifier = Modifier.padding(16.dp)
                )
            }

            is Resource.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            is Resource.Empty -> {
                Text(
                    text = stringResource(R.string.empty_movies_list),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun MovieGrid(movies: List<Movie>, nc: NavController, vm: MoviesViewModel) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies.size) { index ->
            MovieItem(movie = movies[index], onMovieClick = {
                vm.selectMovie(it)
                nc.navigate(MoviesScreens.MovieDetailScreen.name)
            })
        }
    }
}

@Composable
private fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    val imageUrl = movie.posterPath.let { "https://image.tmdb.org/t/p/w500$it" }

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Image(
        painter = rememberAsyncImagePainter(imageRequest),
        contentDescription = movie.title,
        modifier = Modifier
            .size(200.dp)
            .clickable { onMovieClick(movie) },
        contentScale = ContentScale.Crop
    )
}