package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumBlack
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumSecondary
import com.example.readium.viewmodel.BookClubViewModel

@Composable
fun SearchBookClubsScreen(
    viewModel: BookClubViewModel,
    currentUserId: String,
    onNavigateBack: () -> Unit
) {
    val publicClubs = viewModel.publicClubs

    Scaffold(
        topBar = { ReadiumSimpleTopBar(title = "Explorar Clubes", onBackClick = onNavigateBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ReadiumPrimary)
                }
            } else if (publicClubs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum clube público encontrado.", color = ReadiumGrayMedium)
                }
            } else {
                LazyColumn {
                    items(publicClubs) { clube ->
                        Column {
                            ClubeCard(clube = clube)

                            // Verifica se já é membro (opcional, para UI mais refinada)
                            if (!clube.members.contains(currentUserId)) {
                                Button(
                                    onClick = { viewModel.joinClub(clube, currentUserId) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumSecondary)
                                ) {
                                    Text("Entrar no Clube", color = ReadiumBlack)
                                }
                            } else {
                                Text(
                                    "Você já é membro",
                                    color = ReadiumPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}