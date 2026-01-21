package com.example.readium.repository

import com.example.readium.data.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import androidx.core.net.toUri

class BookRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val booksRef = firestore.collection("books")

    private val storage = FirebaseStorage.getInstance()

    suspend fun addBook(
        book: Book,
        userId: String,
        userName: String?
    ) {
        val newBook = book.copy(
            ownerId = userId,
            ownerDisplayName = userName
        )

        val docRef = booksRef.add(newBook).await()
        val bookId = docRef.id

        if (!book.coverUrl.isNullOrBlank() && book.coverUrl!!.startsWith("content://")) {
            val downloadUrl = uploadBookCover(bookId, book.coverUrl!!)
            val finalUrl = "$downloadUrl?ts=${System.currentTimeMillis()}"

            docRef.update("coverUrl", finalUrl).await()
        }
    }


    private suspend fun uploadBookCover(
        bookId: String,
        coverUri: String
    ): String {
        val uri = coverUri.toUri()

        val ref = storage.reference.child("book_covers/$bookId.jpg")

        ref.putFile(uri).await()

        return ref.downloadUrl.await().toString()
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

        var updatedBook = book

        if (!book.coverUrl.isNullOrBlank() && book.coverUrl!!.startsWith("content://")) {
            val downloadUrl = uploadBookCover(book.id, book.coverUrl!!)
            updatedBook = book.copy(
                coverUrl = "$downloadUrl?ts=${System.currentTimeMillis()}"
            )
        }

        booksRef
            .document(book.id)
            .set(updatedBook)
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
