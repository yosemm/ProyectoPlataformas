package com.uvg.mashoras.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.presentation.profile.ProfileViewModel
import com.uvg.mashoras.presentation.profile.ProfileViewModelFactory

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.ui.Modifier


@Composable
fun StudentProgressHeader() {
    // Reutilizamos el mismo ViewModel del perfil
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
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
