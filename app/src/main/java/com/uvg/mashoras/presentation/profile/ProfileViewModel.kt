package com.uvg.mashoras.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.data.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
)

class ProfileViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUiState(
                isLoading = false,
                user = null,
                error = "Usuario no autenticado",
            )
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (!doc.exists()) {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        user = null,
                        error = "No se encontraron datos de usuario",
                    )
                    return@launch
                }

                val user = User(
                    uid = doc.getString("uid") ?: uid,
                    nombre = doc.getString("nombre") ?: "",
                    apellido = doc.getString("apellido") ?: "",
                    correo = doc.getString("correo") ?: auth.currentUser?.email.orEmpty(),
                    meta = (doc.getLong("meta") ?: 0L).toInt(),
                    avance = (doc.getLong("avance") ?: 0L).toInt(),
                    rol = when (doc.getString("rol")) {
                        UserRole.MAESTRO.name -> UserRole.MAESTRO
                        else -> UserRole.ESTUDIANTE
                    },
                    carrera = doc.getString("carrera") ?: "",
                    actividadesRealizadas = (doc.get("actividadesRealizadas") as? List<String>).orEmpty()
                )

                _uiState.value = ProfileUiState(
                    isLoading = false,
                    user = user,
                    error = null,
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    user = null,
                    error = e.message ?: "Error al cargar el perfil",
                )
            }
        }
    }

    fun updateMeta(newMeta: Int) {
        if (newMeta <= 0) return
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid)
                    .update("meta", newMeta)
                    .await()

                _uiState.update { state ->
                    state.copy(user = state.user?.copy(meta = newMeta))
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "No se pudo actualizar la meta")
                }
            }
        }
    }
}
