package com.example.readium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readium.data.model.ClubeLeitura
import androidx.compose.foundation.background
import com.example.readium.ui.theme.*

@Composable
fun SearchBookClubsScreen(
    onNavigateBack: () -> Unit
) {
    val clubesBusca = listOf(
        ClubeLeitura(nomeClube = "Torcida Leitora Ceará", publico = true),
        ClubeLeitura(nomeClube = "Fortaleza Book Club", publico = false),
        ClubeLeitura(nomeClube = "Ferrão & Literatura", publico = true),
        ClubeLeitura(nomeClube = "Icasa Readers", publico = false),
        ClubeLeitura(nomeClube = "Horizonte Literário FC", publico = true)
    )

    Scaffold(
        topBar = {
            ReadiumSimpleTopBar(
                title = "Procurar clubes",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            clubesBusca.forEach { clube ->
                ClubeCard(clube = clube)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}