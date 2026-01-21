package com.example.readium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.R
import com.example.readium.ui.theme.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.viewmodel.AuthViewModel
import com.example.readium.viewmodel.ProfileUpdateState
import coil.compose.AsyncImage
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val updateState by authViewModel.profileUpdateState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    // Carregar dados
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            email = it.email
            bio = it.biography
            city = it.city
            state = it.state
        }
    }

    // Feedback de sucesso/erro
    LaunchedEffect(updateState) {
        when (val st = updateState) {
            is ProfileUpdateState.Success -> {
                isSaving = false
                authViewModel.resetProfileUpdateState()
                onNavigateBack()
            }
            is ProfileUpdateState.Error -> {
                isSaving = false
                snackbarHostState.showSnackbar(st.message)
                authViewModel.resetProfileUpdateState()
            }
            else -> {}
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditProfileTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            ReadiumBottomBar(
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // -- FOTO DE PERFIL --
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(ReadiumGrayMedium)
                        ) {
                            val photoUrl = selectedImageUri ?: userProfile?.profilePhotoUrl
                            if (!photoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "avatar",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = "avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        // ícone de lápis
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(ReadiumPrimary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, "Editar", tint = ReadiumWhite, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Títulos foto
            item {
                Text(
                    text = "Alterar foto de perfil",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "São aceitos arquivos .png, .jpe e .jpeg",
                    fontSize = 12.sp, color = ReadiumGrayMedium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Campos Nome e Email
            item { EditFieldSection(Icons.Default.Edit, "Alterar nome", name, { name = it }, "Name") }
            item { EditFieldSection(Icons.Default.Email, "Alterar email", email, { }, "Email") }

            // Campo Biografia
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.Edit, null, tint = ReadiumBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Alterar biografia", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack)
                    }
                    OutlinedTextField(
                        value = bio, onValueChange = { bio = it },
                        placeholder = { Text("Conte um pouco sobre você", color = ReadiumGrayMedium) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary, unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite, unfocusedContainerColor = ReadiumWhite
                        )
                    )
                }
            }

            // -- NOVO: CAMPOS DE LOCALIZAÇÃO --
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    // Cidade
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        EditFieldSection(
                            icon = Icons.Default.LocationOn,
                            label = "Cidade",
                            value = city,
                            onValueChange = { city = it },
                            placeholder = "Ex: São Paulo"
                        )
                    }
                    // Estado (UF)
                    Column(modifier = Modifier.width(100.dp)) {
                        EditFieldSection(
                            icon = Icons.Default.Map,
                            label = "UF",
                            value = state,
                            onValueChange = { if (it.length <= 2) state = it.uppercase() },
                            placeholder = "SP"
                        )
                    }
                }
            }

            // Campo Senha
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.Lock, null, tint = ReadiumBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Alterar senha", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack)
                    }

                    val isPasswordError = password.isNotEmpty() && !authViewModel.validatePassword(password)
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        placeholder = { Text("**********", color = ReadiumGrayMedium) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        isError = isPasswordError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ReadiumPrimary, unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                            focusedContainerColor = ReadiumWhite, unfocusedContainerColor = ReadiumWhite, errorBorderColor = ReadiumError
                        )
                    )
                    if (isPasswordError) {
                        Text(
                            text = "A senha deve ter no mínimo 8 caracteres, contendo letras e números.",
                            color = ReadiumError, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
            //botão de salvar alterações
            item {
                Button(
                    onClick = {
                        if (password.isNotEmpty()) {
                            if (!authViewModel.validatePassword(password)) {
                                scope.launch { snackbarHostState.showSnackbar("Senha inválida.") }
                                return@Button
                            }
                            authViewModel.updatePassword(password)
                        }

                        // 2. Atualizar perfil (incluindo Cidade e Estado)
                        authViewModel.updateUserProfile(
                            name = name,
                            biography = bio,
                            profilePhotoUri = selectedImageUri,
                            city = city,   // Passa novo valor
                            state = state  // Passa novo valor
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary, contentColor = ReadiumWhite)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ReadiumWhite)
                    } else {
                        Text("Salvar alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun EditProfileTopBar(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp, bottom = 0.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(76.dp),
            color = ReadiumPrimary, shape = RoundedCornerShape(0.dp), tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 0.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = ReadiumWhite)
                }
                Text("Configurações", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ReadiumWhite, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EditFieldSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = ReadiumBlack, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReadiumBlack)
        }
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = ReadiumGrayMedium) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ReadiumPrimary, unfocusedBorderColor = ReadiumGrayMedium.copy(alpha = 0.5f),
                focusedContainerColor = ReadiumWhite, unfocusedContainerColor = ReadiumWhite
            )
        )
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
        Icon(imageVector = icon, contentDescription = label, tint = ReadiumGrayMedium, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = ReadiumGrayMedium)
    }
}