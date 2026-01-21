package com.example.readium.repository

import com.example.readium.data.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val booksRef = firestore.collection("books")


    suspend fun addBook(
        book: Book,
        userId: String,
        userName: String?
    ) {
        val newBook = book.copy(
            ownerId = userId,
            ownerDisplayName = userName
        )

        booksRef.add(newBook).await()
    }

    fun getUserBooks(
        userId: String,
        onResult: (List<Book>) -> Unit
    ) {
        booksRef
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, _ ->

                val books = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Book::class.java)?.apply {
                        id = doc.id
                    }
                }.orEmpty()

                onResult(books)
            }
    }


    suspend fun updateBook(book: Book) {
        if (book.id.isBlank()) return

        booksRef
            .document(book.id)
            .set(book)
            .await()
    }

    suspend fun deleteBook(book: Book) {
        if (book.id.isBlank()) return

        booksRef
            .document(book.id)
            .delete()
            .await()
    }
}
