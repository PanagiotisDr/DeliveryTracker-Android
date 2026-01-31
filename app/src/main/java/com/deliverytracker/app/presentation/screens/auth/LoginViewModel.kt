package com.deliverytracker.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.User
import com.deliverytracker.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State για την οθόνη σύνδεσης.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val needsPinSetup: Boolean = false  // Αν χρειάζεται να ορίσει PIN
)

/**
 * ViewModel για την οθόνη σύνδεσης.
 * Διαχειρίζεται το login flow με Firebase Auth.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    /**
     * Ενημερώνει το email.
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    
    /**
     * Ενημερώνει τον κωδικό.
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    /**
     * Εκτελεί το login.
     */
    fun login() {
        val state = _uiState.value
        
        // Validation
        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "Εισάγετε το email σας") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Εισάγετε τον κωδικό σας") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.login(state.email.trim(), state.password)) {
                is Result.Success -> {
                    val user = result.data
                    // Αν δεν έχει PIN, πρέπει να το ορίσει
                    if (!user.hasPin) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isLoggedIn = true,
                                needsPinSetup = true
                            ) 
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {
                    // Handled by isLoading state
                }
            }
        }
    }
    
    /**
     * Αποστολή email επαναφοράς κωδικού.
     */
    fun resetPassword() {
        val email = _uiState.value.email
        
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Εισάγετε πρώτα το email σας") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = authRepository.resetPassword(email.trim())) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = null
                        ) 
                    }
                    // TODO: Show success snackbar
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Καθαρίζει τα σφάλματα.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
