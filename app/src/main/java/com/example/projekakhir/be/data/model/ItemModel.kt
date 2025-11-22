package com.example.projekakhir.be.data.model

import com.google.firebase.Timestamp

data class ItemModel(
    val id: String = "",
    val userId: String = "",
    val namaBarang: String = "",
    val harga: Double = 0.0,
    val deskripsi: String = "",
    val kategori: String = "",
    val kondisi: String = "", // Tambah untuk Resell
    val ukuran: String = "", // Tambah untuk Resell
    val imageUrl: String = "",
    val lokasiPickup: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val jenis: String = "resell", // resell, donate, recycle
    val status: String = "pending" // pending, picked_up, delivered
)