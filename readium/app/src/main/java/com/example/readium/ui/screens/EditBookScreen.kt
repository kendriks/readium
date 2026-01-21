package com.example.readium.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.readium.data.model.Book
import com.example.readium.data.model.BookStatus
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumOnBackground
import com.example.readium.ui.theme.ReadiumOnSurface
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumWhite

@Composable
fun EditBookScreen(
    book: Book,
    onSave: (Book) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMyBooks: () -> Unit
) {
    var title by remember { mutableStateOf(book.title) }
    var authors by remember { mutableStateOf(book.authors.joinToString(", ")) }
    var publisher by remember { mutableStateOf(book.publisher.orEmpty()) }
    var description by remember { mutableStateOf(book.description.orEmpty()) }
    var status by remember { mutableStateOf(book.status) }
    var availableForTrade by remember { mutableStateOf(book.availableForTrade) }
    var coverUrl by remember { mutableStateOf(book.coverUrl) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        coverUrl = uri?.toString()
    }


    Scaffold(
        topBar = {
            EditBookTopBar(
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = onNavigateToHome,
                onCreateClick = { /* não usado aqui */ },
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Editar livro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumOnBackground
                    )
                }
            }

            item {
                FormCard {
                    Text(
                        text = "Capa do livro",
                        fontWeight = FontWeight.Bold,
                        color = ReadiumOnBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ReadiumGrayMedium.copy(alpha = 0.2f))
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!coverUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = coverUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Selecionar imagem", color = ReadiumGrayMedium)
                        }
                    }
                }
            }

            item {
                FormCard {
                    ReadiumTextField(
                        label = "Título",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Título do livro"
                    )

                    ReadiumTextField(
                        label = "Autores",
                        value = authors,
                        onValueChange = { authors = it },
                        placeholder = "Autores"
                    )

                    ReadiumTextField(
                        label = "Editora",
                        value = publisher,
                        onValueChange = { publisher = it },
                        placeholder = "Editora"
                    )
                }
            }

            item {
                FormCard {
                    ReadiumTextField(
                        label = "Descrição",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Descrição",
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }
            }

            item {
                FormCard {
                    StatusDropdown(
                        selected = status,
                        onSelected = { status = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    ReadiumCheckbox(
                        checked = availableForTrade,
                        onCheckedChange = { availableForTrade = it },
                        label = "Disponível para troca"
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        onSave(
                            book.copy(
                                title = title.trim(),
                                authors = authors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                publisher = publisher.ifBlank { null },
                                description = description.ifBlank { null },
                                coverUrl = coverUrl,
                                status = status,
                                availableForTrade = availableForTrade
                            )
                        )
                        onNavigateToMyBooks()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(ReadiumPrimary)
                ) {
                    Text("Salvar alterações", fontWeight = FontWeight.Bold)
                }
            }

            /* espaço extra pra não colar na bottom bar */
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun EditBookTopBar(
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
                    text = "Editar livro",
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(
    selected: BookStatus,
    onSelected: (BookStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Status",
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
                value = selected.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                placeholder = {
                    Text(
                        "Selecione o status",
                        color = ReadiumOnSurface.copy(alpha = 0.6f)
                    )
                },
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
                    focusedPlaceholderColor = ReadiumOnSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = ReadiumOnSurface.copy(alpha = 0.6f),
                    focusedTrailingIconColor = ReadiumPrimary,
                    unfocusedTrailingIconColor = ReadiumGrayMedium
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(ReadiumWhite)
            ) {
                BookStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = status.name,
                                color = ReadiumOnBackground
                            )
                        },
                        onClick = {
                            onSelected(status)
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
            isSelected = false,
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            isSelected = true,
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
