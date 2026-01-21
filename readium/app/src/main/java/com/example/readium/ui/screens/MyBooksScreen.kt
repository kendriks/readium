package com.example.readium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.readium.data.model.Book
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumBlack
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumWhite
import com.example.readium.viewmodel.BooksViewModel

@Composable
fun MyBooksScreen(
    viewModel: BooksViewModel,
    userId: String,
    onBookClick: (Book) -> Unit,
    onAddBookClick: () -> Unit,
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val books = viewModel.books

    LaunchedEffect(Unit) {
        viewModel.loadBooks(userId)
    }

    Scaffold(
        topBar = {
            MyBooksTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                onHomeClick = onNavigateToHome,
                onCreateClick = onAddBookClick,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = ReadiumPrimary
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books) { book ->
                BookListItem(
                    book = book,
                    onClick = { onBookClick(book) }
                )
            }
        }
    }
}

@Composable
private fun BookListItem(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReadiumWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = rememberAsyncImagePainter(book.coverUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumBlack
                )

                Text(
                    text = book.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = ReadiumBlack.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = book.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = ReadiumPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MyBooksTopBar(onNavigateBack: () -> Unit) {
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
                    text = "Meus Livros",
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
private fun ReadiumBottomBar(
    onHomeClick: () -> Unit,
    onCreateClick: () -> Unit,
    onProfileClick: () -> Unit,
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
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            onClick = onProfileClick
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
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
            tint = ReadiumGrayMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = ReadiumGrayMedium
        )
    }
}