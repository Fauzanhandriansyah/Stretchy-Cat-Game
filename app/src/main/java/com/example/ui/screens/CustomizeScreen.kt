package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameScreen
import com.example.game.GameViewModel

@Composable
fun CustomizeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSkin by viewModel.selectedSkin.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()

    val maxLevelCompleted = allProgress.filter { it.isCompleted }.maxOfOrNull { it.levelId } ?: 0

    val scrollState = rememberScrollState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFAF0), // Cream White
            Color(0xFFFFF0E0), // Soft Peach
            Color(0xFFFFE4D0)  // Warm Apricot
        )
    )

    // Definitions for Skins
    val skins = listOf(
        SkinItem("ORANGE", "Klasik Oranye", "Kucing oyen ceria kesayangan semua orang", Color(0xFFFF9E4F), Color(0xFFE67E22), 0),
        SkinItem("TUXEDO", "Tuxedo Hitam", "Kucing necis berjas hitam-putih formal", Color(0xFF2C3E50), Color(0xFF1A252F), 5),
        SkinItem("CALICO", "Kaliko Belang", "Tiga warna pembawa keberuntungan belang-belang", Color(0xFFE67E22), Color(0xFF2D3E50), 15),
        SkinItem("PINK_BUNNY", "Kelinci Pink", "Kostum kelinci lucu berwarna merah muda pastel", Color(0xFFFFB6C1), Color(0xFFFF69B4), 30),
        SkinItem("TIGER", "Harimau Garang", "Oyen perkasa bergaris hitam layaknya raja hutan", Color(0xFFD35400), Color(0xFF111111), 50)
    )

    // Definitions for Themes
    val themes = listOf(
        ThemeItem("CREAM", "Ruang Keluarga", "Lantai krem hangat yang nyaman", Color(0xFFF9F6F0), Color(0xFFE8E2D5), 0),
        ThemeItem("LAVENDER", "Kebun Lavender", "Suasana kebun ungu yang menenangkan", Color(0xFFF3E5F5), Color(0xFFD1C4E9), 10),
        ThemeItem("COSMIC", "Luar Angkasa", "Lantai kapal induk kosmis yang futuristik", Color(0xFF1B1B22), Color(0xFF3E3E52), 25),
        ThemeItem("BEACH", "Pantai Tropis", "Tepian pantai berpasir emas yang cerah", Color(0xFFFFF5CC), Color(0xFFFFE082), 40)
    )

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(44.dp)
                        .testTag("customize_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Kembali ke Beranda",
                        tint = Color(0xFF2C3E50)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Lemari Kucing 👗",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = "Kustomisasi Kucing & Ruangan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7F8C8D)
                    )
                }
            }

            // Progression Summary Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFFFE4D0), CircleShape)
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Rekor Tingkat Tertinggi: Level $maxLevelCompleted",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2C3E50)
                        )
                        Text(
                            text = "Selesaikan lebih banyak tingkat untuk membuka skin dan tema baru!",
                            fontSize = 12.sp,
                            color = Color(0xFF7F8C8D)
                        )
                    }
                }
            }

            // Category 1: Skins
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Skin Kucing Meong (Skins)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C3E50)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    skins.forEach { skin ->
                        val isUnlocked = maxLevelCompleted >= skin.requiredLevel
                        val isEquipped = selectedSkin == skin.id

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEquipped) Color(0xFFFFF4EB) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 4.dp else 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(enabled = isUnlocked) {
                                    viewModel.selectSkin(skin.id)
                                }
                                .testTag("skin_${skin.id.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Glistening Circular Cat Preview Circle
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .shadow(3.dp, CircleShape)
                                        .background(skin.primaryColor, CircleShape)
                                        .padding(4.dp)
                                ) {
                                    // Innermost stripe decoration
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(skin.stripeColor, CircleShape)
                                            .align(Alignment.Center)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = skin.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF2C3E50)
                                        )
                                        if (isEquipped) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFF9E4F), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Dipakai",
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = skin.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF7F8C8D)
                                    )
                                    if (!isUnlocked) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color(0xFFE74C3C),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Selesaikan Tingkat ${skin.requiredLevel} untuk membuka",
                                                fontSize = 11.sp,
                                                color = Color(0xFFE74C3C),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = if (isEquipped) "Selected" else "Select",
                                        tint = if (isEquipped) Color(0xFFFF9E4F) else Color(0xFFBDC3C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFBDC3C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category 2: Themes
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Tema Ruangan (Themes)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C3E50)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    themes.forEach { theme ->
                        val isUnlocked = maxLevelCompleted >= theme.requiredLevel
                        val isEquipped = selectedTheme == theme.id

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEquipped) Color(0xFFFFF4EB) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 4.dp else 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(enabled = isUnlocked) {
                                    viewModel.selectTheme(theme.id)
                                }
                                .testTag("theme_${theme.id.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Dual Color Split Circle for Theme Preview
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .shadow(3.dp, CircleShape)
                                        .background(theme.bgColor, CircleShape)
                                        .padding(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(theme.gridColor, RoundedCornerShape(4.dp))
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = theme.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF2C3E50)
                                        )
                                        if (isEquipped) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFF9E4F), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Dipakai",
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Kustomisasi papan puzzle dengan suasana baru",
                                        fontSize = 12.sp,
                                        color = Color(0xFF7F8C8D)
                                    )
                                    if (!isUnlocked) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color(0xFFE74C3C),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Selesaikan Tingkat ${theme.requiredLevel} untuk membuka",
                                                fontSize = 11.sp,
                                                color = Color(0xFFE74C3C),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = if (isEquipped) "Selected" else "Select",
                                        tint = if (isEquipped) Color(0xFFFF9E4F) else Color(0xFFBDC3C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFBDC3C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class SkinItem(
    val id: String,
    val name: String,
    val description: String,
    val primaryColor: Color,
    val stripeColor: Color,
    val requiredLevel: Int
)

data class ThemeItem(
    val id: String,
    val name: String,
    val description: String,
    val bgColor: Color,
    val gridColor: Color,
    val requiredLevel: Int
)
