package com.example.projekakhir.be.data.repository

import android.net.Uri
import com.example.projekakhir.be.data.model.ItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AddItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Upload gambar ke Firebase Storage
    suspend fun uploadImage(uri: Uri): String {
        val filename = "items/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(filename)

        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    // Simpan data item ke Firestore
    suspend fun uploadItem(item: ItemModel): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User belum login")

            val newItem = item.copy(userId = user.uid)

            db.collection("items").add(newItem).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
