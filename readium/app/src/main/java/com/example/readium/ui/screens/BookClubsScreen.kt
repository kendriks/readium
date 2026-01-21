package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.data.model.ClubeLeitura
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumBlack
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumWhite
import com.example.readium.viewmodel.BookClubViewModel

@Composable
fun BookClubsScreen(
    viewModel: BookClubViewModel, // Parâmetro ViewModel
    onNavigateBack: () -> Unit,
    onNavigateToCreateClub: () -> Unit,
    onNavigateToSearchClubs: () -> Unit
) {
    val clubs = viewModel.userClubs

    Scaffold(
        topBar = { ReadiumSimpleTopBar(title = "Meus Clubes", onBackClick = onNavigateBack) }
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
            } else if (clubs.isEmpty()) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Você ainda não participa de clubes.", color = ReadiumGrayMedium)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(clubs) { clube ->
                        ClubeCard(clube = clube)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Criar
            OutlinedButton(
                onClick = onNavigateToCreateClub,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ReadiumPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(ReadiumPrimary))
            ) {
                Text("Criar clube +")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Procurar
            Button(
                onClick = onNavigateToSearchClubs,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
            ) {
                Text("Procurar clubes")
            }
        }
    }
}

@Composable
fun ClubeCard(clube: ClubeLeitura, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = clube.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack)
                Text(text = clube.description, fontSize = 12.sp, color = ReadiumGrayMedium, maxLines = 2)
            }
            if (clube.isPrivate) {
                Icon(Icons.Default.Lock, "Privado", tint = ReadiumGrayMedium)
            }
        }
    }
}

@Composable
fun ReadiumSimpleTopBar(title: String, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp)) {
        Surface(modifier = Modifier.fillMaxWidth().height(76.dp), color = ReadiumPrimary) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = ReadiumWhite) }
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ReadiumWhite)
            }
        }
    }
}