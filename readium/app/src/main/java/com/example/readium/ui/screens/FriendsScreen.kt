package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.ui.theme.*
import com.example.readium.data.User
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FriendsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var amigos by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var visualizandoAmigos by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                amigos = result.documents.mapNotNull { it.toObject(User::class.java) }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                selectedItem = 2,
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ReadiumPrimary)
                }
            } else {
                if (visualizandoAmigos) {
                    FriendsList(friends = amigos)
                } else {
                    FriendsSolicitationScreen()
                }
            }
        }
    }
}

@Composable
fun FriendsList(friends: List<User>) {
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
                FriendCard(user = friend)
            }
        }
    }
}

@Composable
fun FriendCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ReadiumGrayMedium)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReadiumBlack,
                    fontWeight = FontWeight.Bold
                )

                user.biography.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ReadiumBlack.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadiumBottomBar(
    selectedItem: Int,
    onHomeClick: () -> Unit,
    onCreateClick: () -> Unit,
    onProfileClick: () -> Unit
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
            isSelected = selectedItem == 0,
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            isSelected = selectedItem == 1,
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            isSelected = selectedItem == 2,
            onClick = onProfileClick
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
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
            tint = if (isSelected) ReadiumPrimary else ReadiumGrayMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) ReadiumPrimary else ReadiumGrayMedium
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ReadiumWhite
                    )
                }

                Text(
                    text = "@name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /*ainda não implementado*/ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações",
                        tint = ReadiumWhite
                    )
                }
                IconButton(onClick = { /*ainda não implementado*/ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar",
                        tint = ReadiumWhite
                    )
                }
            }
        }
    }
}