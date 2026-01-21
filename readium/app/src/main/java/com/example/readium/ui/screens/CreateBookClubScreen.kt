package com.example.readium.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.ui.theme.ReadiumBackground
import com.example.readium.ui.theme.ReadiumBlack
import com.example.readium.ui.theme.ReadiumGrayMedium
import com.example.readium.ui.theme.ReadiumMark
import com.example.readium.ui.theme.ReadiumPrimary
import com.example.readium.ui.theme.ReadiumWhite
import com.example.readium.viewmodel.BookClubViewModel

@Composable
fun CreateBookClubScreen1(
    viewModel: BookClubViewModel, // Parâmetro ViewModel
    onNavigateBack: () -> Unit = {},
    onNavigateToNext: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    Scaffold(
        topBar = { CreateClubTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = { ReadiumBottomBar(onHomeClick = onNavigateToHome, onCreateClick = { }, onProfileClick = { }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "vamos começar...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Foto
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp).background(ReadiumMark.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, "Foto", tint = ReadiumMark, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Foto para o clube", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack)
                        Text("Escolha uma foto ou arraste para cá", fontSize = 12.sp, color = ReadiumGrayMedium)
                    }
                }
            }

            // Nome (Vínculo com ViewModel)
            item {
                CreateClubTextField(
                    label = "Nome do clube",
                    value = viewModel.draftName,
                    onValueChange = { viewModel.draftName = it },
                    placeholder = "@nome"
                )
            }

            // Descrição (Vínculo com ViewModel)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Descrição (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = viewModel.draftDescription,
                        onValueChange = { viewModel.draftDescription = it },
                        placeholder = { Text("Insira o texto aqui...", color = ReadiumGrayMedium, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary, unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite, unfocusedContainerColor = ReadiumWhite
                        )
                    )
                }
            }

            // Botão Continuar
            item {
                Button(
                    onClick = onNavigateToNext,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                ) {
                    Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CreateBookClubScreen2(
    viewModel: BookClubViewModel, // Parâmetro ViewModel
    onNavigateBack: () -> Unit = {},
    onCreateClub: () -> Unit = {}, // Callback de criação
    onNavigateToHome: () -> Unit = {}
) {
    // Inicializa o estado local do botão de tipo com base no ViewModel
    var clubTypeIndex by remember { mutableIntStateOf(if (viewModel.draftIsPrivate) 1 else 0) }

    // Sincroniza a mudança do botão com o ViewModel
    LaunchedEffect(clubTypeIndex) {
        viewModel.draftIsPrivate = (clubTypeIndex == 1)
    }

    Scaffold(
        topBar = { CreateClubTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = { ReadiumBottomBar(onHomeClick = onNavigateToHome, onCreateClick = { }, onProfileClick = { }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Text("alguns últimos ajustes...", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ReadiumPrimary, modifier = Modifier.padding(bottom = 16.dp))
            }

            // Tipo de Clube (Público vs Privado)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Selecionar tipo de clube", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack, modifier = Modifier.padding(bottom = 8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ReadiumGrayMedium.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ClubTypeButton(
                            label = "clube público",
                            isSelected = clubTypeIndex == 0,
                            onClick = { clubTypeIndex = 0 },
                            modifier = Modifier.weight(1f)
                        )
                        ClubTypeButton(
                            label = "privado (requer aprovação)",
                            isSelected = clubTypeIndex == 1,
                            onClick = { clubTypeIndex = 1 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Livro do Mês (Vínculo com ViewModel)
            item {
                CreateClubTextField(
                    label = "Selecionar livro do mês",
                    value = viewModel.draftBookOfMonth,
                    onValueChange = { viewModel.draftBookOfMonth = it },
                    placeholder = "@nome do livro"
                )
            }

            // Botão Criar
            item {
                Button(
                    onClick = onCreateClub,
                    enabled = !viewModel.isLoading, // Desabilita se estiver carregando
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = ReadiumWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Criar clube", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
private fun CreateClubTopBar(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp)) {
        Surface(modifier = Modifier.fillMaxWidth().height(76.dp), color = ReadiumPrimary) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = ReadiumWhite) }
                Text("Criar novo clube", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ReadiumWhite)
            }
        }
    }
}

@Composable
private fun CreateClubTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = ReadiumGrayMedium, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ReadiumPrimary, unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f), focusedContainerColor = ReadiumWhite, unfocusedContainerColor = ReadiumWhite)
        )
    }
}

@Composable
private fun ClubTypeButton(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier.height(40.dp), shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) ReadiumPrimary else ReadiumWhite, contentColor = if (isSelected) ReadiumWhite else ReadiumGrayMedium),
        border = if (!isSelected) BorderStroke(1.dp, ReadiumGrayMedium.copy(alpha = 0.5f)) else null
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ReadiumBottomBar(onHomeClick: () -> Unit, onCreateClick: () -> Unit, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).background(ReadiumWhite).border(1.dp, ReadiumGrayMedium.copy(alpha = 0.2f)),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(Icons.Outlined.Home, "home", onHomeClick)
        BottomBarItem(Icons.Outlined.AddBox, "criar", onCreateClick)
        BottomBarItem(Icons.Outlined.Person, "perfil", onProfileClick)
    }
}

@Composable
private fun BottomBarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = ReadiumGrayMedium, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = ReadiumGrayMedium)
    }
}