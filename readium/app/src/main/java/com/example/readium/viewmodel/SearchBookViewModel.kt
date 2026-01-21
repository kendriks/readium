package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.data.mapper.toBook
import com.example.readium.data.model.Book
import com.example.readium.data.remote.GoogleBooksApi
import com.example.readium.data.remote.needsFallback
import kotlinx.coroutines.launch

data class SearchBookUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Book> = emptyList(),
    val error: String? = null
)

class SearchBookViewModel : ViewModel() {

    private val api = GoogleBooksApi.create()

    var uiState by mutableStateOf(SearchBookUiState())
        private set

    fun onQueryChange(value: String) {
        uiState = uiState.copy(query = value)
    }

    fun search() {
        if (uiState.query.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val response = api.searchByIsbn(uiState.query)

                val books = response.items?.mapNotNull { item ->

                    if (item.needsFallback()) {

                        val title = item.volumeInfo.title
                        if (title.isNullOrBlank()) return@mapNotNull null

                        val fallbackResponse = api.searchByTitle(title)

                        val bestMatch = fallbackResponse.items
                            ?.firstOrNull { !it.volumeInfo.imageLinks?.thumbnail.isNullOrBlank() }
                            ?: fallbackResponse.items?.firstOrNull()

                        bestMatch?.toBook()

                    } else {
                        item.toBook()
                    }

                } ?: emptyList()

                uiState = uiState.copy(
                    isLoading = false,
                    results = books
                )

            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Erro ao buscar livros"
                )
            }
        }
    }
}
