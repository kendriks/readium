package com.example.readium.ui.screens

import android.widget.Toast
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.readium.components.LayoutVariant
import com.example.readium.data.model.Book
import com.example.readium.data.model.ThematicList
import com.example.readium.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddBooksOnListScreen(
    nomeListaEncoded: String,
    descricaoListaEncoded: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val nomeLista = Uri.decode(nomeListaEncoded)
    val descricaoLista = Uri.decode(descricaoListaEncoded)

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var selectedBookIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(Unit) {
        db.collection("books").get().addOnSuccessListener { result ->
            books = result.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.apply {
                    id = doc.id
                }
            }
        }
    }

    LayoutVariant(
        onNavigateBack,
        onNavigateToHome,
        onNavigateToProfile,
        "Adicionar livros à lista",
     {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(16.dp)
        ) {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(books) { book ->
                    BookSelectableItem(
                        book = book,
                        isSelected = selectedBookIds.contains(book.id),
                        onToggle = {
                            selectedBookIds =
                                if (selectedBookIds.contains(book.id))
                                    selectedBookIds - book.id
                                else
                                    selectedBookIds + book.id
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedBookIds.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Selecione ao menos um livro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val livrosSelecionados =
                        books.filter { selectedBookIds.contains(it.id) }

                    val novaLista = ThematicList(
                        id = System.currentTimeMillis().toString(),
                        nome = nomeLista,
                        descricao = descricaoLista,
                        livros = livrosSelecionados
                    )

                    db.collection("listasTematicas")
                        .document(novaLista.id)
                        .set(novaLista)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Lista criada com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onNavigateToProfile()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Erro ao salvar lista",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReadiumPrimary,
                    contentColor = ReadiumWhite
                )
            ) {
                Text("Salvar lista")
            }
        }
    })
}

@Composable
private fun BookSelectableItem(
    book: Book,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                ReadiumPrimary.copy(alpha = 0.1f)
            else
                ReadiumWhite
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(48.dp)
                    .height(72.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    color = ReadiumBlack,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = book.getMainAuthor(),
                    color = ReadiumGrayMedium,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}