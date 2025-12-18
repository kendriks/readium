package com.example.readium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import com.example.readium.ui.theme.*
import androidx.compose.ui.unit.sp
import com.example.readium.components.LayoutVariant
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CreateThematicListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddBookScreen: (nome: String, descricao: String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LayoutVariant(
        onNavigateBack,
        onNavigateToHome,
        onNavigateToProfile,
        "Nova Lista Temática",
     {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da Lista*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ReadiumPrimary,
                    unfocusedBorderColor = ReadiumGrayMedium,
                    focusedLabelColor = ReadiumPrimary,
                    unfocusedLabelColor = ReadiumGrayMedium,
                    cursorColor = ReadiumPrimary,
                    focusedTextColor = ReadiumBlack,
                    unfocusedTextColor = ReadiumBlack
                )
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ReadiumPrimary,
                    unfocusedBorderColor = ReadiumGrayMedium,
                    focusedLabelColor = ReadiumPrimary,
                    unfocusedLabelColor = ReadiumGrayMedium,
                    cursorColor = ReadiumPrimary,
                    focusedTextColor = ReadiumBlack,
                    unfocusedTextColor = ReadiumBlack
                )
            )

            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val nomeEncoded = Uri.encode(nome)
                        if (descricao.isBlank()){
                            descricao = "Sem descrição"
                        }
                        val descricaoEncoded = Uri.encode(descricao)
                        onNavigateToAddBookScreen(nomeEncoded, descricaoEncoded)
                    } else {
                        errorMessage = "Por favor, insira o nome da lista."
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
                Text(
                    text = "Adicionar livros à lista",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
         })
}