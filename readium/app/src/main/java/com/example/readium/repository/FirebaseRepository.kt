package com.example.readium.repository

import com.example.readium.firebase.FirebaseConfig
import com.example.readium.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import android.content.Context
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val context: Context) {

    private val auth = FirebaseConfig.auth
    private val firestore = FirebaseConfig.firestore
    private val storage = FirebaseStorage.getInstance()

    init {
        println("DEBUG FirebaseRepository: Inicializado")
    }

    // Autenticação com Email/Senha
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Criar conta com Email/Senha + Upload de Foto
    suspend fun createUserWithEmail(email: String, password: String, name: String, biography: String = "", profilePhotoUri: String? = null): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let { firebaseUser ->
                var profileUrl: String? = null

                // Se houver foto, faz upload
                if (!profilePhotoUri.isNullOrBlank()) {
                    val uploadResult = uploadProfilePhoto(firebaseUser.uid, profilePhotoUri)
                    uploadResult.fold(
                        onSuccess = { url ->
                            // cache-buster
                            profileUrl = "$url?ts=${System.currentTimeMillis()}"
                        },
                        onFailure = {
                            // Log de erro, mas prossegue com a criação do usuário sem foto
                        }
                    )
                }

                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    profilePhotoUrl = profileUrl,
                    biography = biography
                )

                try {
                    saveUserToFirestore(user)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
            }

            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Upload de Foto de Perfil
    private suspend fun uploadProfilePhoto(userId: String, photoUriString: String): Result<String?> {
        return try {
            if (photoUriString.isBlank()) return Result.failure(Exception("URI da foto está vazia"))

            val uri = Uri.parse(photoUriString)
            val inputStream = try {
                context.contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                return Result.failure(Exception("Não foi possível acessar a foto: ${e.message}"))
            }

            if (inputStream == null) return Result.failure(Exception("InputStream retornou null"))

            val ref = storage.reference.child("profile_photos/${userId}.jpg")

            // Upload
            inputStream.use { stream ->
                ref.putStream(stream).await()
            }

            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao fazer upload da foto: ${e.message}"))
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                // Verificar se é o primeiro login
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                if (!userDoc.exists()) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        profilePhotoUrl = firebaseUser.photoUrl?.toString()
                    )
                    saveUserToFirestore(user)
                }
            }
            
            Result.success(result.user)
        } catch (e: Exception) {
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

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        biography: String,
        profilePhotoUri: String? = null,
        city: String = "",  // Novo parâmetro
        state: String = ""  // Novo parâmetro
    ): Result<Boolean> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "name" to name,
                "biography" to biography,
                "city" to city,   // Salva no Firestore
                "state" to state
            )

            // Se foto nova, faz upload
            if (!profilePhotoUri.isNullOrBlank()) {
                val uploadResult = uploadProfilePhoto(userId, profilePhotoUri)
                uploadResult.getOrNull()?.let { photoUrl ->
                    updates["profilePhotoUrl"] = "$photoUrl?ts=${System.currentTimeMillis()}"
                }
            }

            firestore.collection("users").document(userId).update(updates).await()
            Result.success(true)
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