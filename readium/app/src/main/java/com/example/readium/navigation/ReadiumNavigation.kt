package com.example.readium.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readium.ui.screens.SplashScreen
import com.example.readium.ui.screens.LoginScreen
import com.example.readium.ui.screens.RegisterStepOneScreen
import com.example.readium.ui.screens.RegisterStepTwoScreen
import com.example.readium.ui.screens.ReadiumHomeScreen
import com.example.readium.ui.screens.ProfileScreen
import com.example.readium.ui.screens.EditProfileScreen
import com.example.readium.ui.screens.CreateBookClubScreen1
import com.example.readium.ui.screens.CreateBookClubScreen2
import com.example.readium.ui.screens.FriendsScreen
import com.example.readium.viewmodel.AuthViewModel
import com.example.readium.viewmodel.AuthState

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object RegisterStepOne : Screen("register_step_one")
    object RegisterStepTwo : Screen("register_step_two/{name}/{email}/{password}/{confirmPassword}") {
        fun createRoute(name: String, email: String, password: String, confirmPassword: String): String {
            return "register_step_two/$name/$email/$password/$confirmPassword"
        }
    }
    object Home : Screen("home")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object CreateBookClub1 : Screen("create_book_club_1")
    object CreateBookClub2 : Screen("create_book_club_2")
    object Friends : Screen("friends")

}

@Composable
fun ReadiumNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    //determina rota inicial baseada no estado de autenticação
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Home.route
        else -> Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterStepOne.route)
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterStepOne.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.RegisterStepOne.route) {
            RegisterStepOneScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStepTwo = { name, email, password, confirmPassword ->
                    val route = Screen.RegisterStepTwo.createRoute(name, email, password, confirmPassword)
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.RegisterStepTwo.route) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            val confirmPassword = backStackEntry.arguments?.getString("confirmPassword") ?: ""

            RegisterStepTwoScreen(
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            ReadiumHomeScreen(
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToCreateClub = {
                    navController.navigate(Screen.CreateBookClub1.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToFriends = {
                    navController.navigate(Screen.Friends.route)
                }
            )
        }
        composable(Screen.Friends.route) {
            FriendsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.EditProfile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateBookClub1.route) {
            CreateBookClubScreen1(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNext = {
                    navController.navigate(Screen.CreateBookClub2.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CreateBookClub1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateBookClub2.route) {
            CreateBookClubScreen2(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateClub = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CreateBookClub2.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CreateBookClub2.route) { inclusive = true }
                    }
                }
            )
        }

    }
}