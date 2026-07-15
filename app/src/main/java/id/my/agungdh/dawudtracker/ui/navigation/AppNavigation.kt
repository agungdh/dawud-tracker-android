package id.my.agungdh.dawudtracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.my.agungdh.dawudtracker.R
import id.my.agungdh.dawudtracker.ui.screen.home.HomeScreen
import id.my.agungdh.dawudtracker.ui.screen.profile.ProfileScreen
import id.my.agungdh.dawudtracker.ui.screen.settings.SettingsScreen

sealed class Screen(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    data object Profile : Screen("profile", R.string.nav_profile, Icons.Default.Person)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Profile, Screen.Settings)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
