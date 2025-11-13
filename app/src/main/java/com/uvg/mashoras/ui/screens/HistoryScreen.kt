package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.MasHorasApp
import com.uvg.mashoras.data.models.Activity
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.presentation.activities.ActivitiesUiState
import com.uvg.mashoras.presentation.activities.ActivitiesViewModel
import com.uvg.mashoras.presentation.activities.ActivitiesViewModelFactory
import com.uvg.mashoras.presentation.profile.ProfileViewModel
import com.uvg.mashoras.presentation.profile.ProfileViewModelFactory
import com.uvg.mashoras.ui.components.ActivityDetailDialog
import com.uvg.mashoras.ui.components.StudentProgressHeader
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp

    // ViewModel de actividades para cargar todas las actividades
    val activitiesViewModel: ActivitiesViewModel = viewModel(
        factory = ActivitiesViewModelFactory(
            app.container.activitiesRepository,
            FirebaseAuth.getInstance()
        )
    )

    // ViewModel de perfil para obtener el rol y las actividades realizadas del usuario
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    )

    val activitiesState by activitiesViewModel.state.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    var selectedActivity by remember { mutableStateOf<Activity?>(null) }

    // Actualizar el rol del usuario en el ViewModel de actividades
    LaunchedEffect(profileState.user?.rol) {
        profileState.user?.rol?.let { role ->
            activitiesViewModel.setUserRole(role)
        }
    }

    when {
        profileState.isLoading || activitiesState is ActivitiesUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        profileState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${profileState.error}",
                    color = Color.Red
                )
            }
        }
        
        activitiesState is ActivitiesUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${(activitiesState as ActivitiesUiState.Error).message}",
                    color = Color.Red
                )
            }
        }
        
        else -> {
            val user = profileState.user
            val userRole = user?.rol ?: UserRole.ESTUDIANTE
            val allActivities = (activitiesState as? ActivitiesUiState.Success)?.activities ?: emptyList()
            
            // Filtrar las actividades completadas
            val completedActivities = if (userRole == UserRole.ESTUDIANTE) {
                // Estudiantes: mostrar actividades finalizadas donde está inscrito
                allActivities.filter { activity ->
                    activity.finalizado && currentUserUid?.let { activity.estudiantesInscritos.contains(it) } == true
                }
            } else {
                // Maestros: mostrar actividades finalizadas creadas por él
                allActivities.filter { activity ->
                    activity.finalizado && activity.creadoPor == currentUserUid
                }
            }

            HistoryContent(
                modifier = modifier,
                userRole = userRole,
                completedActivities = completedActivities,
                onActivityClick = { activity ->
                    selectedActivity = activity
                }
            )
        }
    }

    // Modal de detalles de actividad
    selectedActivity?.let { activity ->
        val user = profileState.user
        val userRole = user?.rol ?: UserRole.ESTUDIANTE
        val isEnrolled = currentUserUid?.let { activity.estudiantesInscritos.contains(it) } ?: false

        ActivityDetailDialog(
            activity = activity,
            userRole = userRole,
            currentUserUid = currentUserUid,
            isEnrolled = isEnrolled,
            onDismiss = { selectedActivity = null },
            onEnroll = { /* No se puede inscribir en actividades finalizadas */ },
            onUnenroll = { /* No se puede desinscribir de actividades finalizadas */ },
            onMarkCompleted = { /* Ya está completada */ },
            onDelete = {
                activitiesViewModel.deleteActivity(activity.id)
                selectedActivity = null
            },
            onEdit = { /* TODO: Implementar edición */ }
        )
    }
}

@Composable
private fun HistoryContent(
    modifier: Modifier = Modifier,
    userRole: UserRole,
    completedActivities: List<Activity>,
    onActivityClick: (Activity) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(19.dp))
        }

        // Header de progreso (solo estudiante, se encarga StudentProgressHeader)
        if (userRole == UserRole.ESTUDIANTE) {
            item {
                StudentProgressHeader()
            }
        }

        item {
            Text(
                text = "Historial de actividades",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (completedActivities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (userRole == UserRole.MAESTRO)
                            "No has finalizado ninguna actividad aún"
                        else
                            "No has completado ninguna actividad aún",
                        // ✅ CORRECTO
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(completedActivities) { activity ->
                CompletedActivityCard(
                    activity = activity,
                    onClick = { onActivityClick(activity) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CompletedActivityCard(
    activity: Activity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Fecha: ${formatDate(activity.fecha?.toDate())}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Mostrar número de estudiantes inscritos
                if (activity.estudiantesInscritos.isNotEmpty()) {
                    Text(
                        text = "Estudiantes: ${activity.estudiantesInscritos.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Badge de finalizada
                Surface(
                    color = colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Finalizada",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(70.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+${activity.horasARealizar}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "horas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return "Sin fecha"
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}