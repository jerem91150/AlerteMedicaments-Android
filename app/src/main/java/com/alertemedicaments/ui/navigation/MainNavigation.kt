package com.alertemedicaments.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alertemedicaments.ui.screens.*
import com.alertemedicaments.ui.theme.Teal600

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Search : Screen("search", "Recherche", Icons.Filled.Search, Icons.Outlined.Search)
    object Alerts : Screen("alerts", "Alertes", Icons.Filled.Notifications, Icons.Outlined.Notifications)
    object Map : Screen("map", "Pharmacies", Icons.Filled.LocalPharmacy, Icons.Outlined.LocalPharmacy)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
    object Auth : Screen("auth", "Connexion", Icons.Filled.Login, Icons.Outlined.Login)
    object Ocr : Screen("ocr", "Scanner", Icons.Filled.DocumentScanner, Icons.Outlined.DocumentScanner)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Search, Screen.Alerts, Screen.Map, Screen.Profile)

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp),
                color = Color.White
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Gray,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    screens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(if (selected) 26.dp else 24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Teal600,
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF),
                                indicatorColor = Teal600
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateToOcr = {
                        navController.navigate(Screen.Ocr.route)
                    }
                )
            }
            composable(Screen.Alerts.route) {
                AlertsScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Ocr.route) {
                OcrScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onMedicationClick = { medication ->
                        // Navigate to search with the medication name
                        navController.navigate(Screen.Search.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
