package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Kegiatan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CoupleViewModel,
    onNavigateToKegiatanList: () -> Unit,
    onNavigateToKegiatanDetail: (Int) -> Unit,
    onNavigateToCreateKegiatan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.activeSession.collectAsStateWithLifecycle()
    val allKegiatan by viewModel.allKegiatan.collectAsStateWithLifecycle()
    val allBiaya by viewModel.allBiaya.collectAsStateWithLifecycle()

    val metrics = viewModel.getDashboardMetrics()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBEBE8)) // Warm pastel-pink background from mockup design
    ) {
        // Decorative background circles from the mockup frame
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-40).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E3789)) // Dark Indigo circle
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 100.dp)
                .size(280.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFA29D)) // Blush coral circle
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 1. HERO HEADER: Dark Navy Rounded Card
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2E60)), // Deep royal navy
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Organic blobs within the dark navy card representation
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 24.dp, y = 24.dp)
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFAAB0).copy(alpha = 0.4f))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = (-30).dp, y = 30.dp)
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFAAB0).copy(alpha = 0.2f))
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Dynamic User status bar mimicking Emily's phone tracker
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(24.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val userInitial = session?.username?.firstOrNull()?.uppercase() ?: "S"
                                        val partnerInitial = session?.partnerName?.firstOrNull()?.uppercase() ?: "P"

                                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .align(Alignment.CenterStart)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE91E63)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = userInitial,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 9.sp)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .align(Alignment.CenterEnd)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFFFA29D)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = partnerInitial,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 9.sp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = session?.let { "${it.username} & ${it.partnerName}" } ?: "Samuel & Sarah",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF4CAF50))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Sambungan Aktif",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                                )
                                            }
                                        }
                                    }

                                    // Logout Trigger Action Button
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .clickable { viewModel.logout() }
                                            .testTag("avatar_logout_trigger"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Logout,
                                            contentDescription = "Logout",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // LARGE BANNER CENTRAL TIMER TEXT representation
                                Text(
                                    text = "365 Hari",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 38.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Selalu Bersama & Berbagi Kasih",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFFFAAB0),
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 1.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // ACTIONS BOTTOM BAR: Pure White Pill Container
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, shape = RoundedCornerShape(24.dp))
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    QuickActionItem(
                                        icon = Icons.Filled.Add,
                                        label = "Plan Baru",
                                        onClick = onNavigateToCreateKegiatan,
                                        backgroundColor = Color(0xFFFFF0F2),
                                        iconColor = Color(0xFFE91E63)
                                    )
                                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                                    QuickActionItem(
                                        icon = Icons.Filled.Payments,
                                        label = "Log Biaya",
                                        onClick = onNavigateToKegiatanList,
                                        backgroundColor = Color(0xFFFFF7EB),
                                        iconColor = Color(0xFFFF9800)
                                    )
                                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                                    QuickActionItem(
                                        icon = Icons.Filled.PhotoCamera,
                                        label = "Momen",
                                        onClick = onNavigateToKegiatanList,
                                        backgroundColor = Color(0xFFEEF1FF),
                                        iconColor = Color(0xFF3F51B5)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. STAT CARDS PASTEL GRID: High Round borders precisely matching mockup elements
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MockupStatCard(
                                title = "Total Agenda",
                                value = metrics.totalKegiatan.toString(),
                                subtitle = "Rencana kencan",
                                backgroundColor = Color(0xFFFFF0F2),
                                iconBackgroundColor = Color(0xFFFFAAB0).copy(alpha = 0.4f),
                                iconColor = Color(0xFFE91E63),
                                icon = Icons.Filled.Favorite,
                                testTag = "stat_card_total_agenda",
                                modifier = Modifier.weight(1f)
                            )
                            MockupStatCard(
                                title = "Total Pengeluaran",
                                value = "Rp" + formatThousand(metrics.totalPengeluaran),
                                subtitle = "Anggaran bersama",
                                backgroundColor = Color(0xFFFFF7EB),
                                iconBackgroundColor = Color(0xFFFFD18D).copy(alpha = 0.4f),
                                iconColor = Color(0xFFFF9800),
                                icon = Icons.Filled.Payments,
                                testTag = "stat_card_total_pengeluaran",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MockupStatCard(
                                title = "Sedang Berjalan",
                                value = metrics.belumSelesai.toString(),
                                subtitle = "Agenda aktif",
                                backgroundColor = Color(0xFFEEF1FF),
                                iconBackgroundColor = Color(0xFFCBD2FF).copy(alpha = 0.4f),
                                iconColor = Color(0xFF3F51B5),
                                icon = Icons.Filled.PlayArrow,
                                testTag = "stat_card_sedang_berjalan",
                                modifier = Modifier.weight(1f)
                            )
                            MockupStatCard(
                                title = "Selesai",
                                value = metrics.selesai.toString(),
                                subtitle = "Momen berharga",
                                backgroundColor = Color(0xFFEAF9F5),
                                iconBackgroundColor = Color(0xFFB9F1E5).copy(alpha = 0.4f),
                                iconColor = Color(0xFF009688),
                                icon = Icons.Filled.CheckCircle,
                                testTag = "stat_card_selesai",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. FEATURED ADVENTURE (Petualangan Terdekat Kita mockup styled)
                item {
                    val nextAdventure = allKegiatan.firstOrNull { it.status.lowercase() != "selesai" && it.status.lowercase() != "dibatalkan" }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Petualangan Terdekat Kita",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B2E60)
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (nextAdventure != null) {
                            val planCost = allBiaya.filter { it.kegiatanId == nextAdventure.id }.sumOf { it.nominal }
                            NextAdventureCard(
                                plan = nextAdventure,
                                accumulatedBiaya = planCost,
                                onClick = { onNavigateToKegiatanDetail(nextAdventure.id) }
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToCreateKegiatan() }
                                    .testTag("no_featured_adventure"),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFFFF0F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = null,
                                            tint = Color(0xFFE91E63),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "Rencanakan Petualangan Baru!",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1B2E60))
                                        )
                                        Text(
                                            text = "Belum ada agenda terdekat. Ketuk untuk buat rencana romantis baru.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1B2E60).copy(alpha = 0.6f)),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. TIMELINE LOG: AGENDA RENCANA TERBARU
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Agenda Rencana Terbaru",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B2E60)
                            )
                        )
                        TextButton(
                            onClick = onNavigateToKegiatanList,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text("Lihat Semua", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE91E63)))
                        }
                    }
                }

                if (metrics.recentActivities.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "Empty plan",
                                    tint = Color(0xFF1B2E60).copy(alpha = 0.3f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada rencana kegiatan.\nYuk buat rencana kencan pertamamu berdua!",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF1B2E60).copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                } else {
                    val activitiesList = metrics.recentActivities
                    itemsIndexed(activitiesList, key = { _, plan -> plan.id }) { index, plan ->
                        RecentPlanListItem(
                            plan = plan,
                            isFirst = index == 0,
                            isLast = index == activitiesList.lastIndex,
                            onClick = { onNavigateToKegiatanDetail(plan.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2E60),
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun MockupStatCard(
    title: String,
    value: String,
    subtitle: String,
    backgroundColor: Color,
    iconBackgroundColor: Color,
    iconColor: Color,
    icon: ImageVector,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(135.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2E60).copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B2E60)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = Color(0xFF1B2E60).copy(alpha = 0.6f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NextAdventureCard(
    plan: Kegiatan,
    accumulatedBiaya: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("featured_next_adventure"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF0F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.judul,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2E60)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (!plan.lokasi.isNullOrBlank()) plan.lokasi!! else "Belum ditentukan lokasi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1B2E60).copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Priority Badge
                val isPenting = plan.prioritas.lowercase() == "penting"
                val badgeBg = if (isPenting) Color(0xFFFFF0F2) else Color(0xFFEEF1FF)
                val badgeTextColor = if (isPenting) Color(0xFFE91E63) else Color(0xFF3F51B5)

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = plan.prioritas.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Today,
                        contentDescription = null,
                        tint = Color(0xFF1B2E60).copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = plan.tanggalRencana,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF1B2E60).copy(alpha = 0.6f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total: ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2E60).copy(alpha = 0.4f)
                        )
                    )
                    Text(
                        text = "Rp" + formatThousand(accumulatedBiaya),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63)
                        )
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B2E60),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.heightIn(min = 36.dp)
                ) {
                    Text(
                        text = "Detail",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentPlanListItem(
    plan: Kegiatan,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("kegiatan_item_card_${plan.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline connector path drawing (replicates Screen 1)
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(86.dp),
            contentAlignment = Alignment.Center
        ) {
            val lineColor = Color(0xFFE91E63).copy(alpha = 0.2f)
            val dotColor = when (plan.status.lowercase()) {
                "selesai" -> Color(0xFF009688)
                "sedang berjalan" -> Color(0xFF3F51B5)
                "dibatalkan" -> Color(0xFFE91E63)
                else -> Color(0xFFFF9800)
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val startY = if (isFirst) size.height / 2 else 0f
                val endY = if (isLast) size.height / 2 else size.height
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(size.width / 2, startY),
                    end = androidx.compose.ui.geometry.Offset(size.width / 2, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val priorityColor = when (plan.prioritas.lowercase()) {
                    "penting" -> Color(0xFFE91E63)
                    "biasa" -> Color(0xFFFF9800)
                    else -> Color(0xFF3F51B5)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(priorityColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (plan.status.lowercase()) {
                            "selesai" -> Icons.Filled.CheckCircle
                            "sedang berjalan" -> Icons.Filled.PlayArrow
                            "dibatalkan" -> Icons.Filled.Cancel
                            else -> Icons.Filled.CalendarMonth
                        },
                        contentDescription = null,
                        tint = priorityColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.judul,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2E60)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = plan.status,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1B2E60).copy(alpha = 0.5f),
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = plan.tanggalRencana,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF1B2E60).copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Detail kencan",
                        tint = Color(0xFF1B2E60).copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatThousand(value: Double): String {
    return String.format("%,.0f", value).replace(",", ".")
}

