package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uvg.mashoras.navigation.AppScreens
import com.uvg.mashoras.ui.theme.AppTypography

@Composable
fun WelcomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "¡Bienvenido!",
            fontSize = 62.sp,
            fontWeight = FontWeight.W900,
            color = colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Más horas, más fácil...",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 50.dp, bottom = 60.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = AppTypography.headlineLarge
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { navController.navigate(AppScreens.LoginScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary, contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Iniciar sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(AppScreens.RegisterScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, contentColor = colorScheme.primary
                    ),
                    border = BorderStroke(2.dp, colorScheme.primary),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Crear cuenta", fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 👇 BOTÓN FUNCIONAL DE TÉRMINOS Y CONDICIONES
        TextButton(
            onClick = { 
                navController.navigate(AppScreens.TermsAndConditionsScreen.route) 
            }
        ) {
            Text(
                text = "Términos y condiciones de uso",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "De UVGenios\npara UVGenios",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            color = Color.Gray
        )
    }
}