package com.example.readium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readium.navigation.ReadiumNavigation
import com.example.readium.ui.theme.ReadiumTheme
import com.example.readium.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            ReadiumTheme {
                val authViewModel: AuthViewModel = viewModel()
                ReadiumNavigation(authViewModel = authViewModel)
            }
        }
    }
}