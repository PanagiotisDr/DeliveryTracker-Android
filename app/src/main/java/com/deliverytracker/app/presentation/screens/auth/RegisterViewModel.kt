package com.deliverytracker.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State για την οθόνη εγγραφής.
 */
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false
)

/**
 * ViewModel για την οθόνη εγγραφής.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }
    
    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }
    
    /**
     * Εκτελεί την εγγραφή με validation.
     */
    fun register() {
        val state = _uiState.value
        
        // Validation
        when {
            state.username.isBlank() -> {
                _uiState.update { it.copy(error = "Εισάγετε όνομα χρήστη") }
                return
            }
            state.username.length < 3 -> {
                _uiState.update { it.copy(error = "Το όνομα πρέπει να έχει τουλάχιστον 3 χαρακτήρες") }
                return
            }
            state.email.isBlank() -> {
                _uiState.update { it.copy(error = "Εισάγετε το email σας") }
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                _uiState.update { it.copy(error = "Μη έγκυρη διεύθυνση email") }
                return
            }
            state.password.isBlank() -> {
                _uiState.update { it.copy(error = "Εισάγετε κωδικό πρόσβασης") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(error = "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες") }
                return
            }
            state.confirmPassword != state.password -> {
                _uiState.update { it.copy(error = "Οι κωδικοί δεν ταιριάζουν") }
                return
            }
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.register(
                email = state.email.trim(),
                password = state.password,
                username = state.username.trim()
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
