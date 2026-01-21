package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.data.User
import com.example.readium.repository.FriendsRepository
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val repository = FriendsRepository()

    var friends = mutableStateListOf<User>()
        private set

    val friendsCount: Int
        get() = friends.size
    var isLoading by mutableStateOf(false)
        private set

    fun loadFriends(userId: String) {
        isLoading = true
        viewModelScope.launch {
            val loadedFriends = repository.getFriends(userId)

            friends.clear()
            friends.addAll(loadedFriends)

            isLoading = false
        }
    }

    fun removeFriend(userId: String, friend: User) {
        val friendId = friend.id ?: return

        isLoading = true
        viewModelScope.launch {
            val success = repository.removeFriend(userId, friendId)

            if (success) {
                friends.removeAll { it.id == friendId }
            }

            isLoading = false
        }
    }
}