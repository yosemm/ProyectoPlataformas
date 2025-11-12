package com.uvg.mashoras.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.mashoras.domain.model.ActivityItem
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ActivitiesUiState {
    object Loading : ActivitiesUiState
    data class Success(val data: List<ActivityItem>) : ActivitiesUiState
    data class Error(val message: String) : ActivitiesUiState
}

class ActivitiesViewModel(private val repo: ActivitiesRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Loading)
    val state: StateFlow<ActivitiesUiState> = _state.asStateFlow()

    init {
        observeLocal()
        refresh()
    }

    private fun observeLocal() {
        viewModelScope.launch {
            repo.observeActivities()
                .catch { e -> _state.value = ActivitiesUiState.Error(e.message ?: "Error local") }
                .collect { list -> _state.value = ActivitiesUiState.Success(list) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repo.refreshActivities()
            } catch (e: Exception) {
                _state.value = ActivitiesUiState.Error(e.message ?: "Error de red")
            }
        }
    }
}

