package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs

class CoupleViewModel(application: Application, private val repository: CoupleRepository) : AndroidViewModel(application) {

    // --- SESSION STATE ---
    val activeSession = repository.activeSession.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    var authError = MutableStateFlow<String?>(null)
        private set
    var authSuccess = MutableStateFlow<Boolean>(false)
        private set

    // --- KEGIATAN STATE ---
    val allKegiatan = repository.allKegiatan.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow("Semua")
    val selectedPriorityFilter = MutableStateFlow("Semua")

    // Reactive Filtered Kegiatan
    val filteredKegiatan = combine(
        allKegiatan,
        searchQuery,
        selectedStatusFilter,
        selectedPriorityFilter
    ) { list, query, status, priority ->
        list.filter { item ->
            val matchQuery = item.judul.contains(query, ignoreCase = true) || 
                             item.deskripsi.contains(query, ignoreCase = true) ||
                             (item.lokasi?.contains(query, ignoreCase = true) ?: false)
            val matchStatus = status == "Semua" || item.status.equals(status, ignoreCase = true)
            val matchPriority = priority == "Semua" || item.prioritas.equals(priority, ignoreCase = true)

            matchQuery && matchStatus && matchPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- EXPENSES (BIAYA) & DOCUMENTATION STATE ---
    val allBiaya = repository.allBiaya.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allDokumentasi = repository.allDokumentasi.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- AUTHENTICATION ---
    fun register(username: String, email: String, partnerName: String, pinCode: String) {
        viewModelScope.launch {
            authError.value = null
            authSuccess.value = false
            if (username.isBlank() || email.isBlank() || partnerName.isBlank() || pinCode.isBlank()) {
                authError.value = "Semua bidang harus diisi"
                return@launch
            }
            val success = repository.register(username, email, partnerName, pinCode)
            if (success) {
                // Auto login
                val logged = repository.login(username, pinCode)
                if (logged) {
                    authSuccess.value = true
                } else {
                    authError.value = "Gagal login otomatis setelah registrasi"
                }
            } else {
                authError.value = "Username sudah terdaftar"
            }
        }
    }

    fun login(username: String, pinCode: String) {
        viewModelScope.launch {
            authError.value = null
            if (username.isBlank() || pinCode.isBlank()) {
                authError.value = "Username dan PIN tidak boleh kosong"
                return@launch
            }
            val success = repository.login(username, pinCode)
            if (!success) {
                authError.value = "PIN atau Username salah"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun clearAuthStates() {
        authError.value = null
        authSuccess.value = false
    }

    // --- KEGIATAN OPERATIONS ---
    fun addKegiatan(
        judul: String,
        deskripsi: String,
        tanggalRencana: String,
        jam: String?,
        lokasi: String?,
        prioritas: String,
        status: String
    ) {
        viewModelScope.launch {
            repository.saveKegiatan(
                Kegiatan(
                    judul = judul,
                    deskripsi = deskripsi,
                    tanggalRencana = tanggalRencana,
                    jam = if (jam.isNull_or_empty()) null else jam,
                    lokasi = if (lokasi.isNull_or_empty()) null else lokasi,
                    prioritas = prioritas,
                    status = status
                )
            )
        }
    }

    fun updateKegiatan(
        id: Int,
        judul: String,
        deskripsi: String,
        tanggalRencana: String,
        jam: String?,
        lokasi: String?,
        prioritas: String,
        status: String
    ) {
        viewModelScope.launch {
            repository.saveKegiatan(
                Kegiatan(
                    id = id,
                    judul = judul,
                    deskripsi = deskripsi,
                    tanggalRencana = tanggalRencana,
                    jam = if (jam.isNull_or_empty()) null else jam,
                    lokasi = if (lokasi.isNull_or_empty()) null else lokasi,
                    prioritas = prioritas,
                    status = status
                )
            )
        }
    }

    fun deleteKegiatan(kegiatan: Kegiatan) {
        viewModelScope.launch {
            repository.deleteKegiatan(kegiatan)
        }
    }

    private fun String?.isNull_or_empty(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    // --- EXPENSES (BIAYA) OPERATIONS ---
    fun addBiaya(kegiatanId: Int, pembayar: String, keterangan: String, nominal: Double) {
        viewModelScope.launch {
            repository.saveBiaya(
                Biaya(
                    kegiatanId = kegiatanId,
                    pembayar = pembayar,
                    keterangan = keterangan,
                    nominal = nominal
                )
            )
        }
    }

    fun deleteBiaya(biayaId: Int) {
        viewModelScope.launch {
            repository.deleteBiaya(biayaId)
        }
    }

    // --- DOKUMENTASI OPERATIONS ---
    fun addDokumentasi(kegiatanId: Int, story: String, rating: Int, imageUri: String?) {
        viewModelScope.launch {
            repository.saveDokumentasi(
                Dokumentasi(
                    kegiatanId = kegiatanId,
                    story = story,
                    rating = rating,
                    imageUri = imageUri
                )
            )
        }
    }

    fun deleteDokumentasi(dokumentasiId: Int) {
        viewModelScope.launch {
            repository.deleteDokumentasi(dokumentasiId)
        }
    }

    // --- METRIC CALCULATION HELPERS FOR A SPECIFIC EXERCISE/KEGIATAN ---
    fun getExpensesSummaryForKegiatan(kegiatanId: Int, currentUserName: String, partnerName: String): ExpensesSummary {
        val list = allBiaya.value.filter { it.kegiatanId == kegiatanId }
        val total = list.sumOf { it.nominal }

        // Determine user payments vs partner payments
        val userPaid = list.filter { it.pembayar.equals(currentUserName, ignoreCase = true) || it.pembayar.equals("Samuel", ignoreCase = true) || it.pembayar.equals("Saya", ignoreCase = true) }.sumOf { it.nominal }
        val partnerPaid = list.filter { it.pembayar.equals(partnerName, ignoreCase = true) || it.pembayar.equals("Pasangan", ignoreCase = true) }.sumOf { it.nominal }

        val share = total / 2.0
        val diffName: String
        val diffAmount: Double
        val debtStatusMessage: String

        if (userPaid > partnerPaid) {
            diffName = currentUserName
            diffAmount = (userPaid - partnerPaid) / 2.0
            debtStatusMessage = "$partnerName berhutang sebesar Rp${formatThousand(diffAmount)} kepada $currentUserName"
        } else if (partnerPaid > userPaid) {
            diffName = partnerName
            diffAmount = (partnerPaid - userPaid) / 2.0
            debtStatusMessage = "$currentUserName berhutang sebesar Rp${formatThousand(diffAmount)} kepada $partnerName"
        } else {
            diffName = "Seimbang"
            diffAmount = 0.0
            debtStatusMessage = "Pengeluaran seimbang! Tidak ada yang saling berhutang."
        }

        return ExpensesSummary(
            totalBiaya = total,
            totalDibayarPengguna = userPaid,
            totalDibayarPasangan = partnerPaid,
            selisihPengeluaran = abs(userPaid - partnerPaid),
            debtOwnerName = diffName,
            debtAmount = diffAmount,
            statusMessage = debtStatusMessage
        )
    }

    // --- GENERAL METRICS FOR DASHBOARD ---
    // Total, Completed, InProgress, Canceled etc.
    fun getDashboardMetrics(): DashboardMetrics {
        val list = allKegiatan.value
        val total = list.size
        val pending = list.count { it.status.equals("Belum Dikerjakan", ignoreCase = true) || it.status.equals("Sedang Berjalan", ignoreCase = true) }
        val completed = list.count { it.status.equals("Selesai", ignoreCase = true) }
        val totalExpenses = allBiaya.value.sumOf { it.nominal }

        return DashboardMetrics(
            totalKegiatan = total,
            belumSelesai = pending,
            selesai = completed,
            totalPengeluaran = totalExpenses,
            recentActivities = list.sortedByDescending { it.createdAt }.take(5)
        )
    }

    private fun formatThousand(value: Double): String {
        return String.format("%,.0f", value).replace(",", ".")
    }
}

data class ExpensesSummary(
    val totalBiaya: Double,
    val totalDibayarPengguna: Double,
    val totalDibayarPasangan: Double,
    val selisihPengeluaran: Double, // Raw difference between what user paid and partner paid
    val debtOwnerName: String,
    val debtAmount: Double, // Half of raw difference (for equal splitting)
    val statusMessage: String
)

data class DashboardMetrics(
    val totalKegiatan: Int,
    val belumSelesai: Int,
    val selesai: Int,
    val totalPengeluaran: Double,
    val recentActivities: List<Kegiatan>
)

class CoupleViewModelFactory(private val application: Application, private val repository: CoupleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoupleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoupleViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
