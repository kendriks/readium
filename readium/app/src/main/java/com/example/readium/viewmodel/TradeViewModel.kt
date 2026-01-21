package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readium.data.model.Book
import com.example.readium.data.model.TradeProposal
import com.example.readium.repository.TradeRepository
import kotlinx.coroutines.launch

class TradeViewModel(private val repository: TradeRepository) : ViewModel() {

    var searchResults by mutableStateOf<List<Book>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var proposalMessage by mutableStateOf<String?>(null)
        private set

    fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            searchResults = repository.searchBooksForTrade(query)
            isLoading = false
        }
    }

    fun sendProposal(
        senderId: String,
        senderName: String,
        book: Book
    ) {
        viewModelScope.launch {
            isLoading = true
            val proposal = TradeProposal(
                senderId = senderId,
                senderName = senderName,
                receiverId = book.ownerId,
                receiverName = book.ownerDisplayName ?: "Desconhecido",
                desiredBookId = book.id,
                desiredBookTitle = book.title
            )

            val success = repository.sendTradeProposal(proposal)

            isLoading = false
            proposalMessage = if (success) {
                "Proposta enviada para ${book.ownerDisplayName}!"
            } else {
                "Erro ao enviar proposta."
            }
        }
    }

    fun clearMessage() {
        proposalMessage = null
    }
}

class TradeViewModelFactory(private val repository: TradeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TradeViewModel::class.java)) {
            return TradeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}