package com.uvg.mashoras.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
)

class ProfileViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUser()
    }

    /**
     * Observa los cambios del usuario en tiempo real
     */
    private fun observeUser() {
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
                userRepository.observeUser(uid).collect { user ->
                    if (user == null) {
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            user = null,
                            error = "No se encontraron datos de usuario",
                        )
                    } else {
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            user = user,
                            error = null,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    user = null,
                    error = e.message ?: "Error al cargar el perfil",
                )
            }
        }
    }

    /**
     * Actualiza la meta del usuario
     */
    fun updateMeta(newMeta: Int) {
        if (newMeta <= 0) return
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                userRepository.updateMeta(uid, newMeta)
                // No necesitamos actualizar el estado manualmente,
                // el observeUser() lo hará automáticamente
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "No se pudo actualizar la meta")
                }
            }
        }
    }
    
    /**
     * Recarga el usuario (aunque no es necesario con tiempo real)
     */
    fun loadUser() {
        // Este método se mantiene por compatibilidad pero ya no es necesario
        // porque observeUser() ya está corriendo
    }
}