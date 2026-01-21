package com.example.readium.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.ui.theme.*
import com.example.readium.data.User
import com.example.readium.viewmodel.FriendsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FriendsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    friendsViewModel: FriendsViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid
    var visualizandoAmigos by remember { mutableStateOf(true) }
    var friendToRemove by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        userId?.let {
            friendsViewModel.loadFriends(it)
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = onNavigateToHome,
                onCreateClick = {},
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // ---------- BOTÕES SUPERIORES ----------
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { visualizandoAmigos = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (visualizandoAmigos) ReadiumPrimary else ReadiumGrayMedium.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Meus Amigos", color = ReadiumWhite)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { visualizandoAmigos = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (!visualizandoAmigos) ReadiumPrimary else ReadiumGrayMedium.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Adicionar Amigos", color = ReadiumWhite)
                }
            }

            // ---------- CONTEÚDO ----------
            when {
                friendsViewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ReadiumPrimary)
                    }
                }

                visualizandoAmigos -> {
                    FriendsList(
                        friends = friendsViewModel.friends,
                        onRemoveFriend = { friend ->
                            friendToRemove = friend // 👈 só abre o diálogo
                        })
                }
                else -> {
                    FriendsSolicitationScreen(
                        friendsViewModel = friendsViewModel
                    )
                }
            }
        }
    }
    friendToRemove?.let { friend ->

        AlertDialog(
            onDismissRequest = { },

            title = {
                Text("Remover amigo")
            },

            text = {
                Text("Tem certeza que deseja remover ${friend.name}?")
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let {
                            friendsViewModel.removeFriend(it, friend)
                        }
                    }
                ) {
                    Text("Remover", color = ReadiumError)
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}

@Composable
fun FriendsList(friends: List<User>, onRemoveFriend: (User) -> Unit) {
    if (friends.isEmpty()) {
        Text(
            "Você ainda não adicionou nenhum amigo.",
            style = MaterialTheme.typography.bodyMedium,
            color = ReadiumBlack.copy(alpha = 0.7f)
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(friends) { friend ->
                FriendCard(user = friend, onRemoveFriend = { onRemoveFriend(friend)})
            }
        }
    }
}

@Composable
fun FriendCard(
    user: User,
    onRemoveFriend: () -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReadiumBlack,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    user.biography,
                    style = MaterialTheme.typography.bodySmall,
                    color = ReadiumBlack.copy(alpha = 0.6f)
                )
            }

            OutlinedButton(
                onClick = onRemoveFriend,
                border = BorderStroke(1.dp, ReadiumGrayMedium),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ReadiumGrayMedium
                )
            ) {
                Text("Remover")
            }
        }
    }
}

@Composable
private fun ReadiumBottomBar(
    onHomeClick: () -> Unit,
    onCreateClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ReadiumWhite)
            .border(1.dp, ReadiumGrayMedium.copy(alpha = 0.2f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(
            icon = Icons.Outlined.Home,
            label = "home",
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            onClick = onProfileClick
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ReadiumGrayMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = ReadiumGrayMedium
        )
    }
}

@Composable
private fun ProfileTopBar(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp, bottom = 0.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            color = ReadiumPrimary,
            shape = RoundedCornerShape(0.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 0.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ReadiumWhite
                    )
                }

                Text(
                    text = "Amigos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}