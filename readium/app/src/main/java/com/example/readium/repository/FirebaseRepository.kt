package com.example.readium.repository

import com.example.readium.firebase.FirebaseConfig
import com.example.readium.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    
    private val auth = FirebaseConfig.auth
    private val firestore = FirebaseConfig.firestore
    private val storage = FirebaseStorage.getInstance()
    
    //autenticação
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    //aceita foto de perfil e faz upload pro storage
    suspend fun createUserWithEmail(email: String, password: String, name: String, biography: String = "", profilePhotoUri: String? = null): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                //salva dados no firebase
                var profileUrl: String? = null
                if (!profilePhotoUri.isNullOrBlank()) {
                    //tenta fazer upload da foto
                    val uploadResult = uploadProfilePhoto(firebaseUser.uid, profilePhotoUri)
                    profileUrl = uploadResult.getOrNull()
                }

                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    profilePhotoUrl = profileUrl,
                    biography = biography
                )
                saveUserToFirestore(user)
            }
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadProfilePhoto(userId: String, photoUriString: String): Result<String?> {
        return try {
            val uri = Uri.parse(photoUriString)
            val ref = storage.reference.child("profile_photos/$userId.jpg")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            println("DEBUG: Iniciando signInWithGoogle com idToken")
            
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            println("DEBUG: Credential criado")
            
            val result = auth.signInWithCredential(credential).await()
            println("DEBUG: Autenticação com Google bem-sucedida: ${result.user?.email}")
            
            result.user?.let { firebaseUser ->
                //verificar se é o primeiro login do usuário
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                if (!userDoc.exists()) {
                    println("DEBUG: Primeiro login, criando perfil do usuário")
                    //criar perfil se ainda não existir
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        profilePhotoUrl = firebaseUser.photoUrl?.toString()
                    )
                    saveUserToFirestore(user)
                } else {
                    println("DEBUG: Usuário já existe no Firestore")
                }
            }
            
            Result.success(result.user)
        } catch (e: Exception) {
            println("DEBUG: Erro no signInWithGoogle: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun getUserProfile(userId: String): Result<User?> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}