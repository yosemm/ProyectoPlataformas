package com.uvg.mashoras.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.ui.register.RegisterRepository
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
            val repository = RegisterRepository(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance()
            )
            
            RegisterScreen(
                onRegister = { email, password, career ->
                    repository.register(email, password, career)
                },
                onSuccessNavigate = {
                    navController.navigate(AppScreens.AvailableActivitiesScreen.route) {
                        popUpTo(AppScreens.WelcomeScreen.route) { inclusive = true }
                    }
                }
            )
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