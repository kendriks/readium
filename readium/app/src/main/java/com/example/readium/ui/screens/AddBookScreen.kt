package com.example.readium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.readium.data.model.Book
import com.example.readium.data.model.BookStatus
import com.example.readium.viewmodel.AddBookViewModel
import com.example.readium.ui.theme.ReadiumTheme
import java.util.Date

@Composable
fun AddBookScreen(
    uiState: com.example.readium.viewmodel.AddBookUiState,
    onSearchIsbn: (String) -> Unit,
    onSave: (Book) -> Unit,
    onBack: () -> Unit
) {
    ReadiumAddBookContent(
        uiState = uiState,
        onSearchIsbn = onSearchIsbn,
        onSave = onSave,
        onBack = onBack
    )
}

@Composable
fun ReadiumAddBookContent(
    uiState: com.example.readium.viewmodel.AddBookUiState,
    onSearchIsbn: (String) -> Unit,
    onSave: (Book) -> Unit,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var isbn by remember { mutableStateOf("") }
    var title by remember { mutableStateOf(uiState.foundBook?.title ?: "") }
    var authorsText by remember { mutableStateOf(uiState.foundBook?.authors?.joinToString(", ") ?: "") }
    var publisher by remember { mutableStateOf(uiState.foundBook?.publisher ?: "") }
    var coverUrl by remember { mutableStateOf(uiState.foundBook?.coverUrl ?: "") }
    var description by remember { mutableStateOf(uiState.foundBook?.description ?: "") }
    var status by remember { mutableStateOf(BookStatus.TO_READ) }
    var availableForTrade by remember { mutableStateOf(false) }
    var condition by remember { mutableStateOf("") }

    // Atualiza campos quando uiState.foundBook muda (buscar por ISBN)
    LaunchedEffect(uiState.foundBook) {
        uiState.foundBook?.let { b ->
            title = b.title
            authorsText = b.authors.joinToString(", ")
            publisher = b.publisher ?: ""
            coverUrl = b.coverUrl ?: ""
            description = b.description ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .background(colorScheme.background)
    ) {
        OutlinedTextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("ISBN") },
            trailingIcon = {
                IconButton(onClick = { onSearchIsbn(isbn.trim()) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar ISBN")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = authorsText,
            onValueChange = { authorsText = it },
            label = { Text("Autores (separados por ,)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            label = { Text("Editora") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = coverUrl,
            onValueChange = { coverUrl = it },
            label = { Text("URL da capa (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Status:")
            Spacer(modifier = Modifier.width(12.dp))
            Text(status.name, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = availableForTrade, onCheckedChange = { availableForTrade = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Disponível para troca")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading || uiState.isbnSearchLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        uiState.error?.let { err ->
            Text(text = "Erro: $err", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = {
            val book = Book(
                title = title.trim(),
                authors = authorsText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                isbn = isbn.trim().ifBlank { null },
                coverUrl = coverUrl.ifBlank { null },
                publisher = publisher.ifBlank { null },
                description = description.ifBlank { null },
                ownerId = "", // será preenchido pelo ViewModel ao salvar
                createdAt = Date()
            )
            onSave(book)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Salvar livro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (coverUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(model = coverUrl),
                contentDescription = "Preview da capa",
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownMenuStatus(selected: BookStatus, onSelected: (BookStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = BookStatus.values().toList()
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selected.name,
            onValueChange = {},
            label = { Text("Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { s ->
                DropdownMenuItem(text = { Text(s.name) }, onClick = {
                    onSelected(s)
                    expanded = false
                })
            }
        }
    }
}


/* ---------- Previews ---------- */

@Preview(showBackground = true, name = "AddBook - Preview", widthDp = 360, heightDp = 800)
@Composable
fun AddBookScreenPreview() {
    ReadiumTheme {
        val sampleState = com.example.readium.viewmodel.AddBookUiState(
            isLoading = false,
            isbnSearchLoading = false,
            foundBook = Book(
                title = "1984",
                authors = listOf("George Orwell"),
                isbn = "9780141036144",
                coverUrl = "https://picsum.photos/200",
                publisher = "Penguin",
                publishedDate = "1949",
                description = "Romance distópico clássico.",
                ownerId = "uid",
                createdAt = Date()
            ),
            error = null
        )
        ReadiumAddBookContent(
            uiState = sampleState,
            onSearchIsbn = {},
            onSave = {},
            onBack = {}
        )
    }
}
