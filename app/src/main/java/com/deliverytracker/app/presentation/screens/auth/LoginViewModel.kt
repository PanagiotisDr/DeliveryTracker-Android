package com.deliverytracker.app.presentation.screens.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
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
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null,  // Αλλαγή σε StringRes για localization
    val errorMessage: String? = null,      // Για dynamic errors (π.χ. Firebase)
    val isLoggedIn: Boolean = false,
    val needsPinSetup: Boolean = false,
    val resetPasswordSent: Boolean = false
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
        _uiState.update { it.copy(email = email, errorRes = null, errorMessage = null) }
    }
    
    /**
     * Ενημερώνει τον κωδικό.
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorRes = null, errorMessage = null) }
    }
    
    /**
     * Εκτελεί το login.
     */
    fun login() {
        val state = _uiState.value
        
        // Validation με StringRes
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorRes = R.string.error_enter_email) }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorRes = R.string.error_enter_password) }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null, errorMessage = null) }
            
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
                    // Dynamic error από Firebase
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
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
            _uiState.update { it.copy(errorRes = R.string.error_enter_email_first) }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = authRepository.resetPassword(email.trim())) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorRes = null,
                            errorMessage = null,
                            resetPasswordSent = true
                        ) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Καθαρίζει τα σφάλματα.
     */
    fun clearError() {
        _uiState.update { it.copy(errorRes = null, errorMessage = null) }
    }
}
