package com.deliverytracker.app.presentation.screens.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
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
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null,  // Για static validation errors
    val errorMessage: String? = null,       // Για dynamic errors (Firebase)
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
        _uiState.update { it.copy(email = email, errorRes = null, errorMessage = null) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorRes = null, errorMessage = null) }
    }
    
    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorRes = null, errorMessage = null) }
    }
    
    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, errorRes = null, errorMessage = null) }
    }
    
    /**
     * Εκτελεί την εγγραφή με validation.
     */
    fun register() {
        val state = _uiState.value
        
        // Validation με @StringRes
        when {
            state.username.isBlank() -> {
                _uiState.update { it.copy(errorRes = R.string.error_enter_username) }
                return
            }
            state.username.length < 3 -> {
                _uiState.update { it.copy(errorRes = R.string.error_username_min_chars) }
                return
            }
            state.email.isBlank() -> {
                _uiState.update { it.copy(errorRes = R.string.error_enter_email) }
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                _uiState.update { it.copy(errorRes = R.string.error_invalid_email) }
                return
            }
            state.password.isBlank() -> {
                _uiState.update { it.copy(errorRes = R.string.error_enter_password) }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(errorRes = R.string.error_password_min_chars) }
                return
            }
            state.confirmPassword != state.password -> {
                _uiState.update { it.copy(errorRes = R.string.error_passwords_not_match) }
                return
            }
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null, errorMessage = null) }
            
            when (val result = authRepository.register(
                email = state.email.trim(),
                password = state.password,
                username = state.username.trim()
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                }
                is Result.Error -> {
                    // Dynamic error από Firebase
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorRes = null, errorMessage = null) }
    }
}
