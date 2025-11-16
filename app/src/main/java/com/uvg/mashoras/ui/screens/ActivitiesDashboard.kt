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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
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
import com.uvg.mashoras.ui.components.EditActivityDialog
import com.uvg.mashoras.ui.components.StudentProgressHeader
import com.uvg.mashoras.ui.components.SearchAndFilterBar
import com.uvg.mashoras.ui.components.ActivityFilter
import com.uvg.mashoras.ui.components.ActivitySort
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

    // ViewModel de perfil para obtener el rol y carrera del usuario
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            FirebaseAuth.getInstance(),
            app.container.userRepository,
        )
    )

    val activitiesState by activitiesViewModel.state.collectAsState()
    val createActivityState by activitiesViewModel.createActivityState.collectAsState()
    val updateActivityState by activitiesViewModel.updateActivityState.collectAsState()
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

    // Manejar el éxito de la actualización de actividad
    LaunchedEffect(updateActivityState.success) {
        if (updateActivityState.success) {
            showEditActivityDialog = false
            selectedActivity = null
            activitiesViewModel.resetUpdateActivityState()
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
                userCareer = profileState.user?.carrera,
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
        val isEnrolled =
            currentUserUid?.let { activity.estudiantesInscritos.contains(it) } ?: false

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

    // Modal de editar actividad
    if (showEditActivityDialog && selectedActivity != null) {
        EditActivityDialog(
            activity = selectedActivity!!,
            onDismiss = {
                showEditActivityDialog = false
                activitiesViewModel.resetUpdateActivityState()
            },
            onConfirm = { activityId, titulo, descripcion, fecha, cupos, carrera, horas ->
                activitiesViewModel.updateActivity(
                    activityId = activityId,
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    cupos = cupos,
                    carrera = carrera,
                    horasARealizar = horas
                )
            },
            isLoading = updateActivityState.isLoading
        )

        // Mostrar error si hay alguno
        updateActivityState.error?.let { error ->
            AlertDialog(
                onDismissRequest = { activitiesViewModel.resetUpdateActivityState() },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    Button(onClick = { activitiesViewModel.resetUpdateActivityState() }) {
                        Text("OK")
                    }
                }
            )
        }
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
    userCareer: String?,
    currentUserUid: String?,
    onActivityClick: (Activity) -> Unit,
    onAddActivityClick: () -> Unit
) {
    // Estado de búsqueda, filtro y orden
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(ActivityFilter.ALL) }
    var selectedSort by rememberSaveable { mutableStateOf(ActivitySort.NEWEST) }

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

            // Header de progreso (solo estudiantes)
            item {
                if (userRole == UserRole.ESTUDIANTE) {
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

            // Barra de búsqueda y filtros
            item {
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    selectedSort = selectedSort,
                    onSortChange = { selectedSort = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Filtrado base según rol y carrera
            val baseActivities = when (userRole) {
                UserRole.MAESTRO -> {
                    // Maestros ven todas las actividades activas que crearon
                    activities.filter { !it.finalizado }
                }

                UserRole.ESTUDIANTE -> {
                    // Estudiantes ven solo actividades activas que:
                    // 1. Sean para "Todas" las carreras, O
                    // 2. Sean para su carrera específica
                    activities.filter { activity ->
                        !activity.finalizado && (
                            activity.carrera.equals("Todas", ignoreCase = true) ||
                                activity.carrera.equals(userCareer, ignoreCase = true)
                            )
                    }
                }
            }

            // Si no hay actividades base, mostramos el mensaje original
            if (baseActivities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (userRole) {
                                UserRole.MAESTRO -> "No hay actividades creadas.\n¡Crea la primera!"
                                UserRole.ESTUDIANTE -> if (userCareer.isNullOrBlank()) {
                                    "Configura tu carrera en el perfil\npara ver actividades disponibles"
                                } else {
                                    "No hay actividades disponibles\npara tu carrera en este momento"
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Funciones auxiliares
                val isEnrolledFor: (Activity) -> Boolean = { activity ->
                    currentUserUid?.let { uid -> activity.estudiantesInscritos.contains(uid) } ?: false
                }
                val isFullActivity: (Activity) -> Boolean = { activity ->
                    activity.estudiantesInscritos.size >= activity.cupos
                }

                // 1. Buscar por texto
                val query = searchQuery.trim().lowercase()
                val searchedActivities = if (query.isBlank()) {
                    baseActivities
                } else {
                    baseActivities.filter { activity ->
                        activity.titulo.lowercase().contains(query) ||
                                activity.descripcion.lowercase().contains(query)
                    }
                }

                // 2. Filtrar por estado (disponible, inscrita, llena...)
                val statusFilteredActivities = when (selectedFilter) {
                    ActivityFilter.ALL -> searchedActivities
                    ActivityFilter.AVAILABLE -> searchedActivities.filter { activity ->
                        !activity.finalizado && !isFullActivity(activity)
                    }

                    ActivityFilter.ENROLLED -> searchedActivities.filter { activity ->
                        isEnrolledFor(activity)
                    }

                    ActivityFilter.FULL -> searchedActivities.filter { activity ->
                        isFullActivity(activity)
                    }
                }

                // 3. Ordenar
                val sortedActivities = when (selectedSort) {
                    ActivitySort.NEWEST -> statusFilteredActivities.sortedByDescending { activity ->
                        activity.fecha?.toDate()?.time ?: Long.MIN_VALUE
                    }

                    ActivitySort.OLDEST -> statusFilteredActivities.sortedBy { activity ->
                        activity.fecha?.toDate()?.time ?: Long.MAX_VALUE
                    }

                    ActivitySort.MORE_HOURS -> statusFilteredActivities.sortedByDescending { activity ->
                        activity.horasARealizar
                    }

                    ActivitySort.LESS_HOURS -> statusFilteredActivities.sortedBy { activity ->
                        activity.horasARealizar
                    }

                    ActivitySort.MORE_SPOTS -> statusFilteredActivities.sortedByDescending { activity ->
                        activity.cupos - activity.estudiantesInscritos.size
                    }

                    ActivitySort.LESS_SPOTS -> statusFilteredActivities.sortedBy { activity ->
                        activity.cupos - activity.estudiantesInscritos.size
                    }
                }

                if (sortedActivities.isEmpty()) {
                    // Hay actividades base, pero ninguna coincide con búsqueda/filtros
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron actividades\ncon la búsqueda o filtros actuales",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(sortedActivities) { activity ->
                        ActivityCard(
                            activity = activity,
                            userRole = userRole,
                            currentUserUid = currentUserUid,
                            onClick = { onActivityClick(activity) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
