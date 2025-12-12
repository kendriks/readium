package com.example.readium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.ui.theme.*

@Composable
fun ProfileScreen(
    userName: String = "name",
    userBio: String = "book lover <3",
    postsCount: Int = 13,
    booksCount: Int = 19,
    friendsCount: Int = 89,
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            ProfileTopBar(
                onNavigateBack = onNavigateBack,
                onNavigateToSettings = onNavigateToEditProfile
            )
        },
        bottomBar = {
            ReadiumBottomBar(
                selectedItem = 2, //perfil selecionado
                onHomeClick = onNavigateToHome,
                onCreateClick = { /*ainda não implementado*/ },
                onProfileClick = { }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
        ) {
            //header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //avatar
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(ReadiumGrayMedium)
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        //postagens, livros e amigos
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(count = postsCount, label = "postagens")
                            StatItem(count = booksCount, label = "livros")
                            StatItem(count = friendsCount, label = "amigos")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //nome e bio
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack
                    )
                    Text(
                        text = userBio,
                        fontSize = 14.sp,
                        color = ReadiumBlack.copy(alpha = 0.7f)
                    )
                }
            }

            //aba de postagem e lista
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ReadiumBackground,
                    contentColor = ReadiumPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "minhas postagens",
                                color = if (selectedTab == 0) ReadiumPrimary else ReadiumGrayMedium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "minhas listas",
                                color = if (selectedTab == 1) ReadiumPrimary else ReadiumGrayMedium
                            )
                        }
                    )
                }
            }

            //conteúdo das abas
            when (selectedTab) {
                0 -> {
                    //aba de postagens
                    items(3) { index ->
                        PostCard(
                            userName = userName,
                            timeAgo = "há 14 minutos",
                            postText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eget lorem nec ante iaculis interdum eget in purus.",
                            bookTitle = if (index == 2) "O admirável histórico de leitura" else null,
                            authorName = if (index == 2) "kathy" else null,
                            bookAuthor = if (index == 2) "Muará Awad" else null,
                            bookDescription = if (index == 2) "Wohov é uma história de solidão e potência, amizade e desejo, maternidade e feminino cujas tercerias falam do imaginário." else null,
                            likes = 10,
                            comments = 2
                        )
                    }
                }
                1 -> {
                    //aba de listas
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            //nova lista
                            OutlinedButton(
                                onClick = { /*ainda não implementado*/ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ReadiumGrayMedium
                                )
                            ) {
                                Text("nova lista +")
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            //seção Livros favoritos
                            BookListSection(
                                title = "Livros favoritos",
                                itemCount = 10,
                                bookCount = 5
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            //otra seção
                            BookListSection(
                                title = "Para ler no verão <3",
                                itemCount = 10,
                                bookCount = 5
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
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

                IconButton(onClick = onNavigateToSettings) {
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

@Composable
private fun StatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ReadiumPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = ReadiumBlack.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun PostCard(
    userName: String,
    timeAgo: String,
    postText: String,
    bookTitle: String? = null,
    authorName: String? = null,
    bookAuthor: String? = null,
    bookDescription: String? = null,
    likes: Int,
    comments: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //header do post
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ReadiumGrayMedium)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReadiumPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "@$userName",
                            fontSize = 12.sp,
                            color = ReadiumGrayMedium
                        )
                    }
                    Text(
                        text = timeAgo,
                        fontSize = 11.sp,
                        color = ReadiumGrayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //texto do post
            Text(
                text = postText,
                fontSize = 14.sp,
                color = ReadiumBlack,
                lineHeight = 20.sp
            )

            //card do livro
            if (bookTitle != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = ReadiumMark.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ReadiumMark)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bookTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReadiumPrimary
                            )
                            if (authorName != null) {
                                Text(
                                    text = authorName,
                                    fontSize = 11.sp,
                                    color = ReadiumBlack
                                )
                            }
                            if (bookAuthor != null) {
                                Text(
                                    text = bookAuthor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ReadiumBlack
                                )
                            }
                            if (bookDescription != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = bookDescription,
                                    fontSize = 10.sp,
                                    color = ReadiumBlack.copy(alpha = 0.7f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //likes e comentários
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Curtir",
                        tint = ReadiumGrayMedium,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$likes curtidas",
                        fontSize = 12.sp,
                        color = ReadiumGrayMedium
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comentários",
                        tint = ReadiumGrayMedium,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$comments comentários",
                        fontSize = 12.sp,
                        color = ReadiumGrayMedium
                    )
                }

                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Salvar",
                    tint = ReadiumPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BookListSection(
    title: String,
    itemCount: Int,
    bookCount: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumBlack
                )
                Text(
                    text = "$itemCount itens",
                    fontSize = 12.sp,
                    color = ReadiumGrayMedium
                )
            }

            TextButton(onClick = { /*ainda não implementado*/ }) {
                Text(
                    text = "ver mais >",
                    fontSize = 12.sp,
                    color = ReadiumPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookCount) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ReadiumMark)
                )
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