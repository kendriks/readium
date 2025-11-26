package com.example.readium.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.firebase.FirebaseConfig
import com.example.readium.ui.theme.*
import com.example.readium.viewmodel.AuthViewModel
import androidx.compose.ui.res.painterResource
import com.example.readium.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadiumHomeScreen(
    onLogout: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val user by authViewModel.user.collectAsState()
    val sampleItems = remember { List(5) { index ->
        "Post de exemplo #${index + 1}: Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    } }

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
                                .border(2.dp, ReadiumWhite, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
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
                            IconButton(onClick = { /*ainda não implementado*/ }) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
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
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "sair",
                                tint = ReadiumOnPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            //barra inferior
            Surface(
                color = ReadiumWhite,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //home
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /*ainda não implementado*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "home",
                            tint = ReadiumGrayMedium,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "home", fontSize = 12.sp, color = ReadiumGrayMedium)
                    }

                    //criar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /*ainda não implementado*/ }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ReadiumWhite,
                            border = BorderStroke(1.dp, ReadiumGrayMedium),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "criar",
                                    tint = ReadiumGrayMedium,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "criar", fontSize = 12.sp, color = ReadiumGrayMedium)
                    }

                    //perfil
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /*ainda não implementado*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "perfil",
                            tint = ReadiumGrayMedium,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "perfil", fontSize = 12.sp, color = ReadiumGrayMedium)
                    }
                }
            }
        }
    ) { paddingValues ->
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            items(sampleItems) { text ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                Text(text = userProfile?.name ?: "name", fontWeight = FontWeight.Bold)
                                Text(text = "@${user?.displayName ?: "user"}", fontSize = 12.sp, color = ReadiumGrayMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = text, maxLines = 3, overflow = TextOverflow.Ellipsis)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Text(text = "❤️ 10", color = ReadiumGrayMedium, modifier = Modifier.padding(end = 16.dp))
                            Text(text = "💬 2 comentários", color = ReadiumGrayMedium)
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

@Preview(showBackground = true)
@Composable
fun ReadiumHomeScreenPreview() {
    ReadiumTheme {
        ReadiumHomeScreen()
    }
}