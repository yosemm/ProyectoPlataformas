package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uvg.mashoras.MasHorasApp
import com.uvg.mashoras.domain.model.ActivityItem
import com.uvg.mashoras.presentation.activities.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh


@Composable
fun ActivitiesDashboard() {
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp
    val viewModel: ActivitiesViewModel = viewModel(factory = ActivitiesViewModelFactory(app.container.activitiesRepository))
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                .padding(16.dp)
        ) {
            items(data) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(activity.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(activity.description)
                        Spacer(Modifier.height(4.dp))
                        Text("Horas: ${activity.hours}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
