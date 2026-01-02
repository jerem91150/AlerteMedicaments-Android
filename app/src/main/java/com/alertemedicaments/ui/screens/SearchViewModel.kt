package com.alertemedicaments.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val suggestions: List<Medication> = emptyList(),
    val searchResults: List<Medication> = emptyList(),
    val selectedMedication: Medication? = null,
    val alternatives: List<Alternative> = emptyList(),
    val pharmacies: List<Pharmacy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()

        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(200) // Debounce
                fetchSuggestions(query)
            }
        } else {
            _uiState.update { it.copy(suggestions = emptyList()) }
        }
    }

    private suspend fun fetchSuggestions(query: String) {
        try {
            val response = apiService.getSuggestions(query)
            _uiState.update { it.copy(suggestions = response.suggestions) }
        } catch (e: Exception) {
            // Silently fail for suggestions
        }
    }

    fun search() {
        val query = _uiState.value.searchQuery
        if (query.length < 2) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, suggestions = emptyList()) }

            try {
                val response = apiService.searchMedications(query)
                _uiState.update {
                    it.copy(
                        searchResults = response.medications,
                        isLoading = false
                    )
                }

                if (response.medications.size == 1) {
                    selectMedication(response.medications.first())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectMedication(medication: Medication) {
        _uiState.update {
            it.copy(
                selectedMedication = medication,
                searchQuery = medication.name,
                suggestions = emptyList(),
                searchResults = listOf(medication)
            )
        }

        viewModelScope.launch {
            // Load alternatives if in rupture or tension
            if (medication.status == MedicationStatus.RUPTURE ||
                medication.status == MedicationStatus.TENSION) {
                loadAlternatives(medication.id)
            } else {
                _uiState.update { it.copy(alternatives = emptyList()) }
            }

            // Load pharmacies
            loadPharmacies(medication.id)
        }
    }

    fun selectAlternative(alternative: Alternative) {
        val medication = Medication(
            id = alternative.id,
            cisCode = alternative.cisCode,
            name = alternative.name,
            laboratory = alternative.laboratory,
            activeIngredient = alternative.activeIngredient,
            dosage = null,
            form = null,
            status = try {
                MedicationStatus.valueOf(alternative.status)
            } catch (e: Exception) {
                MedicationStatus.UNKNOWN
            },
            lastChecked = null,
            expectedReturn = null
        )
        selectMedication(medication)
    }

    private suspend fun loadAlternatives(medicationId: String) {
        try {
            val response = apiService.getAlternatives(medicationId)
            _uiState.update { it.copy(alternatives = response.alternatives) }
        } catch (e: Exception) {
            _uiState.update { it.copy(alternatives = emptyList()) }
        }
    }

    private suspend fun loadPharmacies(medicationId: String, lat: Double? = null, lng: Double? = null) {
        try {
            val response = apiService.getNearbyPharmacies(medicationId, lat, lng)
            _uiState.update { it.copy(pharmacies = response.pharmacies) }
        } catch (e: Exception) {
            _uiState.update { it.copy(pharmacies = emptyList()) }
        }
    }

    fun clearSearch() {
        _uiState.update {
            SearchUiState()
        }
    }
}
