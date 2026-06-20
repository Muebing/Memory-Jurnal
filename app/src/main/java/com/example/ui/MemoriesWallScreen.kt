package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Dokumentasi
import com.example.data.Kegiatan
import com.example.ui.theme.SlateDark
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesWallScreen(
    viewModel: CoupleViewModel,
    onNavigateToKegiatanDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val allDokumentasi by viewModel.allDokumentasi.collectAsStateWithLifecycle()
    val allKegiatan by viewModel.allKegiatan.collectAsStateWithLifecycle()

    val averageRating = if (allDokumentasi.isEmpty()) 0.0 else allDokumentasi.map { it.rating }.average()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dinding Kenangan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Memory stats top banner card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("moments_stat_banner"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = allDokumentasi.size.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Momen Indah",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(50.dp)
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f", averageRating),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Rata-rata Rating",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Scrapbook List Title
            item {
                Text(
                    text = "Buku Cerita Kencan Bersama",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Cards Grid/List
            if (allDokumentasi.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = "Empty documentation",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Scrapbook Cerita",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lakukan aktivitas rencana kalian,\nubah status menjadi selesai, kemudian isi Cerita kencan romantis kalian di sini!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(allDokumentasi, key = { it.id }) { memory ->
                    val linkedPlan = allKegiatan.find { it.id == memory.kegiatanId }
                    ScrapbookCard(
                        memory = memory,
                        linkedPlan = linkedPlan,
                        onCardClick = {
                            linkedPlan?.let { onNavigateToKegiatanDetail(it.id) }
                        },
                        onDeleteMemory = { viewModel.deleteDokumentasi(memory.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScrapbookCard(
    memory: Dokumentasi,
    linkedPlan: Kegiatan?,
    onCardClick: () -> Unit,
    onDeleteMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustomImage = memory.imageUri != null && !listOf("coffee", "dinner", "movie", "travel").contains(memory.imageUri)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("scrapbook_card_${memory.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image/Hero Banner Area simulating photo with linear card gradients
            val (presetEmoji, presetBgColor, presetLabel) = when (memory.imageUri) {
                "coffee" -> Triple("☕", Color(0xFF8D6E63), "Coffee Date")
                "dinner" -> Triple("🍔", Color(0xFFFDD835), "Romantic Dinner")
                "movie" -> Triple("🎬", Color(0xFF7E57C2), "Cinema Night")
                "travel" -> Triple("🌴", Color(0xFF26A69A), "Cozy Trip")
                else -> Triple("📸", MaterialTheme.colorScheme.primary, "Koleksi Foto")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (isCustomImage && memory.imageUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        coil.compose.AsyncImage(
                            model = memory.imageUri,
                            contentDescription = "Foto Kenangan",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.40f))
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        presetBgColor.copy(alpha = 0.8f),
                                        presetBgColor.copy(alpha = 0.4f)
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Heart shape overlay background
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterEnd)
                    )

                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = presetEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = presetLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SlateDark))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = linkedPlan?.judul ?: "Rencana kencan bersama",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Star badge top right corners
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        contentColor = SlateDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "${memory.rating}.0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Caption story
                Text(
                    text = "\"" + memory.story + "\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    linkedPlan?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Today, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = it.tanggalRencana, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onDeleteMemory() }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Kenangan", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "Hapus", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)))
                    }
                }
            }
        }
    }
}
