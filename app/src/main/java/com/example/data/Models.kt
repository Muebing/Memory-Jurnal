package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val partnerName: String,
    val pinCode: String // Simulating PIN/password for simple couple secure space
)

@Entity(tableName = "user_sessions")
data class UserSession(
    @PrimaryKey val id: Int = 1, // Single active session
    val userId: Int,
    val username: String,
    val email: String,
    val partnerName: String,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "kegiatan")
data class Kegiatan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val deskripsi: String,
    val tanggalRencana: String, // format: YYYY-MM-DD
    val jam: String? = null,
    val lokasi: String? = null,
    val prioritas: String, // Penting, Biasa, Santai
    val status: String,    // Belum Dikerjakan, Sedang Berjalan, Selesai, Dibatalkan
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dokumentasi")
data class Dokumentasi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kegiatanId: Int, // Foreign key to Kegiatan
    val story: String,
    val rating: Int, // 1 - 5
    val imageUri: String? = null, // Path to image (uri or preset string)
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "biaya")
data class Biaya(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kegiatanId: Int, // Foreign key to Kegiatan
    val pembayar: String, // "Samuel" (or user name) or "Pasangan"
    val keterangan: String,
    val nominal: Double,
    val timestamp: Long = System.currentTimeMillis()
)
