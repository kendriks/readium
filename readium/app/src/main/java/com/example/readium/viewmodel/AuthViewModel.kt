package com.example.readium.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.readium.repository.FirebaseRepository
import com.example.readium.data.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = FirebaseRepository(application.applicationContext)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()
    
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState.asStateFlow()
    
    init {
        checkAuthState()
    }

    fun validatePassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    fun updatePassword(newPassword: String) {
        val user = _user.value
        if (user != null) {
            _profileUpdateState.value = ProfileUpdateState.Loading

            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _profileUpdateState.value = ProfileUpdateState.Success
                    } else {
                        _profileUpdateState.value = ProfileUpdateState.Error(
                            task.exception?.message ?: "Erro ao atualizar senha. Tente fazer login novamente."
                        )
                    }
                }
        } else {
            _profileUpdateState.value = ProfileUpdateState.Error("Usuário não autenticado")
        }
    }

    private fun checkAuthState() {
        val currentUser = repository.getCurrentUser()
        _user.value = currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated
            loadUserProfile(currentUser.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
            
            result.fold(
                onSuccess = { user ->
                    _user.value = user
                    user?.let { loadUserProfile(it.uid) }
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro desconhecido")
                }
            )
        }
    }
    
    fun signUp(name: String, email: String, password: String, biography: String = "", profilePhotoUri: String? = null) {

        if (name.isBlank()) {
            _authState.value = AuthState.Error("Nome de usuário é obrigatório")
            return
        }
        
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email é obrigatório")
            return
        }

        // --- Validação no momento do Cadastro ---
        if (!validatePassword(password)) {
            _authState.value = AuthState.Error("A senha deve ter no mínimo 8 caracteres, contendo letras e números.")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.createUserWithEmail(email, password, name, biography, profilePhotoUri)
            
            result.fold(
                onSuccess = { user ->
                    _user.value = user
                    user?.let { loadUserProfile(it.uid) }
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    exception.printStackTrace()
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao criar conta")
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithGoogle(idToken)
            
            result.fold(
                onSuccess = { user ->
                    _user.value = user
                    user?.let { loadUserProfile(it.uid) }
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao fazer login com Google")
                }
            )
        }
    }

    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserProfile(userId)
            result.fold(
                onSuccess = { profile ->
                    _userProfile.value = profile
                },
                onFailure = { /* Handle error silently */ }
            )
        }
    }
    
    fun updateUserProfile(name: String, biography: String, profilePhotoUri: String? = null) {
        viewModelScope.launch {
            _user.value?.uid?.let { userId ->
                _profileUpdateState.value = ProfileUpdateState.Loading
                val result = repository.updateUserProfile(userId, name, biography, profilePhotoUri)
                result.fold(
                    onSuccess = {
                        loadUserProfile(userId)
                        _profileUpdateState.value = ProfileUpdateState.Success
                    },
                    onFailure = { exception ->
                        _profileUpdateState.value = ProfileUpdateState.Error(exception.message ?: "Falha ao atualizar perfil")
                    }
                )
            } ?: run {
                _profileUpdateState.value = ProfileUpdateState.Error("Usuário não autenticado")
            }
        }
    }

    fun resetProfileUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }
    
    fun clearAuthError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    fun signOut() {
        repository.signOut()
        _user.value = null
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}