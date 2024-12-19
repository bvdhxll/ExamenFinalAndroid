package com.programacionandroid.examenfinal

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionName = "Item"

    suspend fun getItems(): List<Item> {
        return try {
            val snapshot = firestore.collection(collectionName).get().await()
            snapshot.documents.mapNotNull { it.toObject(Item::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addItem(item: Item, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(collectionName)
            .add(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}