package com.example.readium.repository

import com.example.readium.data.model.Book
import com.example.readium.data.remote.GoogleBooksApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.util.*

class FirebaseBookRepository(
    private val firestore: FirebaseFirestore,
    private val googleBooksApi: GoogleBooksApi
) : BookRepository {

    private val booksColl = firestore.collection("books")

    override suspend fun addBook(book: Book): Result<String> {
        return try {
            val docRef = booksColl.document()
            book.id = docRef.id

            docRef.set(book).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBook(book: Book): Result<Unit> {
        return try {
            if (book.id.isBlank()) throw IllegalArgumentException("Book id is required for update")
            booksColl.document(book.id).set(book).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeBooksByUser(userId: String): Flow<List<Book>> = callbackFlow {
        val query = booksColl
            .whereEqualTo("ownerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                try {
                    val book = doc.toObject(Book::class.java)!!
                    book.id = doc.id
                    book
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            trySend(list).isSuccess
        }

        awaitClose { listener.remove() }
    }

    override suspend fun fetchBookByIsbn(isbn: String): Result<Book> {
        return try {

            val response = googleBooksApi.searchByIsbn("isbn:$isbn")
            val items = response.items
            if (items.isNullOrEmpty()) {
                Result.failure(NoSuchElementException("Nenhum resultado para ISBN $isbn"))
            } else {
                val v = items.first()
                val info = v.volumeInfo
                val book = Book(
                    title = info.title ?: "",
                    authors = info.authors ?: emptyList(),
                    isbn = isbn,
                    coverUrl = info.imageLinks?.thumbnail ?: info.imageLinks?.smallThumbnail,
                    publisher = info.publisher,
                    publishedDate = info.publishedDate,
                    description = info.description,
                    categories = info.categories ?: emptyList(),

                    createdAt = Date()
                )
                Result.success(book)
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}