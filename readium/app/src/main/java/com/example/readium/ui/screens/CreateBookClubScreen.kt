package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.readium.ui.theme.*

@Composable
fun CreateBookClubScreen1(
    onNavigateBack: () -> Unit = {},
    onNavigateToNext: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var clubName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var moderators by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CreateClubTopBar(title = "Criar um novo clube de leitura", onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onCreateClick = { },
                onProfileClick = { }
            )
        }
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

            //foto 
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(ReadiumMark.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Foto",
                                tint = ReadiumMark,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Foto para o clube",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReadiumBlack
                        )
                        Text(
                            text = "Escolha uma foto ou arraste para cá",
                            fontSize = 12.sp,
                            color = ReadiumGrayMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            //nome
            item {
                CreateClubTextField(
                    label = "Nome do clube",
                    value = clubName,
                    onValueChange = { clubName = it },
                    placeholder = "@nome"
                )
            }

            //descrição
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Descrição (opcional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = {
                            Text(
                                text = "Intre o texto aqui...",
                                color = ReadiumGrayMedium,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary,
                            unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite,
                            unfocusedContainerColor = ReadiumWhite
                        )
                    )
                }
            }

            //aidcionar moderadores
            item {
                CreateClubTextField(
                    label = "Adicionar moderadores (opcional)",
                    value = moderators,
                    onValueChange = { moderators = it },
                    placeholder = "Digite o nome do usuário aqui..."
                )
            }

            //regras do clube
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Regras para o clube (opcional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = rules,
                        onValueChange = { rules = it },
                        placeholder = {
                            Text(
                                text = "Intre o texto aqui...",
                                color = ReadiumGrayMedium,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary,
                            unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite,
                            unfocusedContainerColor = ReadiumWhite
                        )
                    )
                }
            }

            //continuarr
            item {
                Button(
                    onClick = onNavigateToNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReadiumPrimary,
                        contentColor = ReadiumWhite
                    )
                ) {
                    Text(
                        text = "Continuar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CreateBookClubScreen2(
    onNavigateBack: () -> Unit = {},
    onCreateClub: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var bookOfMonth by remember { mutableStateOf("") }
    var metas by remember { mutableStateOf("") }
    var clubType by remember { mutableStateOf(0) } // 0: público, 1: membros aprovados
    var approvedMembers by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CreateClubTopBar(title = "Criar um novo clube de leitura", onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onCreateClick = { },
                onProfileClick = { }
            )
        }
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
                    text = "alguns últimos ajustes...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            //tipos de clube
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Selecionar tipo de clube",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ReadiumGrayMedium.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ClubTypeButton(
                            label = "clube público",
                            isSelected = clubType == 0,
                            onClick = { clubType = 0 },
                            modifier = Modifier.weight(1f)
                        )
                        ClubTypeButton(
                            label = "membros aprovados",
                            isSelected = clubType == 1,
                            onClick = { clubType = 1 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Seção de membros aprovados (mostrar apenas se selecionado)
            if (clubType == 1) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Membros aprovados",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReadiumBlack,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = approvedMembers,
                            onValueChange = { approvedMembers = it },
                            placeholder = {
                                Text(
                                    text = "Digite os nomes dos membros aprovados...",
                                    color = ReadiumGrayMedium,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ReadiumPrimary,
                                unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                                focusedContainerColor = ReadiumWhite,
                                unfocusedContainerColor = ReadiumWhite
                            )
                        )
                    }
                }
            }

            //selecionar livro do mês
            item {
                CreateClubTextField(
                    label = "Selecionar livro do mês",
                    value = bookOfMonth,
                    onValueChange = { bookOfMonth = it },
                    placeholder = "@nome"
                )
            }

            //metas e sprints
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Metas e sprints",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReadiumBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = metas,
                        onValueChange = { metas = it },
                        placeholder = {
                            Text(
                                text = "Intre o texto aqui...",
                                color = ReadiumGrayMedium,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary,
                            unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite,
                            unfocusedContainerColor = ReadiumWhite
                        )
                    )
                }
            }

            //criar clube
            item {
                Button(
                    onClick = onCreateClub,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReadiumPrimary,
                        contentColor = ReadiumWhite
                    )
                ) {
                    Text(
                        text = "Criar clube",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun CreateClubTopBar(title: String, onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp, bottom = 0.dp)) {
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
                    .padding(start = 0.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ReadiumWhite
                    )
                }

                Text(
                    text = title,
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
private fun CreateClubTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ReadiumBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = ReadiumGrayMedium,
                    fontSize = 12.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ReadiumPrimary,
                unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                focusedContainerColor = ReadiumWhite,
                unfocusedContainerColor = ReadiumWhite
            )
        )
    }
}

@Composable
private fun ClubTypeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) ReadiumPrimary else ReadiumWhite,
            contentColor = if (isSelected) ReadiumWhite else ReadiumGrayMedium
        ),
        border = if (!isSelected) BorderStroke(1.dp, ReadiumGrayMedium.copy(alpha = 0.5f)) else null
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReadiumBottomBar(
    selectedItem: Int,
    onHomeClick: () -> Unit,
    onCreateClick: () -> Unit,
    onProfileClick: () -> Unit
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
            isSelected = selectedItem == 0,
            onClick = onHomeClick
        )

        BottomBarItem(
            icon = Icons.Outlined.AddBox,
            label = "criar",
            isSelected = selectedItem == 1,
            onClick = onCreateClick
        )

        BottomBarItem(
            icon = Icons.Outlined.Person,
            label = "perfil",
            isSelected = selectedItem == 2,
            onClick = onProfileClick
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
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
            tint = if (isSelected) ReadiumPrimary else ReadiumGrayMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) ReadiumPrimary else ReadiumGrayMedium
        )
    }
}