package com.example.readium.repository

import com.example.readium.data.model.Book
import com.google.firebase.firestore.FirebaseFirestore

class BookRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val booksRef = firestore.collection("books")

    fun addBook(book: Book, userId: String, userName: String?) {
        val newBook = book.copy(
            ownerId = userId,
            ownerDisplayName = userName
        )

        booksRef.add(newBook)
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

}