package com.alertemedicaments.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.api.DeleteAccountRequest
import com.alertemedicaments.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class PrivacyUiState(
    val isExporting: Boolean = false,
    val isDeleting: Boolean = false,
    val accountDeleted: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyUiState())
    val uiState: StateFlow<PrivacyUiState> = _uiState.asStateFlow()

    fun exportData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null, successMessage = null) }

            try {
                val token = authRepository.getToken()
                if (token == null) {
                    _uiState.update { it.copy(isExporting = false, error = "Non authentifié") }
                    return@launch
                }

                val response = apiService.exportUserData("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // Save to file
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                        val fileName = "alertemedicaments_export_${dateFormat.format(Date())}.json"

                        val downloadsDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        )
                        val file = File(downloadsDir, fileName)

                        file.outputStream().use { output ->
                            responseBody.byteStream().use { input ->
                                input.copyTo(output)
                            }
                        }

                        _uiState.update {
                            it.copy(
                                isExporting = false,
                                successMessage = "Données exportées vers: ${file.absolutePath}"
                            )
                        }
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Session expirée, veuillez vous reconnecter"
                        else -> "Erreur lors de l'export: ${response.code()}"
                    }
                    _uiState.update { it.copy(isExporting = false, error = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isExporting = false, error = "Erreur: ${e.message}")
                }
            }
        }
    }

    fun deleteAccount(password: String, confirmPhrase: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }

            try {
                val token = authRepository.getToken()
                if (token == null) {
                    _uiState.update { it.copy(isDeleting = false, error = "Non authentifié") }
                    return@launch
                }

                val request = DeleteAccountRequest(password, confirmPhrase)
                val response = apiService.deleteAccount("Bearer $token", request)

                if (response.isSuccessful) {
                    // Clear session
                    authRepository.logout()
                    _uiState.update {
                        it.copy(isDeleting = false, accountDeleted = true)
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Phrase de confirmation incorrecte"
                        401 -> "Mot de passe incorrect"
                        else -> "Erreur lors de la suppression: ${response.code()}"
                    }
                    _uiState.update { it.copy(isDeleting = false, error = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isDeleting = false, error = "Erreur: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
