package com.uvg.mashoras.ui.screens

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.presentation.profile.ProfileViewModel
import com.uvg.mashoras.presentation.profile.ProfileViewModelFactory
import com.uvg.mashoras.utils.EmailValidator
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.runtime.setValue

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
import com.uvg.mashoras.ui.components.ProgressSection
import androidx.compose.ui.platform.LocalContext
import com.uvg.mashoras.MasHorasApp

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // Obtenemos el contexto y el AppContainer
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp
    
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            app.container.userRepository, // ← Usa UserRepository con tiempo real
        )
    )
    val state by viewModel.uiState.collectAsState()

    val user = state.user
    val currentHours = user?.avance ?: 0
    val goalHoursRaw = user?.meta ?: 0
    val goalHours = if (goalHoursRaw > 0) goalHoursRaw else 1 // evitar división por 0
    val progress = (currentHours.toFloat() / goalHours.toFloat()).coerceIn(0f, 1f)
    val progressPercentage = (progress * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(19.dp)) }

        // 👇 Solo mostramos el progreso si es ESTUDIANTE y tiene meta > 0
        if (user?.rol == UserRole.ESTUDIANTE && goalHoursRaw > 0) {
            item {
                ProgressSection(
                    progress = progress,
                    progressPercentage = progressPercentage,
                    currentHours = currentHours,
                    goalHours = goalHoursRaw
                )
            }
        }

        item {
            Text(
                text = "Perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )
        }

        item {
            ProfileCard(user = user)
        }

        if (user?.rol == UserRole.ESTUDIANTE) {
            item {
                GoalForm(
                    currentMeta = user.meta,
                    onSaveMeta = { meta -> viewModel.updateMeta(meta) }
                )
            }
        }

        item {
            LogoutCard(
                onLogoutClicked = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AppScreens.WelcomeScreen.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        if (state.error != null) {
            item {
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun ProfileCard(user: User?) {
    Card(
        modifier = Modifier.padding(20.dp),
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
                    val nombreCompleto = when {
                        user == null -> "Usuario"
                        user.apellido.isBlank() -> user.nombre
                        else -> "${user.nombre} ${user.apellido}"
                    }

                    Text(
                        text = nombreCompleto,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val rolTexto = when (user?.rol) {
                        UserRole.MAESTRO -> "Maestro"
                        UserRole.ESTUDIANTE, null -> "Estudiante"
                    }

                    Text(
                        text = rolTexto,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    
                    val textCorreo = "${user?.correo}"
                    Text(
                        text = textCorreo,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    
                    val textCarrera = "${user?.carrera}"
                    Text(
                        text = textCarrera,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val carnet = user?.correo?.let { EmailValidator.extractCarnet(it) } ?: ""

                    if (carnet.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = carnet,
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
}



@Composable
private fun GoalForm(
    currentMeta: Int,
    onSaveMeta: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Meta de horas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            val metaInicial = if (currentMeta > 0) currentMeta.toString() else ""
            var metaTexto by remember(currentMeta) { mutableStateOf(metaInicial) }
            var errorTexto by remember { mutableStateOf<String?>(null) }

            OutlinedTextField(
                value = metaTexto,
                onValueChange = {
                    metaTexto = it
                    errorTexto = null
                },
                label = { Text("Ingresa tu meta de horas") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorTexto != null) {
                Text(
                    text = errorTexto ?: "",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val valor = metaTexto.toIntOrNull()
                    if (valor == null || valor <= 0) {
                        errorTexto = "Ingresa un número válido mayor que 0"
                    } else {
                        onSaveMeta(valor)
                    }
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = if (currentMeta > 0) "Actualizar meta" else "Guardar meta",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
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