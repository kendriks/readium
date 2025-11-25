package com.example.readium.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.readium.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.viewmodel.AuthViewModel
import com.example.readium.viewmodel.AuthState
import com.example.readium.ui.theme.ReadiumTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    
    //login com o google
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        println("Google Sign-In: Result code = ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                println("Google Sign-In: Account = ${account.email}")
                account.idToken?.let { idToken ->
                    println("Google Sign-In: ID Token obtido")
                    authViewModel.signInWithGoogle(idToken)
                } ?: run {
                    println("Google Sign-In: ID Token é null")
                }
            } catch (e: ApiException) {
                println("Google Sign-In: Erro ApiException = ${e.message}, statusCode = ${e.statusCode}")
            }
        } else {
            println("Google Sign-In: Cancelado pelo usuário ou erro")
        }
    }
    
    //bserva estado de autenticação
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            else -> {}
        }
    }

    fun validateForm(): Boolean {
        var isValid = true
        
        if (email.isBlank()) {
            emailError = "E-mail é obrigatório"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "E-mail inválido"
            isValid = false
        } else {
            emailError = ""
        }
        
        if (password.isBlank()) {
            passwordError = "Senha é obrigatória"
            isValid = false
        } else {
            passwordError = ""
        }
        
        return isValid
    }

    fun handleGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("339444994596-1ojf64rde27u5e95dhlc04i29tofb5nf.apps.googleusercontent.com")
            .requestEmail()
            .build()
            
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        
        //faz logout da conta atual para poder escolher uma conta novamente
        googleSignInClient.signOut().addOnCompleteListener {
            //inicia processo de login após logout
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Faça seu login!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF4B942),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = "E-mail",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
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
            placeholder = { Text("Insira seu e-mail", color = Color(0xFF9E9E9E)) }
        )
        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
        }
        Text(
            text = "Senha",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (passwordError.isNotEmpty()) 4.dp else 8.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )
        }
        TextButton(
            onClick = { /*ainda falta implementar*/ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Esqueceu a senha?",
                color = Color(0xFFF4B942),
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
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

        Button(
            onClick = {
                if (validateForm()) {
                    authViewModel.signIn(email, password)
                }
            },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF4B942)
            )
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Entrar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "ou faça login com",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { handleGoogleSignIn() },
                modifier = Modifier
                    .size(60.dp)
                    .border(1.dp, Color.Gray, CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Login com Google",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Não tem conta? ",
                fontSize = 14.sp
            )
            Text(
                text = "Cadastre-se",
                fontSize = 14.sp,
                color = Color(0xFFF4B942),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ReadiumTheme {
        LoginScreen()
    }
}