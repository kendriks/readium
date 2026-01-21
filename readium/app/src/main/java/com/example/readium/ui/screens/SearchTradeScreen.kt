package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumBlack
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumWhite
import com.example.readium.viewmodel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SearchTradeScreen(
    viewModel: TradeViewModel,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Usuário"

    // Carrega livros iniciais
    LaunchedEffect(Unit) {
        viewModel.searchBooks("")
    }

    Scaffold(
        topBar = {
            TradeTopBar(onNavigateBack)
        },
        snackbarHost = {
            // Exibe mensagem de sucesso/erro
            viewModel.proposalMessage?.let { msg ->
                Snackbar(
                    containerColor = ReadiumPrimary,
                    contentColor = ReadiumWhite,
                    action = {
                        TextButton(onClick = { viewModel.clearMessage() }) {
                            Text("OK", color = ReadiumWhite)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Barra de Busca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchBooks(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por título ou gênero...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ReadiumPrimary,
                    unfocusedBorderColor = ReadiumGrayMedium,
                    focusedContainerColor = ReadiumWhite,
                    unfocusedContainerColor = ReadiumWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ReadiumPrimary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filtra para não mostrar os próprios livros do usuário na busca de troca
                    val filteredList = viewModel.searchResults.filter { it.ownerId != currentUserId }

                    if (filteredList.isEmpty()) {
                        item {
                            Text(
                                "Nenhum livro disponível para troca encontrado.",
                                color = ReadiumGrayMedium,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                        }
                    }

                    items(filteredList) { book ->
                        TradeBookItem(
                            book = book,
                            onRequestTrade = {
                                if (currentUserId != null) {
                                    viewModel.sendProposal(
                                        senderId = currentUserId,
                                        senderName = currentUserName,
                                        book = book
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TradeBookItem(book: Book, onRequestTrade: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Capa
            AsyncImage(
                model = book.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ReadiumGrayMedium.copy(alpha = 0.3f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ReadiumBlack
                )
                Text(
                    text = book.getMainAuthor(),
                    fontSize = 14.sp,
                    color = ReadiumGrayMedium
                )
                Text(
                    text = "Dono: ${book.ownerDisplayName ?: "Desconhecido"}",
                    fontSize = 12.sp,
                    color = ReadiumPrimary
                )
                if (!book.condition.isNullOrBlank()) {
                    Text(
                        text = "Condição: ${book.condition}",
                        fontSize = 12.sp,
                        color = ReadiumBlack.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(onClick = onRequestTrade) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Solicitar Troca",
                    tint = ReadiumPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun TradeTopBar(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(76.dp),
            color = ReadiumPrimary,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = ReadiumWhite)
                }
                Text(
                    "Troca de Livros",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite
                )
            }
        }
    }
}