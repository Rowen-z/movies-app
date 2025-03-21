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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.level5task2.R
import com.example.level5task2.data.api.util.Resource
import com.example.level5task2.data.model.Movie
import com.example.level5task2.data.model.MoviesResponse
import com.example.level5task2.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesSearchScreen(nc: NavController, vm: MoviesViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
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
    var query by remember { mutableStateOf("") }

    Column(
        modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchView { newQuery ->
            query = newQuery
            vm.getMovies(newQuery)
        }

        val moviesResource: Resource<MoviesResponse> by vm.moviesResource.observeAsState(Resource.Empty())

        when (moviesResource) {
            is Resource.Success -> {
                val newMovies = moviesResource.data?.movies ?: emptyList()

                vm.addMovies(newMovies)

                val movies = vm.readMovies()

                MovieGrid(movies, nc, vm, onLoadMore = {
                    vm.setPage(vm.readPage() + 1)
                    vm.getMovies(query, vm.readPage())
                })
            }

            is Resource.Error -> {
                Text(
                    text = moviesResource.message ?: stringResource(R.string.something_wrong_state)
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
fun MovieGrid(movies: List<Movie>, nc: NavController, vm: MoviesViewModel, onLoadMore: () -> Unit) {
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

            if (index == movies.lastIndex) {
                onLoadMore()
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchView(
    searchTMDB: (String) -> Unit
) {
    val searchQueryState = rememberSaveable(stateSaver = TextFieldValue.Saver)  {
        mutableStateOf(TextFieldValue(String()))
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchQueryState.value,
        onValueChange = { value ->
            searchQueryState.value = value
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 18.sp),
        leadingIcon = {
            if (searchQueryState.value != TextFieldValue(String())) {
                IconButton(
                    onClick = {
                        searchQueryState.value =
                            TextFieldValue(String()) // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove search argument",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                searchTMDB(searchQueryState.value.text)
                keyboardController?.hide()
                //based on @ExperimentalComposeUiApi - if this doesn't work in a newer version remove it
                //no alternative in compose for hiding keyboard at time of writing
            }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search for  movies in TMDB based on search argument provided",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_movie_hint)
            )
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
    )
}