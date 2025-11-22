package com.example.projekakhir.be.data.repository

import com.example.projekakhir.be.data.model.ItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class MarketPlaceRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Ambil semua item untuk marketplace
    suspend fun getAllItems(): Result<List<ItemModel>> {
        return try {
            val snapshot = db.collection("items")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val items = snapshot.documents.map {
                it.toObject(ItemModel::class.java)?.copy(id = it.id) ?: ItemModel()
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ambil item untuk user tertentu (History)
    suspend fun getUserItems(userId: String): Result<List<ItemModel>> {
        return try {
            val snapshot = db.collection("items")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val items = snapshot.documents.map {
                it.toObject(ItemModel::class.java)?.copy(id = it.id) ?: ItemModel()
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update status item
    suspend fun updateItemStatus(itemId: String, status: String): Result<Unit> {
        return try {
            db.collection("items").document(itemId)
                .update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Upload item baru
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
