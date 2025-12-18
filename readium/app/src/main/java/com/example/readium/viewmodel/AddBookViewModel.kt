package com.example.readium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.data.model.Book
import com.example.readium.data.model.BookStatus
import com.example.readium.repository.BookRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddBookUiState(
    val isLoading: Boolean = false,
    val isbnSearchLoading: Boolean = false,
    val foundBook: Book? = null,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

class AddBookViewModel(
    private val repository: BookRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState: StateFlow<AddBookUiState> = _uiState

    fun searchByIsbn(isbn: String) {
        if (isbn.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isbnSearchLoading = true, error = null)
            val res = repository.fetchBookByIsbn(isbn)
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(foundBook = res.getOrNull(), isbnSearchLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    isbnSearchLoading = false,
                    error = res.exceptionOrNull()?.message ?: "Erro ao buscar ISBN"
                )
            }
        }
    }

    fun saveBook(book: Book) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, savedSuccessfully = false)
            try {
                val uid = firebaseAuth.currentUser?.uid ?: ""
                if (uid.isBlank()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Usuário não autenticado")
                    return@launch
                }
                book.ownerId = uid
                val result = repository.addBook(book)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isLoading = false, savedSuccessfully = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(savedSuccessfully = false)
    }
}
