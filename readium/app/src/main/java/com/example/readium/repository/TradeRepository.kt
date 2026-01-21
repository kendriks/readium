package com.example.readium.repository

import com.example.readium.data.model.Book
import com.example.readium.data.model.TradeProposal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("books")
    private val proposalsCollection = db.collection("trade_proposals")

    suspend fun searchBooksForTrade(query: String): List<Book> {
        return try {
            // Busca todos os livros marcados como disponíveis para troca
            val snapshot = booksCollection
                .whereEqualTo("availableForTrade", true)
                .get()
                .await()

            val allTradeableBooks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.apply { id = doc.id }
            }

            // Filtragem local por Título ou Gênero (Categorias)
            if (query.isBlank()) {
                allTradeableBooks
            } else {
                val lowercaseQuery = query.lowercase()
                allTradeableBooks.filter { book ->
                    book.title.lowercase().contains(lowercaseQuery) ||
                            book.categories.any { it.lowercase().contains(lowercaseQuery) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun sendTradeProposal(proposal: TradeProposal): Boolean {
        return try {
            proposalsCollection.add(proposal).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}