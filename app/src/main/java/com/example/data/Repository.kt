package com.example.data

import kotlinx.coroutines.flow.Flow

class CoupleRepository(private val dao: CoupleDao) {

    // --- SESSION ---
    val activeSession: Flow<UserSession?> = dao.getActiveSessionFlow()

    suspend fun getActiveSession(): UserSession? = dao.getActiveSession()

    suspend fun register(username: String, email: String, partnerName: String, pinCode: String): Boolean {
        // Simple duplicate user check
        val existing = dao.getUserByUsername(username)
        if (existing != null) return false

        val user = User(username = username, email = email, partnerName = partnerName, pinCode = pinCode)
        dao.registerUser(user)
        return true
    }

    suspend fun login(username: String, pinCode: String): Boolean {
        val user = dao.getUserByUsername(username) ?: return false
        if (user.pinCode == pinCode) {
            val session = UserSession(
                userId = user.id,
                username = user.username,
                email = user.email,
                partnerName = user.partnerName,
                isLoggedIn = true
            )
            dao.insertSession(session)
            return true
        }
        return false
    }

    suspend fun logout() {
        dao.clearSession()
    }

    // --- KEGIATAN ---
    val allKegiatan: Flow<List<Kegiatan>> = dao.getAllKegiatanFlow()

    fun getKegiatanById(id: Int): Flow<Kegiatan?> = dao.getKegiatanByIdFlow(id)

    suspend fun getKegiatan(id: Int): Kegiatan? = dao.getKegiatanById(id)

    suspend fun saveKegiatan(kegiatan: Kegiatan): Long {
        return dao.insertKegiatan(kegiatan)
    }

    suspend fun deleteKegiatan(kegiatan: Kegiatan) {
        dao.deleteKegiatan(kegiatan)
    }

    // --- DOKUMENTASI (MEMORIES) ---
    val allDokumentasi: Flow<List<Dokumentasi>> = dao.getAllDokumentasiFlow()

    fun getDokumentasiForKegiatan(kegiatanId: Int): Flow<List<Dokumentasi>> {
        return dao.getDokumentasiForKegiatanFlow(kegiatanId)
    }

    suspend fun saveDokumentasi(dokumentasi: Dokumentasi): Long {
        return dao.insertDokumentasi(dokumentasi)
    }

    suspend fun deleteDokumentasi(id: Int) {
        dao.deleteDokumentasi(id)
    }

    // --- BIAYA (EXPENSES) ---
    val allBiaya: Flow<List<Biaya>> = dao.getAllBiayaFlow()

    fun getBiayaForKegiatan(kegiatanId: Int): Flow<List<Biaya>> {
        return dao.getBiayaForKegiatanFlow(kegiatanId)
    }

    suspend fun saveBiaya(biaya: Biaya): Long {
        return dao.insertBiaya(biaya)
    }

    suspend fun deleteBiaya(id: Int) {
        dao.deleteBiaya(id)
    }
}
