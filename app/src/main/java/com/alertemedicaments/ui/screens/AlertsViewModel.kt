package com.alertemedicaments.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.models.*
import com.alertemedicaments.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertsUiState(
    val alerts: List<Alert> = emptyList(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Medication> = emptyList(),
    val isSearching: Boolean = false,
    val deletingAlertId: String? = null
)

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn.collect { isLoggedIn ->
                _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
                if (isLoggedIn) {
                    loadAlerts()
                } else {
                    _uiState.update { it.copy(alerts = emptyList()) }
                }
            }
        }
    }

    fun loadAlerts() {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = apiService.getAlerts("Bearer $token")
                _uiState.update {
                    it.copy(alerts = response.alerts, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Impossible de charger les alertes"
                    )
                }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, searchQuery = "", searchResults = emptyList()) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, searchQuery = "", searchResults = emptyList()) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            searchMedications(query)
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private fun searchMedications(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            try {
                val response = apiService.searchMedications(query)
                _uiState.update {
                    it.copy(searchResults = response.medications, isSearching = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    fun createAlert(medication: Medication) {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch

            // Check if alert already exists
            if (_uiState.value.alerts.any { it.medicationId == medication.id }) {
                _uiState.update { it.copy(error = "Alerte déjà existante pour ce médicament") }
                return@launch
            }

            try {
                val request = CreateAlertRequest(medicationId = medication.id)
                apiService.createAlert("Bearer $token", request)
                hideAddDialog()
                loadAlerts()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Impossible de créer l'alerte") }
            }
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch
            _uiState.update { it.copy(deletingAlertId = alertId) }

            try {
                apiService.deleteAlert("Bearer $token", alertId)
                _uiState.update {
                    it.copy(
                        alerts = it.alerts.filter { alert -> alert.id != alertId },
                        deletingAlertId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Impossible de supprimer l'alerte",
                        deletingAlertId = null
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
