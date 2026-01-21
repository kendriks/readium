package com.example.readium.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.readium.ui.theme.*
import com.example.readium.viewmodel.AddBookUiState
import java.util.Date

@Composable
fun AddBookScreen(
    uiState: AddBookUiState,
    onSave: (Book) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf(BookStatus.TO_READ) }
    var availableForTrade by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }


    LaunchedEffect(uiState.foundBook) {
        uiState.foundBook?.let {
            title = it.title
            authors = it.authors.joinToString(", ")
            publisher = it.publisher.orEmpty()
            description = it.description.orEmpty()
        }
    }

    Scaffold(
        topBar = {
            AddBookTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = onNavigateHome,
                onCreateClick = {},
                onProfileClick = onNavigateProfile
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
                        text = "Adicionar livro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack
                    )
                }
            }

            /* Capa do livro */
            item {
                FormCard {

                    Text(
                        text = "Capa do livro",
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack
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

                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Capa do livro",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = ReadiumGrayMedium,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Selecionar imagem",
                                    fontSize = 12.sp,
                                    color = ReadiumGrayMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Formatos aceitos: png, jpg, jpeg",
                        fontSize = 12.sp,
                        color = ReadiumGrayMedium
                    )
                }
            }

            /* Dados principais */
            item {
                FormCard {
                    ReadiumTextField(
                        label = "Título",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Digite o título do livro"
                    )

                    Spacer(Modifier.height(12.dp))

                    ReadiumTextField(
                        label = "Autores",
                        value = authors,
                        onValueChange = { authors = it },
                        placeholder = "Ex: J. K. Rowling, George R. R. Martin"
                    )

                    Spacer(Modifier.height(12.dp))

                    ReadiumTextField(
                        label = "Editora",
                        value = publisher,
                        onValueChange = { publisher = it },
                        placeholder = "Nome da editora"
                    )
                }
            }

            /* Descrição */
            item {
                FormCard {
                    ReadiumTextField(
                        label = "Descrição",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Escreva uma breve descrição do livro",
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }
            }

            /* Status + troca */
            item {
                FormCard {
                    StatusDropdown(status) { status = it }

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ReadiumCheckbox(
                            checked = availableForTrade,
                            onCheckedChange = { availableForTrade = it },
                            label = "Disponível para troca"
                        )
                    }
                }
            }

            /* Capa */
            if (selectedImageUri?.isNotBlank() ?: false) {
                item {
                    FormCard {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Capa do livro",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            /* Salvar */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onSave(
                                Book(
                                    title = title.trim(),
                                    authors = authors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                    isbn = null, // removido
                                    publisher = publisher.ifBlank { null },
                                    description = description.ifBlank { null },
                                    coverUrl = selectedImageUri,
                                    status = status,
                                    availableForTrade = availableForTrade,
                                    createdAt = Date(),
                                    ownerId = ""
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                    ) {
                        Text("Salvar livro", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

/* ================= COMPONENTES ================= */

@Composable
private fun AddBookTopBar(
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
                    text = "Novo livro",
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

@Composable
fun ReadiumTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String = "",
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ReadiumOnBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            isError = error.isNotEmpty(),
            singleLine = singleLine,
            textStyle = LocalTextStyle.current.copy(color = ReadiumOnBackground),
            placeholder = {
                Text(
                    text = placeholder,
                    color = ReadiumOnSurface.copy(alpha = 0.6f)
                )
            }
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = ReadiumError,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ReadiumCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ReadiumPrimary,
                uncheckedColor = ReadiumGrayMedium,
                checkmarkColor = ReadiumWhite
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = ReadiumOnBackground,
            fontSize = 16.sp
        )
    }
}
