package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readium.data.model.ClubeLeitura
import com.example.readium.repository.BookClubRepository
import kotlinx.coroutines.launch

class BookClubViewModel(private val repository: BookClubRepository) : ViewModel() {

    var userClubs by mutableStateOf<List<ClubeLeitura>>(emptyList())
        private set

    var publicClubs by mutableStateOf<List<ClubeLeitura>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set
    var draftName by mutableStateOf("")
    var draftDescription by mutableStateOf("")
    var draftIsPrivate by mutableStateOf(false)
    var draftBookOfMonth by mutableStateOf("")
    fun loadUserClubs(userId: String) {
        viewModelScope.launch {
            isLoading = true
            userClubs = repository.getUserClubs(userId)
            isLoading = false
        }
    }

    // Busca clubes públicos para a tela de pesquisa
    fun searchPublicClubs() {
        viewModelScope.launch {
            isLoading = true
            publicClubs = repository.getPublicClubs()
            isLoading = false
        }
    }

    fun saveDraftClub(
        ownerId: String,
        ownerName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val newClub = ClubeLeitura(
                name = draftName,
                description = draftDescription,
                isPrivate = draftIsPrivate,
                currentBookTitle = draftBookOfMonth,
                ownerId = ownerId,
                ownerName = ownerName,
                members = listOf(ownerId) // O criador já começa como membro
            )
            val success = repository.createClub(newClub)
            isLoading = false

            if (success) {
                clearDraft() // Limpa o formulário
                // Atualiza a lista local imediatamente para o usuário ver o novo clube
                loadUserClubs(ownerId)
                onSuccess()
            }
        }
    }

    // Limpa os dados do rascunho (útil ao cancelar ou finalizar)
    fun clearDraft() {
        draftName = ""
        draftDescription = ""
        draftIsPrivate = false
        draftBookOfMonth = ""
    }

    // Entrar em um clube
    fun joinClub(club: ClubeLeitura, userId: String) {
        viewModelScope.launch {
            val success = repository.joinClub(club.id, userId, club.isPrivate)
            if (success && !club.isPrivate) {
                // Se for público e entrou com sucesso, recarrega a lista
                loadUserClubs(userId)
            }
        }
    }
}

class BookClubViewModelFactory(private val repository: BookClubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookClubViewModel::class.java)) {
            return BookClubViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}