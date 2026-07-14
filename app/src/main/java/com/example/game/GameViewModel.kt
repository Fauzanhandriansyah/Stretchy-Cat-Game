package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LevelProgress
import com.example.data.LevelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class GameScreen {
    HOME,
    LEVEL_SELECT,
    PLAYING,
    CUSTOMIZE
}

class GameViewModel(
    application: Application,
    private val repository: LevelRepository
) : AndroidViewModel(application) {

    // 2. Database Progress State
    val allProgress: StateFlow<List<LevelProgress>> = repository.allProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 1. Navigation State
    private val _currentScreen = MutableStateFlow(GameScreen.HOME)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    // 1b. Active progression target
    private val _nextLevelToPlay = MutableStateFlow(1)
    val nextLevelToPlay: StateFlow<Int> = _nextLevelToPlay.asStateFlow()

    // 1c. Wardrobe / Skin state (Orange, Tuxedo, Calico, Pink Bunny, Tiger)
    private val _selectedSkin = MutableStateFlow("ORANGE")
    val selectedSkin: StateFlow<String> = _selectedSkin.asStateFlow()

    // 1d. Floor & Wall Theme state (Cream, Lavender, Cosmic, Beach)
    private val _selectedTheme = MutableStateFlow("CREAM")
    val selectedTheme: StateFlow<String> = _selectedTheme.asStateFlow()

    init {
        // Automatically track and update the latest level progress
        viewModelScope.launch {
            allProgress.collect { progressList ->
                val lastCompleted = progressList.filter { it.isCompleted }.maxOfOrNull { it.levelId } ?: 0
                _nextLevelToPlay.value = (lastCompleted + 1).coerceIn(1, 1000)
            }
        }
    }

    fun selectSkin(skinId: String) {
        if (isSkinUnlocked(skinId)) {
            _selectedSkin.value = skinId
        }
    }

    fun selectTheme(themeId: String) {
        if (isThemeUnlocked(themeId)) {
            _selectedTheme.value = themeId
        }
    }

    fun isSkinUnlocked(skinId: String): Boolean {
        val maxLevelCompleted = allProgress.value.filter { it.isCompleted }.maxOfOrNull { it.levelId } ?: 0
        return when (skinId) {
            "ORANGE" -> true
            "TUXEDO" -> maxLevelCompleted >= 5
            "CALICO" -> maxLevelCompleted >= 15
            "PINK_BUNNY" -> maxLevelCompleted >= 30
            "TIGER" -> maxLevelCompleted >= 50
            else -> false
        }
    }

    fun isThemeUnlocked(themeId: String): Boolean {
        val maxLevelCompleted = allProgress.value.filter { it.isCompleted }.maxOfOrNull { it.levelId } ?: 0
        return when (themeId) {
            "CREAM" -> true
            "LAVENDER" -> maxLevelCompleted >= 10
            "COSMIC" -> maxLevelCompleted >= 25
            "BEACH" -> maxLevelCompleted >= 40
            else -> false
        }
    }

    // 3. Gameplay State
    private val _activeLevel = MutableStateFlow<GameLevel?>(null)
    val activeLevel: StateFlow<GameLevel?> = _activeLevel.asStateFlow()

    private val _playerPath = MutableStateFlow<List<Point>>(emptyList())
    val playerPath: StateFlow<List<Point>> = _playerPath.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _timeElapsed = MutableStateFlow(0)
    val timeElapsed: StateFlow<Int> = _timeElapsed.asStateFlow()

    private val _isLevelCompleted = MutableStateFlow(false)
    val isLevelCompleted: StateFlow<Boolean> = _isLevelCompleted.asStateFlow()

    private val _earnedStars = MutableStateFlow(0)
    val earnedStars: StateFlow<Int> = _earnedStars.asStateFlow()

    // Timer Job
    private var timerJob: Job? = null

    // For celebrating level completions
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    // Cat visual expressions: Happy, Normal, Thinking
    private val _catExpression = MutableStateFlow("NORMAL")
    val catExpression: StateFlow<String> = _catExpression.asStateFlow()

    // Navigation triggers
    fun navigateTo(screen: GameScreen) {
        _currentScreen.value = screen
        if (screen != GameScreen.PLAYING) {
            stopTimer()
        }
    }

    // Start a specific level
    fun startLevel(levelId: Int) {
        val level = LevelGenerator.generateLevel(levelId)
        _activeLevel.value = level
        _playerPath.value = listOf(level.startPoint)
        _movesCount.value = 0
        _timeElapsed.value = 0
        _isLevelCompleted.value = false
        _earnedStars.value = 0
        _showConfetti.value = false
        _catExpression.value = "NORMAL"

        _currentScreen.value = GameScreen.PLAYING
        startTimer()
    }

    // Swiping / Dragging logic
    fun handleCellTouch(row: Int, col: Int) {
        val level = _activeLevel.value ?: return
        if (_isLevelCompleted.value) return

        val point = Point(row, col)
        // Ensure it's a playable tile
        if (point !in level.playablePoints) {
            SoundManager.playHitObstacleSound()
            return
        }

        val path = _playerPath.value
        val head = path.last()

        // 1. If it's already in the path
        val pathIndex = path.indexOf(point)
        if (pathIndex != -1) {
            // Retract path back to this point!
            if (pathIndex < path.size - 1) {
                _playerPath.value = path.take(pathIndex + 1)
                _movesCount.value++
                _catExpression.value = "THINKING"
                SoundManager.playStretchSound()
            }
            return
        }

        // 2. If it's not in the path, it must be adjacent to the current head
        val rowDiff = abs(point.r - head.r)
        val colDiff = abs(point.c - head.c)
        if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
            // Valid step! Extend the body
            val newPath = path + point
            _playerPath.value = newPath
            _movesCount.value++
            SoundManager.playStretchSound()

            // Animate expression occasionally
            if (newPath.size % 4 == 0) {
                _catExpression.value = "HAPPY"
                viewModelScope.launch {
                    delay(800)
                    if (!_isLevelCompleted.value) {
                        _catExpression.value = "NORMAL"
                    }
                }
            }

            // Check win condition
            if (newPath.size == level.totalPlayableCells) {
                completeLevel()
            }
        } else {
            // Non-adjacent move attempted, count as bump/invalid stretch
            SoundManager.playHitObstacleSound()
        }
    }

    fun undoLastMove() {
        val path = _playerPath.value
        if (path.size > 1) {
            _playerPath.value = path.dropLast(1)
            _movesCount.value++
            _catExpression.value = "THINKING"
            SoundManager.playStretchSound()
        }
    }

    fun resetLevel() {
        val level = _activeLevel.value ?: return
        _playerPath.value = listOf(level.startPoint)
        _movesCount.value = 0 // Reset moves to 0 on restart
        _isLevelCompleted.value = false
        _earnedStars.value = 0
        _showConfetti.value = false
        _catExpression.value = "NORMAL"
        _timeElapsed.value = 0 // Reset timer to 0
        startTimer() // Restart active countdown
    }

    private fun completeLevel() {
        _isLevelCompleted.value = true
        _catExpression.value = "CELEBRATING"
        _showConfetti.value = true
        stopTimer()
        SoundManager.playLevelCompleteSound()

        val level = _activeLevel.value ?: return
        val elapsed = _timeElapsed.value

        // Calculate stars based on time limits and grid sizes
        val stars = calculateStars(level.levelId, elapsed)
        _earnedStars.value = stars

        // Save progress to local database
        viewModelScope.launch {
            // Check if existing record is better, don't overwrite with fewer stars
            val existing = repository.getProgressById(level.levelId)
            val bestTime = if (existing != null && existing.isCompleted) {
                kotlin.math.min(existing.bestTimeSeconds, elapsed)
            } else {
                elapsed
            }
            val bestStars = if (existing != null) {
                kotlin.math.max(existing.stars, stars)
            } else {
                stars
            }

            repository.saveProgress(
                LevelProgress(
                    levelId = level.levelId,
                    isCompleted = true,
                    stars = bestStars,
                    bestTimeSeconds = bestTime
                )
            )
        }
    }

    fun loadNextLevel() {
        val currentId = _activeLevel.value?.levelId ?: return
        if (currentId < 1000) {
            startLevel(currentId + 1)
        } else {
            _currentScreen.value = GameScreen.LEVEL_SELECT
        }
    }

    // Helper to compute stars
    private fun calculateStars(levelId: Int, timeSeconds: Int): Int {
        return when {
            levelId <= 5 -> { // 3x3
                if (timeSeconds <= 8) 3 else if (timeSeconds <= 18) 2 else 1
            }
            levelId <= 15 -> { // 4x4
                if (timeSeconds <= 15) 3 else if (timeSeconds <= 35) 2 else 1
            }
            levelId <= 35 -> { // 5x5
                if (timeSeconds <= 30) 3 else if (timeSeconds <= 65) 2 else 1
            }
            levelId <= 60 -> { // 6x6
                if (timeSeconds <= 50) 3 else if (timeSeconds <= 110) 2 else 1
            }
            levelId <= 80 -> { // 7x7
                if (timeSeconds <= 80) 3 else if (timeSeconds <= 180) 2 else 1
            }
            else -> { // 8x8
                if (timeSeconds <= 120) 3 else if (timeSeconds <= 260) 2 else 1
            }
        }
    }

    // Timer functions
    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timeElapsed.value++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // Clear all progress for testing or resetting
    fun resetAllGameProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

// ViewModel Factory
class GameViewModelFactory(
    private val application: Application,
    private val repository: LevelRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
