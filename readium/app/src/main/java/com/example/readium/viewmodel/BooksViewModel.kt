package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.data.model.Book
import com.example.readium.repository.BookRepository
import kotlinx.coroutines.launch

data class AddBookUiState(
    val isLoading: Boolean = false,
    val isbnSearchLoading: Boolean = false,
    val foundBook: Book? = null,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

class BooksViewModel(
    private val repository: BookRepository
) : ViewModel() {

    var books by mutableStateOf<List<Book>>(emptyList())
        private set

    var addBookUiState by mutableStateOf(AddBookUiState())
        private set

    fun loadBooks(userId: String) {
        repository.getUserBooks(userId) { list ->
            books = list
        }
    }

    fun saveBook(
        book: Book,
        userId: String,
        userName: String?
    ) {
        viewModelScope.launch {
            try {
                addBookUiState = addBookUiState.copy(
                    isLoading = true,
                    error = null,
                    savedSuccessfully = false
                )

                repository.addBook(
                    book = book,
                    userId = userId,
                    userName = userName
                )

                addBookUiState = addBookUiState.copy(
                    isLoading = false,
                    savedSuccessfully = true
                )

            } catch (e: Exception) {
                addBookUiState = addBookUiState.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao salvar livro"
                )
            }
        }
    }
}