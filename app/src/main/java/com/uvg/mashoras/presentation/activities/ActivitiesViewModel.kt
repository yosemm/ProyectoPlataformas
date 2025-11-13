package com.uvg.mashoras.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.data.models.Activity
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ActivitiesUiState {
    object Loading : ActivitiesUiState
    data class Success(val activities: List<Activity>, val userRole: UserRole) : ActivitiesUiState
    data class Error(val message: String) : ActivitiesUiState
}

data class CreateActivityState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class ActivitiesViewModel(
    private val repository: ActivitiesRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Loading)
    val state: StateFlow<ActivitiesUiState> = _state.asStateFlow()

    private val _createActivityState = MutableStateFlow(CreateActivityState())
    val createActivityState: StateFlow<CreateActivityState> = _createActivityState.asStateFlow()

    private val _userRole = MutableStateFlow<UserRole>(UserRole.ESTUDIANTE)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    init {
        observeActivities()
        refreshActivities()
    }

    private fun observeActivities() {
        viewModelScope.launch {
            repository.observeActivities()
                .catch { e -> 
                    _state.value = ActivitiesUiState.Error(e.message ?: "Error al cargar actividades") 
                }
                .collect { activities ->
                    _state.value = ActivitiesUiState.Success(activities, _userRole.value)
                }
        }
    }

    fun refreshActivities() {
        viewModelScope.launch {
            try {
                repository.refreshActivities()
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(e.message ?: "Error de red")
            }
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
        val currentState = _state.value
        if (currentState is ActivitiesUiState.Success) {
            _state.value = currentState.copy(userRole = role)
        }
    }

    /**
     * Crea una nueva actividad
     */
    fun createActivity(
        titulo: String,
        descripcion: String,
        fecha: Timestamp,
        cupos: Int,
        carrera: String,
        horasARealizar: Int
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _createActivityState.value = CreateActivityState(
                error = "Usuario no autenticado"
            )
            return
        }

        viewModelScope.launch {
            _createActivityState.value = CreateActivityState(isLoading = true)
            
            try {
                val activity = Activity(
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    cupos = cupos,
                    carrera = carrera,
                    finalizado = false,
                    horasARealizar = horasARealizar,
                    estudiantesInscritos = emptyList(),
                    creadoPor = currentUser.uid
                )
                
                repository.createActivity(activity, currentUser.uid)
                
                _createActivityState.value = CreateActivityState(success = true)
            } catch (e: Exception) {
                _createActivityState.value = CreateActivityState(
                    error = e.message ?: "Error al crear actividad"
                )
            }
        }
    }

    /**
     * Reinicia el estado de creación de actividad
     */
    fun resetCreateActivityState() {
        _createActivityState.value = CreateActivityState()
    }

    /**
     * Inscribe a un estudiante en una actividad
     */
    fun enrollInActivity(activityId: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _state.value = ActivitiesUiState.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            try {
                repository.enrollStudent(activityId, currentUser.uid)
                // El repositorio ya actualiza la lista automáticamente
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(
                    e.message ?: "Error al inscribirse en la actividad"
                )
            }
        }
    }

    /**
     * Desinscribe a un estudiante de una actividad
     */
    fun unenrollFromActivity(activityId: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _state.value = ActivitiesUiState.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            try {
                repository.unenrollStudent(activityId, currentUser.uid)
                // El repositorio ya actualiza la lista automáticamente
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(
                    e.message ?: "Error al desinscribirse de la actividad"
                )
            }
        }
    }

    /**
     * Marca una actividad como completada (solo maestros)
     */
    fun markActivityAsCompleted(activityId: String) {
        viewModelScope.launch {
            try {
                repository.markActivityAsCompleted(activityId)
                // El repositorio ya actualiza la lista automáticamente
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(
                    e.message ?: "Error al marcar actividad como completada"
                )
            }
        }
    }

    /**
     * Elimina una actividad (solo maestros)
     */
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            try {
                repository.deleteActivity(activityId)
                // El repositorio ya actualiza la lista automáticamente
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(
                    e.message ?: "Error al eliminar actividad"
                )
            }
        }
    }
}