package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameScreen
import com.example.game.GameViewModel
import com.example.game.LevelGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.allProgress.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    // Map levelId to progress for easy lookup
    val progressMap = remember(progressList) {
        progressList.associateBy { it.levelId }
    }

    // Warm pastel background gradient
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF6ED), // Peach White
            Color(0xFFFFFAF0)  // Floral Cream
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pilih Tingkat (1-1000)",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(GameScreen.HOME) },
                        modifier = Modifier.testTag("back_to_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Kembali ke Menu Utama",
                            tint = Color(0xFF2C3E50)
                        )
                    }
                },
                actions = {
                    // Reset Progress Button (Trash icon)
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("reset_progress_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Hapus Semua Kemajuan",
                            tint = Color(0xFFE74C3C)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF6ED)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 85.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(1000) { index ->
                    val levelId = index + 1
                    val progress = progressMap[levelId]

                    // A level is unlocked if it is level 1, OR if the previous level is completed
                    val isUnlocked = levelId == 1 || progressMap[levelId - 1]?.isCompleted == true

                    LevelGridItem(
                        levelId = levelId,
                        isUnlocked = isUnlocked,
                        stars = progress?.stars ?: 0,
                        bestTime = progress?.bestTimeSeconds,
                        onClick = {
                            if (isUnlocked) {
                                viewModel.startLevel(levelId)
                            }
                        }
                    )
                }
            }
        }
    }

    // Reset Progress Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Kemajuan Game?") },
            text = { Text("Langkah ini akan menghapus semua level yang telah Anda selesaikan dan mengunci kembali level 2 hingga 1000. Apakah Anda yakin?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllGameProgress()
                        showResetDialog = false
                    },
                    modifier = Modifier.testTag("confirm_reset_btn")
                ) {
                    Text("Ya, Reset", color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text("Batal", color = Color(0xFF7F8C8D))
                }
            }
        )
    }
}

@Composable
fun LevelGridItem(
    levelId: Int,
    isUnlocked: Boolean,
    stars: Int,
    bestTime: Int?,
    onClick: () -> Unit
) {
    val difficultyColor = when {
        levelId <= 150 -> Color(0xFF48C9B0) // Soft mint green (easy)
        levelId <= 350 -> Color(0xFF5DADE2) // Soft blue (medium)
        levelId <= 600 -> Color(0xFFF4D03F) // Soft yellow (hard)
        levelId <= 800 -> Color(0xFFEB984E) // Soft orange (very hard)
        else -> Color(0xFFEC7063)          // Soft red (expert)
    }

    val cardColor = if (isUnlocked) Color.White else Color(0xFFF2F3F4)
    val contentColor = if (isUnlocked) Color(0xFF2C3E50) else Color(0xFFBDC3C7)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_item_$levelId")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Level Title & Difficulty Indicator Dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Difficulty Dot
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(difficultyColor, CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.size(8.dp))
                }

                // Level Number
                Text(
                    text = "$levelId",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(8.dp))
            }

            // 2. Center Slot: Lock Icon vs Stars
            if (isUnlocked) {
                // Row of 3 stars
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..3) {
                        val isStarred = i <= stars
                        Icon(
                            imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            tint = if (isStarred) Color(0xFFF1C40F) else Color(0xFFE5E7E9),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else {
                // Lock Icon
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Terkunci",
                    tint = Color(0xFFBDC3C7),
                    modifier = Modifier.size(18.dp)
                )
            }

            // 3. Bottom Slot: Time Taken
            if (isUnlocked && bestTime != null) {
                Text(
                    text = "⏱️ ${bestTime}s",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7F8C8D),
                    textAlign = TextAlign.Center
                )
            } else {
                // Difficulty text label
                Text(
                    text = if (isUnlocked) "Buka" else "Terkunci",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) difficultyColor else Color(0xFFBDC3C7),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
