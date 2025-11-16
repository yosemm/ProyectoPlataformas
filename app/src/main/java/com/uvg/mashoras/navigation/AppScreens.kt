package com.uvg.mashoras.navigation

sealed class AppScreens(val route: String) {
    object WelcomeScreen : AppScreens("welcome_screen")
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object AvailableActivitiesScreen : AppScreens("available_activities_screen")
    object HistoryScreen : AppScreens("history_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object TermsAndConditionsScreen : AppScreens("terms_and_conditions_screen") 
}