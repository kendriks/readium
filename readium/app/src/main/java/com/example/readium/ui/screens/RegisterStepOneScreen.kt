package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStepOneScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStepTwo: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    fun validateForm(): Boolean {
        var isValid = true
        
        //valida nome
        if (name.isBlank()) {
            nameError = "Nome é obrigatório"
            isValid = false
        } else {
            nameError = ""
        }
        
        //valida email
        if (email.isBlank()) {
            emailError = "E-mail é obrigatório"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "E-mail inválido"
            isValid = false
        } else {
            emailError = ""
        }
        
        //valida senha
        if (password.isBlank()) {
            passwordError = "Senha é obrigatória"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Senha deve ter pelo menos 8 caracteres"
            isValid = false
        } else if (!password.matches(Regex(".*[a-zA-Z].*")) || !password.matches(Regex(".*\\d.*"))) {
            passwordError = "Senha deve conter letras e números"
            isValid = false
        } else {
            passwordError = ""
        }
        
        //valida confirmação de senha
        if (confirmPassword != password) {
            confirmPasswordError = "Senhas não coincidem"
            isValid = false
        } else {
            confirmPasswordError = ""
        }
        
        return isValid
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
            Text(
                text = "Nome",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (nameError.isNotEmpty()) 4.dp else 16.dp),
                isError = nameError.isNotEmpty(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("Nome", color = Color(0xFF9E9E9E)) }
            )
            if (nameError.isNotEmpty()) {
                Text(
                    text = nameError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Text(
                text = "E-mail",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (emailError.isNotEmpty()) 4.dp else 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError.isNotEmpty(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("nome@email.com", color = Color(0xFF9E9E9E)) }
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Text(
                text = "Senha",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (passwordError.isNotEmpty()) 4.dp else 16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                isError = passwordError.isNotEmpty(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("Insira sua senha", color = Color(0xFF9E9E9E)) }
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Text(
                text = "Confirmação de senha",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    confirmPasswordError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (confirmPasswordError.isNotEmpty()) 4.dp else 32.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                isError = confirmPasswordError.isNotEmpty(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                placeholder = { Text("Confirme sua senha", color = Color(0xFF9E9E9E)) }
            )
            if (confirmPasswordError.isNotEmpty()) {
                Text(
                    text = confirmPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        onNavigateToStepTwo(name, email, password, confirmPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF4B942)
                )
            ) {
                Text(
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
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
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}