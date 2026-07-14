package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameScreen
import com.example.game.GameViewModel
import com.example.game.LevelGenerator
import com.example.ui.components.CatBoard
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val level by viewModel.activeLevel.collectAsState()
    val playerPath by viewModel.playerPath.collectAsState()
    val movesCount by viewModel.movesCount.collectAsState()
    val timeElapsed by viewModel.timeElapsed.collectAsState()
    val isCompleted by viewModel.isLevelCompleted.collectAsState()
    val earnedStars by viewModel.earnedStars.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()
    val catExpression by viewModel.catExpression.collectAsState()
    val selectedSkin by viewModel.selectedSkin.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFAF0), // Cream White
            Color(0xFFFFF4EB), // Warm Peach
            Color(0xFFFFEAD9)  // Soft Orange Accent
        )
    )

    // Calculate progression details
    val currentFilled = playerPath.size
    val totalPlayable = level?.totalPlayableCells ?: 1
    val progressFraction = currentFilled.toFloat() / totalPlayable
    val progressPercent = (progressFraction * 100).toInt()

    // Confetti particles representation
    val confettiParticles = remember { mutableStateListOf<ConfettiParticle>() }

    // Spawn falling confetti particles when showConfetti is true
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            confettiParticles.clear()
            // Generate some random falling sparkles
            repeat(100) {
                confettiParticles.add(
                    ConfettiParticle(
                        x = Random.nextFloat(),
                        y = -Random.nextFloat() * 0.5f,
                        color = listOf(
                            Color(0xFFFF5733), Color(0xFFFFC300), Color(0xFF33FF57),
                            Color(0xFF33FFF0), Color(0xFFFF33F0), Color(0xFF581845)
                        ).random(),
                        speed = 0.01f + Random.nextFloat() * 0.02f,
                        rotation = Random.nextFloat() * 360f,
                        size = 15f + Random.nextFloat() * 25f
                    )
                )
            }
            // Update positions in a loop
            while (showConfetti) {
                delay(16)
                for (i in confettiParticles.indices) {
                    val p = confettiParticles[i]
                    var ny = p.y + p.speed
                    if (ny > 1.1f) {
                        ny = -0.1f // recycle to the top
                    }
                    confettiParticles[i] = p.copy(y = ny, rotation = p.rotation + 2f)
                }
            }
        } else {
            confettiParticles.clear()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            level?.let { currentLevel ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. HEADER ROW: Back, Level Name, Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(GameScreen.LEVEL_SELECT) },
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .size(44.dp)
                                .testTag("game_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Kembali ke Pilih Level",
                                tint = Color(0xFF2C3E50)
                            )
                        }

                        // Level Text & Difficulty Label
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tingkat ${currentLevel.levelId}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2C3E50)
                            )
                            Text(
                                text = LevelGenerator.getDifficultyLabel(currentLevel.levelId),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    currentLevel.levelId <= 15 -> Color(0xFF1ABC9C)
                                    currentLevel.levelId <= 35 -> Color(0xFF3498DB)
                                    currentLevel.levelId <= 60 -> Color(0xFFF1C40F)
                                    currentLevel.levelId <= 80 -> Color(0xFFE67E22)
                                    else -> Color(0xFFE74C3C)
                                }
                            )
                        }

                        // Realtime Timer Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9E4F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formatTimer(timeElapsed),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C3E50)
                                )
                            }
                        }
                    }

                    // 2. FILL RATIO PROGRESS BAR
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ruangan Terisi:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7F8C8D)
                            )
                            Text(
                                text = "$progressPercent% ($currentFilled/$totalPlayable)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9E4F)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .testTag("level_progress_bar"),
                            color = Color(0xFFFF9E4F),
                            trackColor = Color(0xFFE5E7E9)
                        )
                    }

                    // 3. CAT SPEECH BUBBLE (Interactive prompts based on state!)
                    CatSpeechBubble(catExpression, progressPercent)

                    // 4. ACTIVE PUZZLE BOARD (Scaled responsive)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CatBoard(
                            level = currentLevel,
                            playerPath = playerPath,
                            catExpression = catExpression,
                            onCellTouch = { r, c ->
                                viewModel.handleCellTouch(r, c)
                            },
                            selectedSkin = selectedSkin,
                            selectedTheme = selectedTheme
                        )
                    }

                    // 5. CONTROLS BAR: Undo, Restart, Moves Tracker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Moves Counter
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "LANGKAH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF95A5A6)
                                )
                                Text(
                                    text = "$movesCount",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2C3E50)
                                )
                            }
                        }

                        // Undo & Restart buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Undo button
                            IconButton(
                                onClick = { viewModel.undoLastMove() },
                                modifier = Modifier
                                    .background(Color.White, CircleShape)
                                    .size(52.dp)
                                    .testTag("undo_move_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Undo",
                                    tint = Color(0xFF2C3E50),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Restart button
                            IconButton(
                                onClick = { viewModel.resetLevel() },
                                modifier = Modifier
                                    .background(Color.White, CircleShape)
                                    .size(52.dp)
                                    .testTag("reset_level_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Mulai Ulang",
                                    tint = Color(0xFFE74C3C),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }

            // F. Confetti Overlay Drawing (Drawn on absolute top when completed)
            if (showConfetti) {
                ConfettiOverlay(particles = confettiParticles)
            }

            // G. SUCCESS LEVEL COMPLETION OVERLAY DIALOG (Animated slide-in)
            AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SuccessCard(
                        levelId = level?.levelId ?: 1,
                        stars = earnedStars,
                        timeTaken = timeElapsed,
                        onReplay = { viewModel.resetLevel() },
                        onNext = { viewModel.loadNextLevel() },
                        onBack = { viewModel.navigateTo(GameScreen.LEVEL_SELECT) }
                    )
                }
            }
        }
    }
}

// Beautiful speech bubbles above the puzzle
@Composable
fun CatSpeechBubble(expression: String, fillPercent: Int) {
    val bubbleText = when {
        expression == "CELEBRATING" -> "Meong! Sempurna! 🎉 Kamu hebat sekali!"
        expression == "HAPPY" -> "Nyamm! Tubuhku terasa sangat segar! 🐾"
        expression == "THINKING" -> "Hmm... meong, bagaimana kalau kita belok ke sini?"
        fillPercent > 80 -> "Wah, hampir penuh! Ayo sedikit lagi meong! 🐱"
        fillPercent > 50 -> "Bagus sekali! Regangkan aku lebih panjang!"
        else -> "Tarik kepalaku untuk mengisi ruangan ini, Meow! 😸"
    }

    Card(
        shape = RoundedCornerShape(
            topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4D0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = bubbleText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF8A5A36),
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}

// Confetti particle representation
data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val speed: Float,
    val rotation: Float,
    val size: Float
)

@Composable
fun ConfettiOverlay(particles: List<ConfettiParticle>) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        for (p in particles) {
            val px = p.x * size.width
            val py = p.y * size.height

            // Draw a tiny rotating rectangle confetti
            withTransform({
                rotate(p.rotation, Offset(px, py))
            }) {
                drawRoundRect(
                    color = p.color,
                    topLeft = Offset(px - p.size / 2, py - p.size / 4),
                    size = Size(p.size, p.size / 2),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }
    }
}

// Level Win Success Card
@Composable
fun SuccessCard(
    levelId: Int,
    stars: Int,
    timeTaken: Int,
    onReplay: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("victory_modal_card")
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Title / Celebration header
            Text(
                text = "PURR-FECT! 🐾",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF9E4F),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tingkat $levelId Selesai Sempurna!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF34495E),
                textAlign = TextAlign.Center
            )

            // 2. Stars Display Animation
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..3) {
                    val active = i <= stars
                    Icon(
                        imageVector = if (active) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = null,
                        tint = if (active) Color(0xFFF1C40F) else Color(0xFFE5E7E9),
                        modifier = Modifier
                            .size(if (active) 44.dp else 36.dp)
                            .padding(2.dp)
                            .scale(if (active) 1.1f else 1.0f)
                    )
                }
            }

            // 3. Stats Rows: Time Taken
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Waktu Selesai",
                            fontSize = 11.sp,
                            color = Color(0xFF7F8C8D),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${timeTaken}s",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2C3E50)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFE5E7E9))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Status Bintang",
                            fontSize = 11.sp,
                            color = Color(0xFF7F8C8D),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (stars == 3) "Luar Biasa!" else if (stars == 2) "Hebat!" else "Cukup Bagus",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF9E4F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. ACTION BUTTONS: Next Level, Play Again, Back
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (levelId < 1000) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9E4F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("next_level_button")
                    ) {
                        Text("Tingkat Berikutnya ➡️", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("replay_current_level")
                    ) {
                        Text("Main Lagi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9E4F))
                    }

                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("back_to_level_select_victory")
                    ) {
                        Text("Semua Tingkat", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7F8C8D))
                    }
                }
            }
        }
    }
}

// Utility to format elapsed seconds to mm:ss
fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
