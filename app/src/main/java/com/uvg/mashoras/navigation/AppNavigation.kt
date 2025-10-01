package com.uvg.mashoras.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uvg.mashoras.ui.screens.ActivitiesDashboard
import com.uvg.mashoras.ui.screens.HistoryScreen
import com.uvg.mashoras.ui.screens.LoginScreen
import com.uvg.mashoras.ui.screens.ProfileScreen
import com.uvg.mashoras.ui.screens.RegisterScreen
import com.uvg.mashoras.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.WelcomeScreen.route,
        modifier = modifier
    ) {
        composable(AppScreens.WelcomeScreen.route) {
            WelcomeScreen(navController)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }
        composable(AppScreens.AvailableActivitiesScreen.route) {
            ActivitiesDashboard()
        }
        composable(AppScreens.HistoryScreen.route) {
            HistoryScreen()
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
    }
}
