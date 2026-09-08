package com.example.game

import kotlin.random.Random

data class Point(val r: Int, val c: Int) {
    fun getNeighbors(rows: Int, cols: Int): List<Point> {
        val list = mutableListOf<Point>()
        if (r > 0) list.add(Point(r - 1, c))
        if (r < rows - 1) list.add(Point(r + 1, c))
        if (c > 0) list.add(Point(r, c - 1))
        if (c < cols - 1) list.add(Point(r, c + 1))
        return list
    }
}

enum class ObstacleType {
    YARN,     // Ball of yarn
    MOUSE,    // Cute toy mouse
    SCRATCH,  // Scratch board
    PLANT     // Small houseplant
}

data class GameLevel(
    val levelId: Int,
    val rows: Int,
    val cols: Int,
    val startPoint: Point,
    val playablePoints: Set<Point>,
    val obstacles: Map<Point, ObstacleType>,
    val solutionPath: List<Point> // One guaranteed solution path
) {
    val totalPlayableCells: Int = playablePoints.size
}

object LevelGenerator {

    fun generateLevel(levelId: Int): GameLevel {
        // Deterministic random seed per level
        val seed = levelId * 12345L + 7L
        val random = Random(seed)

        // 1. Determine grid size
        val (rows, cols) = when {
            levelId <= 5 -> 3 to 3     // Levels 1-5: 3x3 (total 9)
            levelId <= 15 -> 4 to 4    // Levels 6-15: 4x4 (total 16)
            levelId <= 50 -> 5 to 5    // Levels 16-50: 5x5 (total 25)
            levelId <= 150 -> 6 to 6   // Levels 51-150: 6x6 (total 36)
            levelId <= 400 -> 7 to 7   // Levels 151-400: 7x7 (total 49)
            else -> 8 to 8             // Levels 401-1000: 8x8 (total 64)
        }

        // 2. Determine target path length (empty spaces for the cat)
        val targetLength = when {
            levelId <= 2 -> 7         // Level 1-2: 7 playable, 2 obstacles (3x3)
            levelId <= 5 -> 8         // Level 3-5: 8 playable, 1 obstacle (3x3)
            levelId <= 10 -> 12       // Level 6-10: 12 playable, 4 obstacles (4x4)
            levelId <= 15 -> 13       // Level 11-15: 13 playable, 3 obstacles (4x4)
            levelId <= 30 -> 18       // Level 16-30: 18 playable, 7 obstacles (5x5)
            levelId <= 50 -> 20       // Level 31-50: 20 playable, 5 obstacles (5x5)
            levelId <= 100 -> 26      // Level 51-100: 26 playable, 10 obstacles (6x6)
            levelId <= 150 -> 28      // Level 101-150: 28 playable, 8 obstacles (6x6)
            levelId <= 250 -> 35      // Level 151-250: 35 playable, 14 obstacles (7x7)
            levelId <= 400 -> 38      // Level 251-400: 38 playable, 11 obstacles (7x7)
            levelId <= 700 -> 46      // Level 401-700: 46 playable, 18 obstacles (8x8)
            else -> 50                // Level 701-1000: 50 playable, 14 obstacles (8x8)
        }

        // 3. Find a random self-avoiding path of targetLength using DFS
        var bestPath = listOf<Point>()
        var attempts = 0
        val maxAttempts = 150 // Try different starting points if needed

        while (bestPath.size < targetLength && attempts < maxAttempts) {
            val startR = random.nextInt(rows)
            val startC = random.nextInt(cols)
            val startPoint = Point(startR, startC)

            val currentPath = mutableListOf(startPoint)
            val pathFound = mutableListOf<Point>()

            var stepCount = 0
            val maxSteps = 4000 // limit DFS backtracking depth per start point

            fun dfs(curr: Point): Boolean {
                stepCount++
                if (stepCount > maxSteps) return false
                if (currentPath.size == targetLength) {
                    pathFound.addAll(currentPath)
                    return true
                }

                val neighbors = curr.getNeighbors(rows, cols)
                    .filter { it !in currentPath }
                    .shuffled(random)

                for (next in neighbors) {
                    currentPath.add(next)
                    if (dfs(next)) return true
                    currentPath.removeAt(currentPath.size - 1)
                }
                return false
            }

            dfs(startPoint)

            if (pathFound.size > bestPath.size) {
                bestPath = pathFound
            }
            attempts++
        }

        // Safe fallback in case DFS target wasn't fully reached
        val finalPath = if (bestPath.isNotEmpty()) bestPath else {
            // Extreme fallback: simple snake path starting at (0,0)
            val fallback = mutableListOf<Point>()
            for (r in 0 until rows) {
                if (r % 2 == 0) {
                    for (c in 0 until cols) fallback.add(Point(r, c))
                } else {
                    for (c in cols - 1 downTo 0) fallback.add(Point(r, c))
                }
            }
            fallback.take(targetLength)
        }

        val startPoint = finalPath.first()
        val playablePoints = finalPath.toSet()

        // 4. Any cell in the grid not in the finalPath is an obstacle
        val obstacles = mutableMapOf<Point, ObstacleType>()
        val obstacleTypes = ObstacleType.values()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val p = Point(r, c)
                if (p !in playablePoints) {
                    val obsType = obstacleTypes[random.nextInt(obstacleTypes.size)]
                    obstacles[p] = obsType
                }
            }
        }

        return GameLevel(
            levelId = levelId,
            rows = rows,
            cols = cols,
            startPoint = startPoint,
            playablePoints = playablePoints,
            obstacles = obstacles,
            solutionPath = finalPath
        )
    }

    // Get difficulty level text (Indonesian)
    fun getDifficultyLabel(levelId: Int): String {
        return when {
            levelId <= 150 -> "Sangat Mudah"
            levelId <= 350 -> "Mudah"
            levelId <= 600 -> "Sedang"
            levelId <= 800 -> "Sulit"
            else -> "Sangat Sulit"
        }
    }
}
