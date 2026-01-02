package com.alertemedicaments.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alertemedicaments.data.models.User
import com.alertemedicaments.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn.collect { isLoggedIn ->
                _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
            }
        }
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun updateLoginEmail(email: String) {
        _uiState.update { it.copy(loginEmail = email, error = null) }
    }

    fun updateLoginPassword(password: String) {
        _uiState.update { it.copy(loginPassword = password, error = null) }
    }

    fun updateRegisterName(name: String) {
        _uiState.update { it.copy(registerName = name, error = null) }
    }

    fun updateRegisterEmail(email: String) {
        _uiState.update { it.copy(registerEmail = email, error = null) }
    }

    fun updateRegisterPassword(password: String) {
        _uiState.update { it.copy(registerPassword = password, error = null) }
    }

    fun updateRegisterConfirmPassword(password: String) {
        _uiState.update { it.copy(registerConfirmPassword = password, error = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.loginEmail.isBlank() || state.loginPassword.isBlank()) {
            _uiState.update { it.copy(error = "Veuillez remplir tous les champs") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(state.loginEmail, state.loginPassword)
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            loginEmail = "",
                            loginPassword = ""
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erreur de connexion"
                        )
                    }
                }
            )
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.registerEmail.isBlank() || state.registerPassword.isBlank()) {
            _uiState.update { it.copy(error = "Veuillez remplir tous les champs") }
            return
        }
        if (state.registerPassword != state.registerConfirmPassword) {
            _uiState.update { it.copy(error = "Les mots de passe ne correspondent pas") }
            return
        }
        if (state.registerPassword.length < 6) {
            _uiState.update { it.copy(error = "Le mot de passe doit contenir au moins 6 caractères") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(
                state.registerName.ifBlank { state.registerEmail.substringBefore("@") },
                state.registerEmail,
                state.registerPassword
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            registerName = "",
                            registerEmail = "",
                            registerPassword = "",
                            registerConfirmPassword = ""
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erreur d'inscription"
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
