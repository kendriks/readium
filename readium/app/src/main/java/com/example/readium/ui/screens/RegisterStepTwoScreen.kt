package com.example.readium.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.viewmodel.AuthViewModel
import com.example.readium.viewmodel.AuthState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStepTwoScreen(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNavigateBack: () -> Unit,
    onRegistrationComplete: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var biography by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    
    //selecionar imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }
    
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onRegistrationComplete()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Cadastre-se",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4B942),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFF4B942), CircleShape)
                        .clickable {
                            //abre seletor de imagens
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Foto selecionada",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Selecionar foto",
                            tint = Color(0xFFF4B942),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "foto de perfil",
                        fontSize = 14.sp,
                        color = Color(0xFFF4B942)
                    )

                    Text(
                        text = "faça um upload dos seus arquivos ou tire uma foto agora.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
            Text(
                text = "Nome de usuário",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("@nome", color = Color(0xFF9E9E9E)) }
            )
            Text(
                text = "Biografia (opcional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = biography,
                onValueChange = { biography = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 16.dp),
                maxLines = 3,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("Insira o texto aqui...", color = Color(0xFF9E9E9E)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            //mostra erros
            val currentAuthState = authState
            if (currentAuthState is AuthState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = currentAuthState.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    authViewModel.signUp(
                        name = name,
                        email = email,
                        password = password,
                        biography = biography,
                        profilePhotoUri = selectedImageUri
                    )
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF4B942)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Continuar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Você já é cadastrado? ",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "Faça login",
                    fontSize = 14.sp,
                    color = Color(0xFFF4B942),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateBack /*ainda falta implementar para que ele volte diretamente para login*/ }
                )
            }
        }
    }
}