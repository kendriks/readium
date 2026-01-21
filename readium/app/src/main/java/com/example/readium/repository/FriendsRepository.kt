package com.example.readium.repository

import com.example.readium.data.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FriendsRepository {

    private val db = FirebaseFirestore.getInstance()


    suspend fun getFriendsIds(userId: String): List<String> {
        return try {
            val doc = db.collection("friends").document(userId).get().await()
            val friendIds = doc.get("ids_friends") as? List<String> ?: emptyList()

            if (friendIds.isEmpty()) return emptyList()

           return friendIds

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getFriends(userId: String): List<User> {
        return try {
            val doc = db.collection("friends").document(userId).get().await()
            val friendIds = doc.get("ids_friends") as? List<String> ?: emptyList()

            if (friendIds.isEmpty()) return emptyList()

            val allFriends = mutableListOf<User>()

            // Divide os IDs em pedaços de 10 para respeitar o limite do Firestore
            val chunks = friendIds.chunked(10)

            for (chunk in chunks) {
                // Busca cada lote de usuários
                val result = db.collection("users")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val users = result.documents.mapNotNull { document ->
                    val user = document.toObject(User::class.java)
                    user?.id = document.id
                    user
                }
                allFriends.addAll(users)
            }

            allFriends
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Remove um amigo atualizando as listas de ambos os usuários
    suspend fun removeFriend(userId: String, friendId: String): Boolean {
        return try {
            val currentUserRef = db.collection("friends").document(userId)
            val friendRef = db.collection("friends").document(friendId)

            db.runBatch { batch ->
                // Remove o amigo da lista do usuário atual
                batch.update(currentUserRef, "ids_friends", FieldValue.arrayRemove(friendId))
                // Remove o usuário atual da lista do amigo
                batch.update(friendRef, "ids_friends", FieldValue.arrayRemove(userId))
            }.await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}