package com.example.readium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readium.data.model.ClubeLeitura
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.readium.ui.theme.*

@Composable
fun BookClubsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreateClub: () -> Unit,
    onNavigateToSearchClubs: () -> Unit
) {
    var clubs by remember { mutableStateOf<List<ClubeLeitura>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("clubs")
            .get()
            .addOnSuccessListener { result ->
                val clubesFirestore = result.documents.mapNotNull { doc ->
                    doc.toObject(ClubeLeitura::class.java)?.copy(id = doc.id)
                }

                clubs = clubesFirestore + clubesMockados()
            }
    }

    Scaffold(
        topBar = {
            ReadiumSimpleTopBar(
                title = "Clubes do livro",
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

            clubs.forEach { clube ->
                ClubeCard(clube = clube)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão criar clube (igual ao original, mas no tema)
            OutlinedButton(
                onClick = onNavigateToCreateClub,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ReadiumPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(ReadiumPrimary)
                )
            ) {
                Text("Criar clube +")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToSearchClubs,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReadiumPrimary,
                    contentColor = ReadiumWhite
                )
            ) {
                Text("Procurar clubes")
            }
        }
    }
}

@Composable
fun ReadiumSimpleTopBar(
    title: String,
    onBackClick: () -> Unit
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
            shape = RoundedCornerShape(0.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ReadiumOnPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = ReadiumOnPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}



@Composable
fun ClubeCard(
    clube: ClubeLeitura
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
            //.clickable { onClick() }, Não implementado ainda
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clube.nomeClube,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReadiumBlack
                )
                Text(
                    text = clube.descricaoClube,
                    style = MaterialTheme.typography.bodySmall,
                    color = ReadiumGrayMedium
                )
            }

            if (!clube.publico) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Clube privado",
                    tint = ReadiumGrayMedium
                )
            }
        }
    }
}

private fun clubesMockados(): List<ClubeLeitura> =
    listOf(
        ClubeLeitura(
            id = "mock_ceara",
            nomeClube = "Clube do Livro Ceará SC",
            descricaoClube = "Leituras alvinegras",
            publico = true
        ),
        ClubeLeitura(
            id = "mock_fortaleza",
            nomeClube = "Leões da Leitura",
            descricaoClube = "Clube do Fortaleza EC",
            publico = false
        ),
        ClubeLeitura(
            id = "mock_ferroviario",
            nomeClube = "Ferrão Literário",
            descricaoClube = "Discussões semanais",
            publico = true
        )
    )