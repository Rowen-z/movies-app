package com.example.level5task2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.level5task2.ui.screens.MovieDetailScreen
import com.example.level5task2.ui.screens.MoviesFavoriteScreen
import com.example.level5task2.ui.screens.MoviesScreens
import com.example.level5task2.ui.screens.MoviesSearchScreen
import com.example.level5task2.ui.theme.Level5Task2Theme
import com.example.level5task2.viewmodel.MoviesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Level5Task2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MoviesNavHost(navController, modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun MoviesNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    val viewModel: MoviesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = MoviesScreens.MoviesSearchScreen.name,
        modifier = modifier
    ) {
        composable(route = MoviesScreens.MoviesSearchScreen.name) {
            MoviesSearchScreen(navController, viewModel)
        }
        composable(route = MoviesScreens.MovieDetailScreen.name) {
            MovieDetailScreen(navController, viewModel)
        }
        composable(route = MoviesScreens.MoviesFavoriteScreen.name) {
            MoviesFavoriteScreen(navController, viewModel)
        }
    }
}