package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uvg.mashoras.navigation.AppScreens

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentHours: Int = 75,
    goalHours: Int = 100
) {
    val progress = (currentHours.toFloat() / goalHours.toFloat()).coerceIn(0f, 1f)
    val progressPercentage = (progress * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }

        item {
            ProgressSection(
                progress = progress,
                progressPercentage = progressPercentage,
                currentHours = currentHours,
                goalHours = goalHours
            )
        }

        item {
            Text(
                text = "Perfil",
                fontSize = 32.sp,
                fontWeight = FontWeight.W900,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            ProfileCard()
        }

        item {
            LogoutCard(onLogoutClicked = {
                navController.navigate(AppScreens.WelcomeScreen.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(0.7f),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Ícono de Perfil",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier.weight(0.5f),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Juanito",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estudiante",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "jua25555",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoutCard(onLogoutClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(0.7f),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onLogoutClicked,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, contentColor = colorScheme.error
                ),
                border = BorderStroke(2.dp, colorScheme.error),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.W900
                )
            }
        }
    }
}

