package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Biaya
import com.example.data.Dokumentasi
import com.example.data.Kegiatan
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.layout.ContentScale
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanDetailScreen(
    viewModel: CoupleViewModel,
    kegiatanId: Int,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val allKegiatan by viewModel.allKegiatan.collectAsStateWithLifecycle()
    val allBiaya by viewModel.allBiaya.collectAsStateWithLifecycle()
    val allDokumentasi by viewModel.allDokumentasi.collectAsStateWithLifecycle()

    val plan = allKegiatan.find { it.id == kegiatanId }

    val userSession = activeSession
    val currentUserName = userSession?.username ?: "Samuel"
    val partnerName = userSession?.partnerName ?: "Pasangan"

    // Dialog trigger states
    var showAddBiayaDialog by remember { mutableStateOf(false) }
    var showAddDokumentasiDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (plan == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Rencana tidak ditemukan atau telah dihapus.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Kembali")
                }
            }
        }
        return
    }

    // Dynamic metrics for the selected plan
    val summary = viewModel.getExpensesSummaryForKegiatan(plan.id, currentUserName, partnerName)
    val pBiaya = allBiaya.filter { it.kegiatanId == plan.id }
    val pDokumentasi = allDokumentasi.filter { it.kegiatanId == plan.id }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(plan.judul, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_to_list_button")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(plan.id) }, modifier = Modifier.testTag("edit_kegiatan_button")) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Rencana", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.testTag("delete_kegiatan_button")) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus Rencana", tint = MaterialTheme.colorScheme.error)
                    }
                },
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
            // INFORMATION CARD
            item {
                InformationSection(plan = plan)
            }

            // MODUL BIAYA & EXPENSES SPLIT SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_split_section_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Payments, contentDescription = "Budget Split Tracker", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Anggaran & Pengeluaran",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            IconButton(
                                onClick = { showAddBiayaDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("add_expense_dialog_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Tambah Catatan Biaya", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Calculations summary
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                DisplayCalculationRow(label = "Total Pengeluaran", value = "Rp" + formatThousand(summary.totalBiaya), isBold = true, brandColor = MaterialTheme.colorScheme.primary)
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                                DisplayCalculationRow(label = "Dibayar oleh $currentUserName (Saya)", value = "Rp" + formatThousand(summary.totalDibayarPengguna))
                                DisplayCalculationRow(label = "Dibayar oleh $partnerName (Mitra)", value = "Rp" + formatThousand(summary.totalDibayarPasangan))
                                DisplayCalculationRow(label = "Selisih Pengeluaran", value = "Rp" + formatThousand(summary.selisihPengeluaran))

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = summary.statusMessage,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Expenses entries list
                        if (pBiaya.isEmpty()) {
                            Text(
                                text = "Belum ada catatan biaya. Tap tombol + untuk menambah pengeluaran.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        } else {
                            Text(
                                text = "Daftar Biaya:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            pBiaya.forEach { charge ->
                                ExpenseLineRow(
                                    charge = charge,
                                    userPaid = charge.pembayar.equals(currentUserName, ignoreCase = true) || charge.pembayar.equals("Saya", ignoreCase = true) || charge.pembayar.equals("Samuel", ignoreCase = true),
                                    onDelete = { viewModel.deleteBiaya(charge.id) },
                                    userName = currentUserName,
                                    partnerName = partnerName
                                )
                            }
                        }
                    }
                }
            }

            // MODUL DOKUMENTASI (MEMORIES) SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("documentation_section_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CameraEnhance, contentDescription = "Memories", tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Dokumentasi & Kenangan",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            // Only allow adding documentation if status is finished (Selesai)
                            val isCompleted = plan.status.equals("Selesai", ignoreCase = true)
                            if (isCompleted) {
                                IconButton(
                                    onClick = { showAddDokumentasiDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("add_memory_dialog_button")
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Tambah Kenaman", tint = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isCompleted = plan.status.equals("Selesai", ignoreCase = true)
                        if (!isCompleted) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Dokumentasi kenangan dapat diisi setelah status kegiatan diubah menjadi 'Selesai'.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSecondaryContainer),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Memories entries list
                        if (pDokumentasi.isEmpty()) {
                            if (isCompleted) {
                                Text(
                                    text = "Kegiatan telah selesai! Tap tombol + di atas untuk menceritakan keseruanmu dan memberi rating kencan.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                )
                            } else {
                                Text(
                                    text = "Belum ada kenangan disimpan.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                )
                            }
                        } else {
                            pDokumentasi.forEach { memory ->
                                MemoryItemRow(
                                    memory = memory,
                                    onDelete = { viewModel.deleteDokumentasi(memory.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOGS SECTION

    // 1. Delete Activity Confirm Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Rencana Kegiatan?") },
            text = { Text("Apakah kamu yakin ingin menghapus agenda '${plan.judul}'? Data biaya dan kenangan terkait juga akan terhapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteKegiatan(plan)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // 2. Add Expense Line Dialog
    if (showAddBiayaDialog) {
        var payerIsUser by remember { mutableStateOf(true) }
        var comment by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddBiayaDialog = false },
            title = { Text("Tambah Pengeluaran") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (inputError) {
                        Text("Keterangan dan Nominal harus valid!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Row toggle who paid
                    Text("Siapa yang membayar?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { payerIsUser = true }
                                .testTag("payer_myself"),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (payerIsUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                contentColor = if (payerIsUser) Color.White else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Saya ($currentUserName)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { payerIsUser = false }
                                .testTag("payer_partner"),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!payerIsUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                contentColor = if (!payerIsUser) Color.White else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("$partnerName", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it; inputError = false },
                        label = { Text("Keterangan / Item") },
                        placeholder = { Text("Tiket nonton, GrabCar, Dinner, dll") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_descr")
                    )

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it; inputError = false },
                        label = { Text("Nominal Pengeluaran (Rp)") },
                        placeholder = { Text("Masing-masing pengeluaran") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_amount")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull()
                        if (comment.isBlank() || amt == null || amt <= 0) {
                            inputError = true
                        } else {
                            val payerName = if (payerIsUser) currentUserName else partnerName
                            viewModel.addBiaya(
                                kegiatanId = plan.id,
                                pembayar = payerName,
                                keterangan = comment,
                                nominal = amt
                            )
                            showAddBiayaDialog = false
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBiayaDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // 3. Add Memory Documentation Dialog
    if (showAddDokumentasiDialog) {
        var captionText by remember { mutableStateOf("") }
        var starRating by remember { mutableStateOf(5) }
        var imageSelectedPreset by remember { mutableStateOf("coffee") }
        var customImageUri by remember { mutableStateOf<String?>(null) }
        var isFormError by remember { mutableStateOf(false) }

        val imagePresets = listOf(
            PresetItem("coffee", "☕ Coffee Date", "ic_love_coffee"),
            PresetItem("dinner", "🍔 Romantic Dinner", "ic_love_dinner"),
            PresetItem("movie", "🎬 Cinema Night", "ic_love_movie"),
            PresetItem("travel", "🌴 Cozy Trip", "ic_love_travel")
        )

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                customImageUri = it.toString()
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDokumentasiDialog = false },
            title = { Text("Simpan Kenangan Indah ") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (isFormError) {
                        Text("Cerita/caption tidak boleh kosong!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // STARRING
                    Text("Bagaimana ulasan kencanmu?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            val active = star <= starRating
                            IconButton(onClick = { starRating = star }) {
                                Icon(
                                    imageVector = if (active) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "$star Bintang",
                                    tint = if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // CAPTION STORY
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it; isFormError = false },
                        label = { Text("Ceritakan keseruan / kesannya") },
                        placeholder = { Text("Ceritakan kenanganmu bersama kekasih...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("add_memory_caption")
                    )

                    // PHOTO SELECTOR
                    Text("Foto Kenangan Kita:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    if (customImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            coil.compose.AsyncImage(
                                model = customImageUri,
                                contentDescription = "Preview Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { customImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus Foto", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("upload_photo_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unggah Foto dari Galeri", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Text("Atau gunakan Ilustrasi Estetika:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)))

                    // PRESET ILLUS SELECTOR
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        imagePresets.forEach { preset ->
                            val select = imageSelectedPreset == preset.id && customImageUri == null
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { 
                                        imageSelectedPreset = preset.id
                                        customImageUri = null
                                    }
                                    .testTag("preset_${preset.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (select) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                    contentColor = if (select) Color.White else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(preset.text, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (captionText.isBlank()) {
                            isFormError = true
                        } else {
                            viewModel.addDokumentasi(
                                kegiatanId = plan.id,
                                story = captionText,
                                rating = starRating,
                                imageUri = customImageUri ?: imageSelectedPreset
                            )
                            showAddDokumentasiDialog = false
                        }
                    }
                ) {
                    Text("Simpan Memori")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDokumentasiDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

data class PresetItem(val id: String, val text: String, val assetName: String)

@Composable
fun InformationSection(plan: Kegiatan, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.testTag("info_section_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Priority & Status Tags Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Tag
                val priorityColor = when (plan.prioritas.lowercase()) {
                    "penting" -> MaterialTheme.colorScheme.primary
                    "biasa" -> Color(0xFFFF9800)
                    else -> Color(0xFF03A9F4)
                }
                Surface(
                    color = priorityColor.copy(alpha = 0.1f),
                    contentColor = priorityColor,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Prioritas ${plan.prioritas}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // Status Tag
                val statusColor = when (plan.status.lowercase()) {
                    "selesai" -> Color(0xFF4CAF50)
                    "sedang berjalan" -> Color(0xFF2196F3)
                    "dibatalkan" -> Color(0xFFE91E63)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    contentColor = statusColor,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = plan.status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = plan.judul,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (plan.deskripsi.isNotBlank()) {
                Text(
                    text = plan.deskripsi,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

            // Time & Date information row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Waktu Rencana",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = plan.tanggalRencana + (plan.jam?.let { " pukul $it" } ?: ""),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // Location information row (if available)
            plan.lokasi?.let { place ->
                if (place.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Place, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Lokasi Kencan",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = place,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayCalculationRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    brandColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
            color = if (isBold && brandColor != null) brandColor else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (brandColor != null) brandColor else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ExpenseLineRow(
    charge: Biaya,
    userPaid: Boolean,
    onDelete: () -> Unit,
    userName: String,
    partnerName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = charge.keterangan,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Dibayar oleh: " + (if (userPaid) "$userName (Saya)" else partnerName),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rp" + formatThousand(charge.nominal),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus catatan pengeluaran",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryItemRow(
    memory: Dokumentasi,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustomImage = memory.imageUri != null && !listOf("coffee", "dinner", "movie", "travel").contains(memory.imageUri)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Love Preset Representation Icon / Tag
                val (presetEmoji, presetBgColor) = when (memory.imageUri) {
                    "coffee" -> Pair("☕", Color(0xFF8D6E63))
                    "dinner" -> Pair("🍔", Color(0xFFFDD835))
                    "movie" -> Pair("🎬", Color(0xFF7E57C2))
                    "travel" -> Pair("🌴", Color(0xFF26A69A))
                    else -> Pair("📸", MaterialTheme.colorScheme.primary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(presetBgColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = presetEmoji, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Stars rating representation
                    Row {
                        (1..5).forEach { star ->
                            val isLit = star <= memory.rating
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (isLit) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Delete memory button
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Kenangan",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cerita singkat/Story
            Text(
                text = "\"" + memory.story + "\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            if (isCustomImage && memory.imageUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    coil.compose.AsyncImage(
                        model = memory.imageUri,
                        contentDescription = "Foto Kenangan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun formatThousand(value: Double): String {
    return String.format("%,.0f", value).replace(",", ".")
}
