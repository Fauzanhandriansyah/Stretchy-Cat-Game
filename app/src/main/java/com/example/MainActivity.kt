package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.LevelRepository
import com.example.game.GameScreen
import com.example.game.GameViewModel
import com.example.game.GameViewModelFactory
import com.example.ui.screens.GameScreen as ActiveGameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.CustomizeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = LevelRepository(database.levelProgressDao())

        // Get Game ViewModel via Factory
        val viewModel: GameViewModel by viewModels {
            GameViewModelFactory(application, repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

                    // Smooth transition between screens
                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            GameScreen.HOME -> {
                                HomeScreen(viewModel = viewModel)
                            }
                            GameScreen.LEVEL_SELECT -> {
                                LevelSelectScreen(viewModel = viewModel)
                            }
                            GameScreen.PLAYING -> {
                                ActiveGameScreen(viewModel = viewModel)
                            }
                            GameScreen.CUSTOMIZE -> {
                                CustomizeScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

