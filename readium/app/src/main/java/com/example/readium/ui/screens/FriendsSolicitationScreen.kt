package com.example.readium.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readium.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.readium.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FriendsSolicitationScreen() {
    var confirmationMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    val friends = remember { mutableStateListOf<User>() }
    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(User::class.java) }
                    .filter { it.id != currentUserId }
                users.addAll(list)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReadiumBackground)
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ReadiumPrimary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(users) { user ->
                    if (friends.none { it.id == user.id }) {
                        UserCard(
                            user = user,
                            onAddFriend = {
                                addFriend(user, friends, db, currentUserId) {
                                    confirmationMessage = it
                                }
                            },
                            onRejectFriend = {
                                confirmationMessage = "Você rejeitou ${user.name}."
                            }
                        )
                    }
                }
            }
        }

        confirmationMessage?.let {
            showConfirmationDialog(
                message = it,
                onDismiss = { confirmationMessage = null }
            )
        }
    }
}

fun addFriend(user: User, friends: MutableList<User>, db: FirebaseFirestore, currentUserId: String, onResult: (String) -> Unit) {
    val friendRef = db.collection("users").document(currentUserId).collection("amigos").document(user.id ?: "")

    friendRef.set(
        mapOf(
            "id" to (user.id ?: ""),
            "nome" to (user.name ?: "Usuário Desconhecido"),
            "email" to (user.email ?: "")
        )
    )
        .addOnSuccessListener {
            Log.d("FriendsSolicitation", "Amigo adicionado: ${user.name}")
            friends.add(user)
            onResult("${user.name} foi adicionado como amigo!")
        }
        .addOnFailureListener { e ->
            Log.e("FriendsSolicitation", "Erro ao adicionar amigo", e)
            onResult("Erro ao adicionar ${user.name}. Tente novamente.")
        }
}


@Composable
fun UserCard(
    user: User,
    onAddFriend: () -> Unit,
    onRejectFriend: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReadiumBlack,
                    fontWeight = FontWeight.Bold
                )

                user.biography?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ReadiumBlack.copy(alpha = 0.6f)
                    )
                }
            }

            Row {
                Button(
                    onClick = onAddFriend,
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                ) {
                    Text("Adicionar", color = ReadiumWhite)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onRejectFriend,
                    border = BorderStroke(1.dp, ReadiumGrayMedium),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReadiumGrayMedium
                    )
                ) {
                    Text("Rejeitar")
                }

            }
        }
    }
}


@Composable
fun showConfirmationDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Confirmação",
                color = ReadiumPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                message,
                color = ReadiumBlack
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = ReadiumPrimary)
            ) {
                Text("OK")
            }
        },
        containerColor = ReadiumWhite
    )
}
