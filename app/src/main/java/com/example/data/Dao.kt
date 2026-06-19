package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleDao {

    // --- USER & SESSION ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSession)

    @Query("SELECT * FROM user_sessions WHERE id = 1 LIMIT 1")
    fun getActiveSessionFlow(): Flow<UserSession?>

    @Query("SELECT * FROM user_sessions WHERE id = 1 LIMIT 1")
    suspend fun getActiveSession(): UserSession?

    @Query("DELETE FROM user_sessions WHERE id = 1")
    suspend fun clearSession()

    // --- KEGIATAN (ACTIVITIES) ---
    @Query("SELECT * FROM kegiatan ORDER BY tanggalRencana ASC, jam ASC")
    fun getAllKegiatanFlow(): Flow<List<Kegiatan>>

    @Query("SELECT * FROM kegiatan WHERE id = :id LIMIT 1")
    fun getKegiatanByIdFlow(id: Int): Flow<Kegiatan?>

    @Query("SELECT * FROM kegiatan WHERE id = :id LIMIT 1")
    suspend fun getKegiatanById(id: Int): Kegiatan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKegiatan(kegiatan: Kegiatan): Long

    @Delete
    suspend fun deleteKegiatan(kegiatan: Kegiatan)

    // --- DOKUMENTASI (DOCUMENTATION / MEMORIES) ---
    @Query("SELECT * FROM dokumentasi ORDER BY timestamp DESC")
    fun getAllDokumentasiFlow(): Flow<List<Dokumentasi>>

    @Query("SELECT * FROM dokumentasi WHERE kegiatanId = :kegiatanId ORDER BY timestamp DESC")
    fun getDokumentasiForKegiatanFlow(kegiatanId: Int): Flow<List<Dokumentasi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDokumentasi(dokumentasi: Dokumentasi): Long

    @Query("DELETE FROM dokumentasi WHERE id = :id")
    suspend fun deleteDokumentasi(id: Int)

    // --- BIAYA (EXPENSES) ---
    @Query("SELECT * FROM biaya ORDER BY timestamp DESC")
    fun getAllBiayaFlow(): Flow<List<Biaya>>

    @Query("SELECT * FROM biaya WHERE kegiatanId = :kegiatanId ORDER BY timestamp DESC")
    fun getBiayaForKegiatanFlow(kegiatanId: Int): Flow<List<Biaya>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiaya(biaya: Biaya): Long

    @Query("DELETE FROM biaya WHERE id = :id")
    suspend fun deleteBiaya(id: Int)
}
