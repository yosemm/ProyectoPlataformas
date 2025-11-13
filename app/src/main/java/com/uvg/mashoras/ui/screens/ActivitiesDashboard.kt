package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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
import com.uvg.mashoras.ui.components.AddActivityDialog
import com.uvg.mashoras.ui.components.StudentProgressHeader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivitiesDashboard() {
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp

    // ViewModel de actividades
    val activitiesViewModel: ActivitiesViewModel = viewModel(
        factory = ActivitiesViewModelFactory(
            app.container.activitiesRepository,
            FirebaseAuth.getInstance()
        )
    )

    // ViewModel de perfil para obtener el rol del usuario
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    )

    val activitiesState by activitiesViewModel.state.collectAsState()
    val createActivityState by activitiesViewModel.createActivityState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    // Actualizar el rol del usuario en el ViewModel de actividades
    LaunchedEffect(profileState.user?.rol) {
        profileState.user?.rol?.let { role ->
            activitiesViewModel.setUserRole(role)
        }
    }

    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showEditActivityDialog by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    // Manejar el éxito de la creación de actividad
    LaunchedEffect(createActivityState.success) {
        if (createActivityState.success) {
            showAddActivityDialog = false
            activitiesViewModel.resetCreateActivityState()
        }
    }

    when (val state = activitiesState) {
        is ActivitiesUiState.Loading -> {
            LoadingState()
        }
        is ActivitiesUiState.Error -> {
            ErrorState(state.message) { 
                activitiesViewModel.refreshActivities() 
            }
        }
        is ActivitiesUiState.Success -> {
            SuccessState(
                activities = state.activities,
                userRole = state.userRole,
                currentUserUid = currentUserUid,
                onActivityClick = { activity ->
                    selectedActivity = activity
                },
                onAddActivityClick = {
                    showAddActivityDialog = true
                }
            )
        }
    }

    // Modal de detalles de actividad
    selectedActivity?.let { activity ->
        val state = activitiesState as? ActivitiesUiState.Success
        val userRole = state?.userRole ?: UserRole.ESTUDIANTE
        val isEnrolled = currentUserUid?.let { activity.estudiantesInscritos.contains(it) } ?: false

        ActivityDetailDialog(
            activity = activity,
            userRole = userRole,
            currentUserUid = currentUserUid,
            isEnrolled = isEnrolled,
            onDismiss = { selectedActivity = null },
            onEnroll = {
                activitiesViewModel.enrollInActivity(activity.id)
            },
            onUnenroll = {
                activitiesViewModel.unenrollFromActivity(activity.id)
            },
            onMarkCompleted = {
                activitiesViewModel.markActivityAsCompleted(activity.id)
            },
            onDelete = {
                activitiesViewModel.deleteActivity(activity.id)
            },
            onEdit = {
                showEditActivityDialog = true
            }
        )
    }

    // Modal de agregar actividad
    if (showAddActivityDialog) {
        AddActivityDialog(
            onDismiss = {
                showAddActivityDialog = false
                activitiesViewModel.resetCreateActivityState()
            },
            onConfirm = { titulo, descripcion, fecha, cupos, carrera, horas ->
                activitiesViewModel.createActivity(
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    cupos = cupos,
                    carrera = carrera,
                    horasARealizar = horas
                )
            },
            isLoading = createActivityState.isLoading
        )

        // Mostrar error si hay alguno
        createActivityState.error?.let { error ->
            AlertDialog(
                onDismissRequest = { activitiesViewModel.resetCreateActivityState() },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    Button(onClick = { activitiesViewModel.resetCreateActivityState() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    // Modal de editar actividad (reutilizamos AddActivityDialog pero con datos precargados)
    if (showEditActivityDialog && selectedActivity != null) {
        // TODO: Implementar EditActivityDialog similar a AddActivityDialog
        // Por ahora cerramos el modal
        showEditActivityDialog = false
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $message")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { 
                Text("Reintentar") 
            }
        }
    }
}

@Composable
private fun SuccessState(
    activities: List<Activity>,
    userRole: UserRole,
    currentUserUid: String?,
    onActivityClick: (Activity) -> Unit,
    onAddActivityClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            // Mostrar botón de agregar solo si es maestro
            if (userRole == UserRole.MAESTRO) {
                FloatingActionButton(
                    onClick = onAddActivityClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar actividad",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Header de progreso del estudiante (solo se muestra si el rol es ESTUDIANTE)
            if (userRole == UserRole.ESTUDIANTE) {
                item {
                    StudentProgressHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Text(
                    text = if (userRole == UserRole.MAESTRO) 
                        "Gestionar Actividades" 
                    else 
                        "Actividades Disponibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Filtrar actividades según el rol
            val filteredActivities = if (userRole == UserRole.MAESTRO) {
                // Maestros ven todas las actividades activas
                activities.filter { !it.finalizado }
            } else {
                // Estudiantes ven solo actividades activas
                activities.filter { !it.finalizado }
            }

            if (filteredActivities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userRole == UserRole.MAESTRO)
                                "No hay actividades creadas.\n¡Crea la primera!"
                            else
                                "No hay actividades disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(filteredActivities) { activity ->
                    ActivityCard(
                        activity = activity,
                        userRole = userRole,
                        currentUserUid = currentUserUid,
                        onClick = { onActivityClick(activity) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: Activity,
    userRole: UserRole,
    currentUserUid: String?,
    onClick: () -> Unit
) {
    val isEnrolled = currentUserUid?.let { activity.estudiantesInscritos.contains(it) } ?: false
    val isFull = activity.estudiantesInscritos.size >= activity.cupos
    val isCreator = currentUserUid == activity.creadoPor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                if (activity.finalizado) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Finalizada",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (isEnrolled) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Inscrito",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (isFull) {
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Llena",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Descripción (truncada)
            Text(
                text = activity.descripcion.take(100) + if (activity.descripcion.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            // Información de la actividad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    InfoChip(
                        label = "Fecha",
                        value = activity.fecha?.let { formatDate(it.toDate()) } ?: "Sin fecha"
                    )
                    Spacer(Modifier.height(4.dp))
                    InfoChip(
                        label = "Horas",
                        value = "${activity.horasARealizar}h"
                    )
                }
                
                Column {
                    InfoChip(
                        label = "Cupos",
                        value = "${activity.estudiantesInscritos.size}/${activity.cupos}"
                    )
                    Spacer(Modifier.height(4.dp))
                    InfoChip(
                        label = "Carrera",
                        value = activity.carrera
                    )
                }
            }

            if (userRole == UserRole.MAESTRO && isCreator) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Creada por ti",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 14.sp
        )
    }
}

private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}