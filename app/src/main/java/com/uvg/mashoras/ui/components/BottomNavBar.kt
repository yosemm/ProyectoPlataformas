package com.uvg.mashoras.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.uvg.mashoras.navigation.AppScreens
import kotlin.collections.forEachIndexed
import kotlin.let

data class NavItem(
    val icon: ImageVector, val contentDescription: String, val route: String
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem(
            icon = Icons.Filled.Schedule,
            contentDescription = "Historial",
            route = AppScreens.HistoryScreen.route
        ), NavItem(
            icon = Icons.Filled.Home,
            contentDescription = "Actividades",
            route = AppScreens.AvailableActivitiesScreen.route
        ), NavItem(
            icon = Icons.Filled.Person,
            contentDescription = "Perfil",
            route = AppScreens.ProfileScreen.route
        )
    )

    NavigationBar(
        modifier = Modifier.drawBehind {
            val stroke = 1.dp.toPx()
            drawLine(
                color = Color.Gray,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = stroke
            )
        }, containerColor = Color.White, tonalElevation = 0.dp

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEachIndexed { index, item ->
            AddItem(
                item = item, isSelected = currentRoute == item.route, onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

@Composable
fun RowScope.AddItem(
    item: NavItem, isSelected: Boolean, onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
            )
        }, selected = isSelected, onClick = onClick, colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colorScheme.primary,
            unselectedIconColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}

