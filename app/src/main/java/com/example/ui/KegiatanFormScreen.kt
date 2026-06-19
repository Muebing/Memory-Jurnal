package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanFormScreen(
    viewModel: CoupleViewModel,
    mode: String, // "add" or "edit"
    kegiatanId: Int, // -1 if add
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEdit = mode == "edit"
    val allKegiatan by viewModel.allKegiatan.collectAsStateWithLifecycle()

    // Form states
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var tanggalRencana by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var prioritas by remember { mutableStateOf("Biasa") }
    var status by remember { mutableStateOf("Belum Dikerjakan") }

    var isInitialized by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // On edit, load from repository/flow once
    LaunchedEffect(allKegiatan, isEdit, kegiatanId) {
        if (isEdit && kegiatanId != -1 && !isInitialized) {
            val plan = allKegiatan.find { it.id == kegiatanId }
            plan?.let {
                judul = it.judul
                deskripsi = it.deskripsi
                tanggalRencana = it.tanggalRencana
                jam = it.jam ?: ""
                lokasi = it.lokasi ?: ""
                prioritas = it.prioritas
                status = it.status
                isInitialized = true
            }
        }
    }

    // Set default date if empty and adding
    LaunchedEffect(Unit) {
        if (!isEdit && tanggalRencana.isEmpty()) {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH) + 1
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            tanggalRencana = String.format("%04d-%02d-%02d", year, month, day)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEdit) "Edit Rencana Kita" else "Rencana Kegiatan Baru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error overlay
            if (showError) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Judul Rencana dan Tanggal tidak boleh kosong!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onErrorContainer)
                        )
                    }
                }
            }

            // Input: Title
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it; showError = false },
                label = { Text("Judul Kegiatan") },
                placeholder = { Text("Contoh: Dinner Romantis, Berburu Ramen, Nonton Bioskop") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_judul")
            )

            // Input: Description
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = { Text("Deskripsi Rencana / Rincian") },
                placeholder = { Text("Tuliskan aktivitas atau kejutan kecil yang ingin dilakukan...") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_deskripsi")
            )

            // Row: Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = tanggalRencana,
                    onValueChange = { tanggalRencana = it; showError = false },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    placeholder = { Text("Tahun-Bulan-Tanggal") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("field_tanggal")
                )

                OutlinedTextField(
                    value = jam,
                    onValueChange = { jam = it },
                    label = { Text("Jam (Opsional)") },
                    placeholder = { Text("18:30 / 19:00") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("field_jam")
                )
            }

            // Input: Location
            OutlinedTextField(
                value = lokasi,
                onValueChange = { lokasi = it },
                label = { Text("Lokasi (Opsional)") },
                placeholder = { Text("Sebutkan nama Resto, Mall, Bioskop, atau Taman") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_lokasi")
            )

            // Segmented Picker For Prioritas (Penting, Biasa, Santai)
            Text(
                text = "Prioritas Kegiatan",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Penting", "Biasa", "Santai").forEach { p ->
                    val isSelected = prioritas == p
                    val buttonColor = when (p.lowercase()) {
                        "penting" -> MaterialTheme.colorScheme.primary
                        "biasa" -> Color(0xFFFF9800)
                        else -> Color(0xFF03A9F4)
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { prioritas = p }
                            .testTag("prioritas_chip_$p"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) buttonColor else buttonColor.copy(alpha = 0.08f),
                            contentColor = if (isSelected) Color.White else buttonColor
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = p, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Segmented Picker For Status (only edit details or start with defaults)
            Text(
                text = "Status Kegiatan",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Belum Dikerjakan", "Sedang Berjalan").forEach { s ->
                        val isSelected = status == s
                        val buttonColor = if (s.contains("Sedang")) Color(0xFF2196F3) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { status = s }
                                .testTag("status_chip_${s.replace(" ", "_")}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) buttonColor else buttonColor.copy(alpha = 0.1f),
                                contentColor = if (isSelected) Color.White else buttonColor
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = s, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Selesai", "Dibatalkan").forEach { s ->
                        val isSelected = status == s
                        val buttonColor = if (s == "Selesai") Color(0xFF4CAF50) else Color(0xFFE91E63)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { status = s }
                                .testTag("status_chip_${s.replace(" ", "_")}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) buttonColor else buttonColor.copy(alpha = 0.1f),
                                contentColor = if (isSelected) Color.White else buttonColor
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = s, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cancel_form_button")
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        if (judul.isBlank() || tanggalRencana.isBlank()) {
                            showError = true
                        } else {
                            if (isEdit) {
                                viewModel.updateKegiatan(
                                    id = kegiatanId,
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    tanggalRencana = tanggalRencana,
                                    jam = jam,
                                    lokasi = lokasi,
                                    prioritas = prioritas,
                                    status = status
                                )
                            } else {
                                viewModel.addKegiatan(
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    tanggalRencana = tanggalRencana,
                                    jam = jam,
                                    lokasi = lokasi,
                                    prioritas = prioritas,
                                    status = status
                                )
                            }
                            onNavigateBack() // Go back
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("submit_form_button")
                ) {
                    Text(
                        text = if (isEdit) "Simpan Perubahan" else "Buat Rencana",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
