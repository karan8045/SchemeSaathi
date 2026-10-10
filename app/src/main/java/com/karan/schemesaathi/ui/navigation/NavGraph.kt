package com.karan.schemesaathi.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.ui.alerts.AlertsScreen
import com.karan.schemesaathi.ui.alerts.AlertsViewModel
import com.karan.schemesaathi.ui.assistant.ApplicationAssistantScreen
import com.karan.schemesaathi.ui.assistant.ApplicationAssistantViewModel
import com.karan.schemesaathi.ui.auth.AuthScreen
import com.karan.schemesaathi.ui.auth.AuthViewModel
import com.karan.schemesaathi.ui.chat.AskSaathiScreen
import com.karan.schemesaathi.ui.chat.AskSaathiViewModel
import com.karan.schemesaathi.ui.detail.DetailScreen
import com.karan.schemesaathi.ui.detail.DetailViewModel
import com.karan.schemesaathi.ui.documents.DocumentManagerScreen
import com.karan.schemesaathi.ui.documents.DocumentViewModel
import com.karan.schemesaathi.ui.explore.ExploreScreen
import com.karan.schemesaathi.ui.explore.ExploreViewModel
import com.karan.schemesaathi.ui.home.HomeScreen
import com.karan.schemesaathi.ui.home.HomeViewModel
import com.karan.schemesaathi.ui.onboarding.OnboardingScreen
import com.karan.schemesaathi.ui.onboarding.OnboardingViewModel
import com.karan.schemesaathi.ui.profile.ProfileScreen
import com.karan.schemesaathi.ui.profile.ProfileViewModel
import com.karan.schemesaathi.ui.saved.SavedScreen
import com.karan.schemesaathi.ui.saved.SavedViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Auth : Screen("auth", "Auth")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Explore : Screen("explore", "Explore", Icons.Default.Explore)
    object Saved : Screen("saved", "Saved", Icons.Default.Bookmark)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Notifications)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Detail : Screen("detail/{opportunityId}", "Detail") {
        fun createRoute(opportunityId: String) = "detail/$opportunityId"
    }
    object AskSaathi : Screen("ask_saathi", "Ask Saathi")
    object ApplicationAssistant : Screen("assistant/{opportunityId}", "Assistant") {
        fun createRoute(opportunityId: String) = "assistant/$opportunityId"
    }
    object Documents : Screen("documents", "Documents")
}

@Composable
fun SchemeSaathiNavGraph(
    userPreferences: UserPreferencesRepository,
    navController: NavHostController = rememberNavController()
) {
    val userId by userPreferences.userIdFlow.collectAsState(initial = null)
    val isOnboarded by userPreferences.isOnboardedFlow.collectAsState(initial = false)

    val startDestination = when {
        userId.isNullOrBlank() -> Screen.Auth.route
        !isOnboarded -> Screen.Onboarding.route
        else -> Screen.Home.route
    }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Saved,
        Screen.Alerts,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Auth.route) {
                val viewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        if (isOnboarded) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                val viewModel: OnboardingViewModel = hiltViewModel()
                OnboardingScreen(
                    viewModel = viewModel,
                    onOnboardingSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val exploreViewModel: ExploreViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onOpportunityClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                    onExploreCategoryClick = { cat ->
                        exploreViewModel.selectCategory(cat)
                        navController.navigate(Screen.Explore.route)
                    },
                    onAskSaathiClick = { navController.navigate(Screen.AskSaathi.route) },
                    onAlertsClick = { navController.navigate(Screen.Alerts.route) }
                )
            }

            composable(Screen.Explore.route) {
                val viewModel: ExploreViewModel = hiltViewModel()
                ExploreScreen(
                    viewModel = viewModel,
                    onOpportunityClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
                )
            }

            composable(Screen.Saved.route) {
                val viewModel: SavedViewModel = hiltViewModel()
                SavedScreen(
                    viewModel = viewModel,
                    onOpportunityClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
                )
            }

            composable(Screen.Alerts.route) {
                val viewModel: AlertsViewModel = hiltViewModel()
                AlertsScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("opportunityId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("opportunityId") ?: ""
                val viewModel: DetailViewModel = hiltViewModel()
                DetailScreen(
                    opportunityId = id,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onStartApplicationAssistant = { oppId ->
                        navController.navigate(Screen.ApplicationAssistant.createRoute(oppId))
                    }
                )
            }

            composable(Screen.AskSaathi.route) {
                val viewModel: AskSaathiViewModel = hiltViewModel()
                AskSaathiScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ApplicationAssistant.route,
                arguments = listOf(navArgument("opportunityId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("opportunityId") ?: ""
                val viewModel: ApplicationAssistantViewModel = hiltViewModel()
                ApplicationAssistantScreen(
                    opportunityId = id,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Documents.route) {
                val viewModel: DocumentViewModel = hiltViewModel()
                DocumentManagerScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
