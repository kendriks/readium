package com.example.readium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.readium.R
import com.example.readium.data.model.Post
import com.example.readium.ui.theme.*
import com.example.readium.viewmodel.AuthViewModel
import com.example.readium.viewmodel.HomeViewModel
import com.example.readium.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadiumHomeScreen(
    onLogout: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToBookClubs: () -> Unit = {},
    onNavigateToSearchTrade: () -> Unit = {},
    onNavigateToAddPost: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val user by authViewModel.user.collectAsState()

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(authViewModel)
    )
    val posts by homeViewModel.posts.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadFeed()
    }


    Scaffold(
        containerColor = ReadiumBackground,
        topBar = {
            //barra superior
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
                            .padding(start = 12.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //foto perfil
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ReadiumWhite)
                        ) {
                            if (!userProfile?.profilePhotoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = userProfile?.profilePhotoUrl,
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Oi, ${userProfile?.name ?: user?.displayName ?: "name"} !",
                            color = ReadiumOnPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )

                        //clube do livro
                        Surface(
                            modifier = Modifier
                                .size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = ReadiumPrimary
                        ) {
                            IconButton(onClick = { onNavigateToBookClubs() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "livro",
                                    tint = ReadiumOnPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        //sair
                        IconButton(
                            onClick = { onLogout() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "sair",
                                tint = ReadiumOnPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = { },
                onCreateClick = onNavigateToAddPost,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            item {
                Button(
                    onClick = { onNavigateToSearchTrade() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Buscar Trocas")
                }
            }

            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = ReadiumWhite)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ReadiumSecondary)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = post.ownerName,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "@${post.ownerName}",
                                    fontSize = 12.sp,
                                    color = ReadiumGrayMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = post.content,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Text(
                                text = "❤️ ${post.likes}",
                                color = ReadiumGrayMedium,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Text(
                                text = "💬 ${post.commentsCount} comentários",
                                color = ReadiumGrayMedium
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ReadiumBottomBar(
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
            isSelected = true,
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            isSelected = false,
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            isSelected = false,
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
            tint = if (isSelected) ReadiumPrimary else ReadiumGrayMedium ,
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