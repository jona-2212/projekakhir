package com.example.projekakhir.be.data.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekakhir.be.data.model.ItemModel
import com.example.projekakhir.be.data.repository.AddItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val msg: String) : UploadState()
}

class AddItemViewModel(
    private val repository: AddItemRepository = AddItemRepository()
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun uploadItem(
        nama: String,
        harga: Double,
        kategori: String,
        deskripsi: String,
        lokasi: String,
        jenis: String,
        kondisi: String = "",
        ukuran: String = "",
        lat: Double? = null,
        lon: Double? = null,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading

                // 1. Upload Image (pastikan imageUri tidak null)
                if (imageUri == null) {
                    _uploadState.value = UploadState.Error("Gambar harus dipilih")
                    return@launch
                }

                val imageUrl = repository.uploadImage(imageUri)

                // 2. Buat item
                val item = ItemModel(
                    namaBarang = nama,
                    harga = harga,
                    kategori = kategori,
                    kondisi = kondisi,
                    ukuran = ukuran,
                    deskripsi = deskripsi,
                    lokasiPickup = lokasi,
                    imageUrl = imageUrl,
                    jenis = jenis,
                    latitude = lat,
                    longitude = lon,
                    status = if (jenis == "resell") "available" else "pending_pickup"
                )

                // 3. Upload ke Firestore
                val result = repository.uploadItem(item)

                if (result.isSuccess) {
                    _uploadState.value = UploadState.Success
                } else {
                    _uploadState.value = UploadState.Error("Gagal upload item")
                }

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Error tidak diketahui")
            }
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}