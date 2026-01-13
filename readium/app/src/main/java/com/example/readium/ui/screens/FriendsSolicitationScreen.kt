package com.example.readium.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.data.model.Friends
import com.example.readium.viewmodel.FriendsViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

@Composable
fun FriendsSolicitationScreen(
    friendsViewModel: FriendsViewModel = viewModel()
) {
    var confirmationMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    val friends = remember { mutableStateListOf<User>() }
    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        isLoading = true
        db.collection("friends")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { doc ->
                val friends = doc.toObject(Friends::class.java)
                val friendIds = friends?.idsFriends ?: emptyList()

                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->

                        val list = result.documents
                            .mapNotNull { it.toObject(User::class.java) }
                            .filter {
                                it.id != currentUserId &&
                                        !friendIds.contains(it.id)
                            }
                        users.clear()
                        users.addAll(list)
                        isLoading = false
                    }
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
                                addFriend(user, db, currentUserId) { message ->
                                    confirmationMessage = message
                                    friendsViewModel.loadFriends()
                                }
                            }
                        )
                    }
                }
            }
        }

        confirmationMessage?.let {
            ShowConfirmationDialog(
                message = it,
                onDismiss = { confirmationMessage = null }
            )
        }
    }
}

fun addFriend(
    user: User,
    db: FirebaseFirestore,
    currentUserId: String,
    onResult: (String) -> Unit
) {
    val friendId = user.id

    val currentUserRef = db.collection("friends").document(currentUserId)
    val friendRef = db.collection("friends").document(friendId)

    db.runBatch { batch ->
        batch.set(
            currentUserRef,
            mapOf("ids_friends" to FieldValue.arrayUnion(friendId)),
            SetOptions.merge()
        )
        batch.set(
            friendRef,
            mapOf("ids_friends" to FieldValue.arrayUnion(currentUserId)),
            SetOptions.merge()
        )
    }.addOnSuccessListener {
        onResult("${user.name} foi adicionado como amigo!")
    }.addOnFailureListener {
        onResult("Erro ao adicionar amigo")
    }
}

@Composable
fun UserCard(
    user: User,
    onAddFriend: () -> Unit
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

                Text(
                    text = user.biography,
                    style = MaterialTheme.typography.bodySmall,
                    color = ReadiumBlack.copy(alpha = 0.6f)
                )
            }

            Row {
                Button(
                    onClick = onAddFriend,
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                ) {
                    Text("Adicionar", color = ReadiumWhite)
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


@Composable
fun ShowConfirmationDialog(
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