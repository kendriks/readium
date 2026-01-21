package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.data.User
import com.example.readium.data.model.Book
import com.example.readium.data.model.Post
import com.example.readium.ui.theme.*
import java.util.Date

@Composable
fun AddPostScreen(
    userName: String,
    userBooks: List<Book>,
    onPublish: (Post) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var rating by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AddPostTopBar(onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = onNavigateHome,
                onCreateClick = {},
                onProfileClick = onNavigateProfile
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(padding)
        ) {

            item {
                FormCard {
                    ReadiumTextField(
                        label = "Título do post",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Escreva um título"
                    )
                }
            }

            item {
                FormCard {
                    ReadiumTextField(
                        label = "Conteúdo",
                        value = content,
                        onValueChange = { content = it },
                        placeholder = "Compartilhe sua opinião...",
                        singleLine = false,
                        modifier = Modifier.height(160.dp)
                    )
                }
            }

            item {
                FormCard {
                    BookDropdown(
                        books = userBooks,
                        selectedBook = selectedBook,
                        onSelected = { selectedBook = it }
                    )
                }
            }

            item {
                FormCard {
                    Text(
                        text = "Nota",
                        fontWeight = FontWeight.Medium,
                        color = ReadiumOnBackground
                    )

                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt() },
                        steps = 4,
                        valueRange = 0f..5f,
                        colors = SliderDefaults.colors(
                            thumbColor = ReadiumPrimary,
                            activeTrackColor = ReadiumPrimary,
                            inactiveTrackColor = ReadiumGrayMedium.copy(alpha = 0.4f),
                            activeTickColor = ReadiumPrimary,
                            inactiveTickColor = ReadiumGrayMedium.copy(alpha = 0.4f)
                        )
                    )

                    Text(
                        text = "$rating / 5",
                        fontSize = 14.sp,
                        color = ReadiumGrayMedium
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            selectedBook?.let {
                                onPublish(
                                    Post(
                                        title = title.trim(),
                                        content = content.trim(),
                                        bookId = it.id,
                                        rating = rating,
                                        ownerId = "",
                                        createdAt = Date(),
                                        ownerName = userName

                                    )
                                )
                            }
                            onNavigateHome()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                    ) {
                        Text("Publicar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDropdown(
    books: List<Book>,
    selectedBook: Book?,
    onSelected: (Book) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Livro relacionado",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ReadiumOnBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedBook?.title ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        "Selecione um livro",
                        color = ReadiumOnSurface.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ReadiumOnBackground,
                    unfocusedTextColor = ReadiumOnBackground,
                    focusedContainerColor = ReadiumWhite,
                    unfocusedContainerColor = ReadiumWhite,
                    focusedBorderColor = ReadiumPrimary,
                    unfocusedBorderColor = ReadiumGrayMedium,
                    cursorColor = ReadiumPrimary,
                    focusedTrailingIconColor = ReadiumPrimary,
                    unfocusedTrailingIconColor = ReadiumGrayMedium
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(ReadiumWhite)
            ) {
                books.forEach { book ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = book.title,
                                color = ReadiumOnBackground
                            )
                        },
                        onClick = {
                            onSelected(book)
                            expanded = false
                        }
                    )
                }
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
            isSelected = false,
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            isSelected = true,
            onClick = {}
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
private fun AddPostTopBar(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            color = ReadiumPrimary,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
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
                    text = "Publicar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
@Composable
private fun FormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}