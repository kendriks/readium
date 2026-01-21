package com.example.readium.repository

import com.example.readium.data.model.Book
import com.example.readium.data.model.TradeProposal
import com.example.readium.data.model.TradeStatus
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

    suspend fun getReceivedProposals(userId: String): List<TradeProposal> {
        return try {
            val snapshot = proposalsCollection
                .whereEqualTo("receiverId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(TradeProposal::class.java)?.apply { id = doc.id }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateProposalStatus(
        proposalId: String,
        bookId: String,
        newStatus: TradeStatus
    ): Boolean {
        return try {
            val batch = db.batch()

            val proposalRef = proposalsCollection.document(proposalId)
            batch.update(proposalRef, "status", newStatus)

            // Se aceitou a troca, marca o livro como indisponível
            if (newStatus == TradeStatus.ACCEPTED) {
                val bookRef = booksCollection.document(bookId)
                batch.update(bookRef, "availableForTrade", false)
            }

            batch.commit().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}