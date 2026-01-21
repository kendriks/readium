package com.example.readium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readium.repository.PostRepository

class HomeViewModelFactory(
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                repository = PostRepository(),
                authViewModel = authViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
