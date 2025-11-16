package com.uvg.mashoras.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.presentation.profile.ProfileViewModel
import com.uvg.mashoras.presentation.profile.ProfileViewModelFactory

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.uvg.mashoras.MasHorasApp


@Composable
fun StudentProgressHeader() {
    // Obtenemos el contexto y el AppContainer
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp
    
    // Reutilizamos el mismo ViewModel del perfil PERO con UserRepository
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            app.container.userRepository, // ← Ahora usa UserRepository con tiempo real
        )
    )
    val state by viewModel.uiState.collectAsState()
    val user = state.user

    // Si no hay usuario o no es estudiante, no mostramos nada
    if (user == null || user.rol != UserRole.ESTUDIANTE) {
        return
    }

    val goal = user.meta
    if (goal <= 0) {
        // Si no tiene meta configurada aún, no mostramos el progreso
        return
    }

    val current = user.avance
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ProgressSection(
            progress = progress,
            progressPercentage = percentage,
            currentHours = current,
            goalHours = goal
        )
    }
}