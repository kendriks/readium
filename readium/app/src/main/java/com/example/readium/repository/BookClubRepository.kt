package com.example.readium.repository

import com.example.readium.data.model.ClubeLeitura
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookClubRepository {

    private val db = FirebaseFirestore.getInstance()
    private val clubsCollection = db.collection("book_clubs")

    // Criar um novo clube
    suspend fun createClub(club: ClubeLeitura): Boolean {
        return try {
            clubsCollection.add(club).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Buscar clubes onde o usuário já é membro (Meus Clubes)
    suspend fun getUserClubs(userId: String): List<ClubeLeitura> {
        return try {
            val snapshot = clubsCollection
                .whereArrayContains("members", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ClubeLeitura::class.java)?.apply { id = doc.id }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Buscar clubes públicos (Search) - RN02
    suspend fun getPublicClubs(): List<ClubeLeitura> {
        return try {
            val snapshot = clubsCollection
                .whereEqualTo("private", false) // Filtra apenas os públicos
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ClubeLeitura::class.java)?.apply { id = doc.id }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Entrar em um clube
    suspend fun joinClub(clubId: String, userId: String, isPrivate: Boolean): Boolean {
        return try {
            val fieldToUpdate = if (isPrivate) "pendingRequests" else "members"

            clubsCollection.document(clubId)
                .update(fieldToUpdate, FieldValue.arrayUnion(userId))
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}