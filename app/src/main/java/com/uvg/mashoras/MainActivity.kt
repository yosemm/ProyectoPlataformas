package com.uvg.mashoras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uvg.mashoras.navigation.AppNavigation
import com.uvg.mashoras.navigation.AppScreens
import com.uvg.mashoras.ui.components.BottomNavBar
import com.uvg.mashoras.ui.components.CenteredTopBar
import com.uvg.mashoras.ui.theme.MasHorasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasHorasTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenteredTopBar()
    }, bottomBar = {
        if (currentRoute in listOf(
                AppScreens.AvailableActivitiesScreen.route,
                AppScreens.HistoryScreen.route,
                AppScreens.ProfileScreen.route
            )
        ) {
            BottomNavBar(navController)
        }
    }) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}
