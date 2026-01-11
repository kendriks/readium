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
        println("DEBUG FirebaseRepository: Storage bucket: ${storage.reference.bucket}")
    }
    
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
            println("DEBUG createUserWithEmail: Criando usuário $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            result.user?.let { firebaseUser ->
                println("DEBUG createUserWithEmail: Usuário criado com uid=${firebaseUser.uid}")
                var profileUrl: String? = null
                
                // Se houver foto, faz upload e aguarda completamente
                if (!profilePhotoUri.isNullOrBlank()) {
                    println("DEBUG createUserWithEmail: Iniciando upload de foto")
                    val uploadResult = uploadProfilePhoto(firebaseUser.uid, profilePhotoUri)
                    uploadResult.fold(
                        onSuccess = { url -> 
                            // cache-buster para evitar imagem antiga em cache do cliente
                            val cacheBusted = "$url?ts=${System.currentTimeMillis()}"
                            println("DEBUG createUserWithEmail: Upload bem-sucedido, URL cache-busted=$cacheBusted")
                            profileUrl = cacheBusted 
                        },
                        onFailure = { uploadError ->
                            println("DEBUG createUserWithEmail: Falha no upload: ${uploadError.message}")
    
                            val userWithoutPhoto = User(
                                id = firebaseUser.uid,
                                name = name,
                                email = email,
                                profilePhotoUrl = null,
                                biography = biography
                            )
                            try {
                                saveUserToFirestore(userWithoutPhoto)
                                println("DEBUG createUserWithEmail: Usuário salvo sem foto")
                            } catch (e: Exception) {
                                println("DEBUG createUserWithEmail: Erro ao salvar usuário: ${e.message}")
                            }
                            return Result.failure(Exception("Conta criada, mas falha ao fazer upload da foto: ${uploadError.message}"))
                        }
                    )
                }

                //cria o objeto user com a url da foto
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    profilePhotoUrl = profileUrl,
                    biography = biography
                )
                
                //aguarda o salvamento no Firestore
                try {
                    saveUserToFirestore(user)
                    println("DEBUG createUserWithEmail: Usuário salvo com sucesso no Firestore")
                } catch (e: Exception) {
                    println("DEBUG createUserWithEmail: Erro ao salvar usuário no Firestore: ${e.message}")
                    return Result.failure(e)
                }
            }
            
            println("DEBUG createUserWithEmail: Processo completo, retornando sucesso")
            Result.success(result.user)
        } catch (e: Exception) {
            println("DEBUG createUserWithEmail: Erro geral: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private suspend fun uploadProfilePhoto(userId: String, photoUriString: String): Result<String?> {
        return try {
            // Validação da URI
            if (photoUriString.isBlank()) {
                println("DEBUG uploadProfilePhoto: URI está vazia")
                return Result.failure(Exception("URI da foto está vazia"))
            }
            
            val uri = Uri.parse(photoUriString)
            println("DEBUG uploadProfilePhoto: URI original: $photoUriString")
            println("DEBUG uploadProfilePhoto: URI parseada: $uri")
            println("DEBUG uploadProfilePhoto: URI scheme: ${uri.scheme}")
            println("DEBUG uploadProfilePhoto: URI path: ${uri.path}")
            
            // Verifica se a URI é válida
            if (uri.scheme == null) {
                println("DEBUG uploadProfilePhoto: URI sem scheme")
                return Result.failure(Exception("URI inválida (sem scheme): $photoUriString"))
            }
            
            //tenta abrir o InputStream
            println("DEBUG uploadProfilePhoto: Tentando abrir InputStream para userId=$userId")
            val inputStream = try {
                context.contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                println("DEBUG uploadProfilePhoto: ERRO ao abrir InputStream: ${e.message}")
                e.printStackTrace()
                return Result.failure(Exception("Não foi possível acessar a foto: ${e.message}"))
            }
            
            if (inputStream == null) {
                println("DEBUG uploadProfilePhoto: InputStream retornou null")
                return Result.failure(Exception("Não foi possível abrir a foto selecionada"))
            }
            
            println("DEBUG uploadProfilePhoto: InputStream aberto com sucesso")
            
            val ref = storage.reference.child("profile_photos/${userId}.jpg")
            println("DEBUG uploadProfilePhoto: Referência Firebase Storage criada: ${ref.path}")
            println("DEBUG uploadProfilePhoto: Storage bucket: ${storage.reference.bucket}")
            println("DEBUG uploadProfilePhoto: Storage URL completa: gs://${storage.reference.bucket}/${ref.path}")
            
            //verifica autenticação antes do upload
            val currentUser = auth.currentUser
            println("DEBUG uploadProfilePhoto: Usuário autenticado: ${currentUser?.uid}")
            println("DEBUG uploadProfilePhoto: Email do usuário: ${currentUser?.email}")
            
            if (currentUser == null) {
                return Result.failure(Exception("Usuário não está autenticado no momento do upload"))
            }
            
            if (currentUser.uid != userId) {
                println("DEBUG uploadProfilePhoto: AVISO - userId não corresponde ao usuário autenticado!")
            }
            
            //upload usando InputStream
            inputStream.use { stream ->
                println("DEBUG uploadProfilePhoto: Iniciando upload para Firebase Storage...")
                val uploadTask = ref.putStream(stream)
                uploadTask.await()
                println("DEBUG uploadProfilePhoto: Upload concluído com sucesso!")
            }
            
            println("DEBUG uploadProfilePhoto: Obtendo URL de download...")
            val downloadUrl = ref.downloadUrl.await().toString()
            println("DEBUG uploadProfilePhoto: URL obtida: $downloadUrl")
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            println("DEBUG uploadProfilePhoto: ERRO GERAL durante upload: ${e.message}")
            println("DEBUG uploadProfilePhoto: Tipo de erro: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Result.failure(Exception("Erro ao fazer upload da foto: ${e.message}"))
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
    
    suspend fun updateUserProfile(userId: String, name: String, biography: String, profilePhotoUri: String? = null): Result<Boolean> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "name" to name,
                "biography" to biography
            )
            
            //se uma nova foto local foi fornecida, faz o upload
            if (!profilePhotoUri.isNullOrBlank()) {
                val uploadResult = uploadProfilePhoto(userId, profilePhotoUri)
                uploadResult.getOrNull()?.let { photoUrl ->
                    //adiciona cache-buster para forçar atualização nas telas Profile/Home
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