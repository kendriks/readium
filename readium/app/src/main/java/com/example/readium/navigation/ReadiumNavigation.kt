package com.example.readium.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readium.data.model.Book
import com.example.readium.repository.BookRepository
import com.example.readium.repository.TradeRepository
import com.example.readium.ui.screens.*
import com.example.readium.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.example.readium.repository.BookClubRepository

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
    object BookClubs: Screen("book_clubs")
    object SearchBookClubs: Screen("search_book_clubs")
    object CreateThematicList : Screen("newThematicList")
    object AddBooksOnList : Screen("addBooksOnList/{nome}/{descricao}") {
        fun createRoute(nome: String, descricao: String): String {
            return "addBooksOnList/$nome/$descricao"
        }
    }
    object MyBooks : Screen("my_books")
    object AddBook : Screen("add_book")
    object SearchBook : Screen("search_book")
    object SearchTrade : Screen("search_trade")
    object EditBook : Screen("edit_book")
    object BookDetails : Screen("book_details")
    object TradeProposals : Screen("trade_proposals")
}

@Composable
fun ReadiumNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel()
) {

    val repository = remember { BookRepository() }
    val factory = remember { BooksViewModelFactory(repository) }
    val booksViewModel: BooksViewModel = viewModel(factory = factory)

    val tradeRepository = remember { TradeRepository() }
    val tradeFactory = remember { TradeViewModelFactory(tradeRepository) }
    val tradeViewModel: TradeViewModel = viewModel(factory = tradeFactory)

    // NOVO: ViewModel de Clubes (Compartilhado)
    val clubRepository = remember { BookClubRepository() }
    val clubFactory = remember { BookClubViewModelFactory(clubRepository) }
    val clubViewModel: BookClubViewModel = viewModel(factory = clubFactory)

    val authState by authViewModel.authState.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            friendsViewModel.loadFriends(it)
            booksViewModel.loadBooks(it)
        }
    }

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
                onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Screen.RegisterStepOne.route) },
                authViewModel = authViewModel
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.RegisterStepOne.route) },
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                authViewModel = authViewModel
            )
        }
        composable(Screen.RegisterStepOne.route) {
            RegisterStepOneScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStepTwo = { name, email, password, confirmPassword ->
                    val route = Screen.RegisterStepTwo.createRoute(name, email, password, confirmPassword)
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.RegisterStepTwo.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            RegisterStepTwoScreen(
                email = email,
                password = password,
                onNavigateBack = { navController.popBackStack() },
                onRegistrationComplete = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                authViewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            ReadiumHomeScreen(
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Splash.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToBookClubs = { navController.navigate(Screen.BookClubs.route) },
                onNavigateToSearchTrade = { navController.navigate(Screen.SearchTrade.route) },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Profile.route) {
            val userProfile by authViewModel.userProfile.collectAsState()
            ProfileScreen(
                booksViewModel = booksViewModel,
                userName = userProfile?.name ?: "name",
                userBio = userProfile?.biography ?: "book lover <3",
                userPhotoUrl = userProfile?.profilePhotoUrl,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Profile.route) { inclusive = true } } },
                onNavigateToFriends = { navController.navigate(Screen.Friends.route) },
                friendsViewModel = friendsViewModel,
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToCreateThematicList = { navController.navigate((Screen.CreateThematicList.route)) },
                onNavigateToMyBooks = { navController.navigate(Screen.MyBooks.route) },
                onNavigateToProposals = { navController.navigate(Screen.TradeProposals.route) }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.EditProfile.route) { inclusive = true } } },
                authViewModel = authViewModel
            )
        }
        composable(Screen.Friends.route) {
            FriendsScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Profile.route) { inclusive = true } } }, onNavigateToProfile = { navController.navigate(Screen.Profile.route) }, friendsViewModel = friendsViewModel)
        }
        composable(Screen.CreateThematicList.route) {
            CreateThematicListScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.CreateThematicList.route) { inclusive = true } } }, onNavigateToProfile = { navController.navigate(Screen.Profile.route) }, onNavigateToAddBookScreen = { nome, descricao -> val route = Screen.AddBooksOnList.createRoute(nome, descricao); navController.navigate(route) })
        }
        composable(Screen.AddBooksOnList.route) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("nome") ?: ""
            val description = backStackEntry.arguments?.getString("descricao") ?: ""
            AddBooksOnListScreen(nomeListaEncoded = name, descricaoListaEncoded = description, onNavigateBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, onNavigateToProfile = { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Profile.route) { inclusive = true } } })
        }
        composable(Screen.MyBooks.route) {
            val firebaseUser by authViewModel.user.collectAsState()
            val userId = firebaseUser?.uid.orEmpty()

            LaunchedEffect(userId) {
                if (userId.isNotBlank()) {
                    booksViewModel.loadBooks(userId)
                }
            }

            MyBooksScreen(
                viewModel = booksViewModel,
                userId = userId,
                onBookClick = { book ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("book", book)

                    navController.navigate(Screen.BookDetails.route)
                },
                onAddBookClick = {
                    navController.navigate(Screen.SearchBook.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SearchBook.route) {
            val searchBookViewModel: SearchBookViewModel = viewModel()
            SearchBookScreen(viewModel = searchBookViewModel, onBookSelected = { book -> navController.currentBackStackEntry?.savedStateHandle?.set("book", book); navController.navigate(Screen.AddBook.route) }, onNavigateBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, onNavigateToProfile = { navController.navigate(Screen.Profile.route) }, onCreateBookManually = { navController.navigate(Screen.AddBook.route) })
        }

        composable(Screen.EditBook.route) {

            val book =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Book>("book")

            if (book != null) {
                EditBookScreen(
                    book = book,
                    onSave = { updatedBook ->
                        booksViewModel.updateBook(updatedBook)
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToMyBooks = {
                        navController.navigate(Screen.MyBooks.route)
                    }
                )
            }
        }

        composable(Screen.BookDetails.route) {

            val book =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Book>("book")

            if (book != null) {
                BookDetailsScreen(
                    book = book,

                    onNavigateBack = {
                        navController.popBackStack()
                    },

                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },

                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },

                    onEditBook = { selectedBook ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("book", selectedBook)

                        navController.navigate(Screen.EditBook.route)
                    },

                    onDeleteBook = { selectedBook ->
                        booksViewModel.deleteBook(selectedBook)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.AddBook.route) {
            val firebaseUser by authViewModel.user.collectAsState()
            val userProfile by authViewModel.userProfile.collectAsState()

            val uId = firebaseUser?.uid.orEmpty()
            val userName = userProfile?.name

            // Lógica para montar a string de localização
            val userLocation = if (!userProfile?.city.isNullOrBlank() && !userProfile?.state.isNullOrBlank()) {
                "${userProfile!!.city} - ${userProfile!!.state}"
            } else {
                userProfile?.city ?: userProfile?.state
            }

            val selectedBook = navController.previousBackStackEntry?.savedStateHandle?.get<Book>("book")
            val uiState = remember { AddBookUiState(foundBook = selectedBook) }

            AddBookScreen(
                uiState = uiState,
                onSave = { book ->
                    if (uId.isNotBlank()) {
                        // Agora passamos userLocation para o ViewModel
                        booksViewModel.saveBook(
                            book = book,
                            userId = uId,
                            userName = userName,
                            userLocation = userLocation
                        )
                    }
                    navController.navigate(Screen.MyBooks.route)
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.SearchTrade.route) { SearchTradeScreen(viewModel = tradeViewModel, onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.TradeProposals.route) { TradeProposalsScreen(viewModel = tradeViewModel, onNavigateBack = { navController.popBackStack() }) }

        // --- ROTAS DO CLUBE DE LEITURA ---

        composable(Screen.CreateBookClub1.route) {
            CreateBookClubScreen1(
                viewModel = clubViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNext = { navController.navigate(Screen.CreateBookClub2.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.CreateBookClub1.route) { inclusive = true } } }
            )
        }

        composable(Screen.CreateBookClub2.route) {
            val userProfile by authViewModel.userProfile.collectAsState()
            val currentUser = FirebaseAuth.getInstance().currentUser

            CreateBookClubScreen2(
                viewModel = clubViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCreateClub = {
                    if (currentUser != null) {
                        clubViewModel.saveDraftClub(
                            ownerId = currentUser.uid,
                            ownerName = userProfile?.name ?: "Usuário",
                            onSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.CreateBookClub2.route) { inclusive = true } } }
            )
        }

        composable(Screen.BookClubs.route) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            LaunchedEffect(currentUser) {
                currentUser?.let { clubViewModel.loadUserClubs(it.uid) }
            }

            BookClubsScreen(
                viewModel = clubViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateClub = { navController.navigate(Screen.CreateBookClub1.route) },
                onNavigateToSearchClubs = { navController.navigate(Screen.SearchBookClubs.route) }
            )
        }

        composable(Screen.SearchBookClubs.route) {
            LaunchedEffect(Unit) { clubViewModel.searchPublicClubs() }
            val currentUser = FirebaseAuth.getInstance().currentUser

            SearchBookClubsScreen(
                viewModel = clubViewModel,
                currentUserId = currentUser?.uid ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}