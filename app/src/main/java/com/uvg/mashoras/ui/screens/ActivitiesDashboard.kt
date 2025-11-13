package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uvg.mashoras.MasHorasApp
import com.uvg.mashoras.domain.model.ActivityItem
import com.uvg.mashoras.presentation.activities.ActivitiesUiState
import com.uvg.mashoras.presentation.activities.ActivitiesViewModel
import com.uvg.mashoras.presentation.activities.ActivitiesViewModelFactory
import com.uvg.mashoras.ui.components.StudentProgressHeader


@Composable
fun ActivitiesDashboard() {
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp
    val viewModel: ActivitiesViewModel =
        viewModel(factory = ActivitiesViewModelFactory(app.container.activitiesRepository))
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is ActivitiesUiState.Loading -> LoadingState()
        is ActivitiesUiState.Error -> ErrorState(s.message) { viewModel.refresh() }
        is ActivitiesUiState.Success -> SuccessState(s.data) { viewModel.refresh() }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $message")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Reintentar") }
        }
    }
}

@Composable
fun SuccessState(data: List<ActivityItem>, onRefresh: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onRefresh,
                icon = { Icon(Icons.Default.Refresh, contentDescription = "Actualizar") },
                text = { Text("Actualizar") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header de progreso del estudiante (solo se muestra si el rol es ESTUDIANTE)
            item {
                StudentProgressHeader()
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(data) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            activity.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(activity.description)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Horas: ${activity.hours}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
