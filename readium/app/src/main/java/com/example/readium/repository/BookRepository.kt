package com.example.readium.repository

import com.example.readium.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun addBook(book: Book): Result<String> // retorna id do documento ou erro
    suspend fun updateBook(book: Book): Result<Unit>
    fun observeBooksByUser(userId: String): Flow<List<Book>>
    suspend fun fetchBookByIsbn(isbn: String): Result<Book> // usa Google Books
}