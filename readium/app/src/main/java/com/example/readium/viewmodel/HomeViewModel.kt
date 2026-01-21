package com.example.readium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.data.model.Post
import com.example.readium.repository.FriendsRepository
import com.example.readium.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val repository: PostRepository = PostRepository(),
    private val friendsRepository: FriendsRepository = FriendsRepository(),
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    suspend fun loadFeed() {
        val userId = authViewModel.user.value?.uid ?: return
        val friendsIds =friendsRepository.getFriendsIds(userId)

        viewModelScope.launch {
            _posts.value = repository.fetchFeedPosts(
                userId = userId,
                friendsIds = friendsIds
            )
        }
    }
}
