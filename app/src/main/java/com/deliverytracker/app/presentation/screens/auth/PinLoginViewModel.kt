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
 * ViewModel για το PIN login.
 */
@HiltViewModel
class PinLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinLoginUiState())
    val uiState: StateFlow<PinLoginUiState> = _uiState.asStateFlow()
    
    init {
        checkUserPin()
    }
    
    /**
     * Ελέγχει αν ο χρήστης έχει ορίσει PIN.
     */
    private fun checkUserPin() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { 
                    it.copy(
                        hasPin = user?.hasPin == true,
                        userId = user?.id
                    )
                }
            }
        }
    }
    
    /**
     * Προσθέτει ψηφίο στο PIN.
     */
    fun addDigit(digit: String) {
        val currentPin = _uiState.value.enteredPin
        if (currentPin.length < 4) {
            val newPin = currentPin + digit
            _uiState.update { it.copy(enteredPin = newPin, error = null) }
            
            // Αν ολοκληρώθηκε το PIN, επαλήθευση
            if (newPin.length == 4) {
                verifyPin(newPin)
            }
        }
    }
    
    /**
     * Αφαιρεί το τελευταίο ψηφίο.
     */
    fun removeDigit() {
        val currentPin = _uiState.value.enteredPin
        if (currentPin.isNotEmpty()) {
            _uiState.update { 
                it.copy(enteredPin = currentPin.dropLast(1), error = null) 
            }
        }
    }
    
    /**
     * Καθαρίζει το PIN.
     */
    fun clearPin() {
        _uiState.update { it.copy(enteredPin = "", error = null) }
    }
    
    /**
     * Επαληθεύει το PIN.
     */
    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = _uiState.value.userId
            if (userId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Δεν βρέθηκε χρήστης",
                        enteredPin = ""
                    ) 
                }
                return@launch
            }
            
            when (val result = authRepository.verifyPin(userId, pin)) {
                is Result.Success -> {
                    if (result.data) {
                        // Επιτυχής επαλήθευση
                        authRepository.resetPinLockout(userId)
                        _uiState.update { 
                            it.copy(isLoading = false, isVerified = true) 
                        }
                    } else {
                        // Λάθος PIN
                        authRepository.recordFailedPinAttempt(userId)
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = "Λάθος PIN",
                                enteredPin = ""
                            ) 
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message,
                            enteredPin = ""
                        ) 
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
}

/**
 * UI State για το PIN login.
 */
data class PinLoginUiState(
    val isLoading: Boolean = false,
    val hasPin: Boolean = false,
    val userId: String? = null,
    val enteredPin: String = "",
    val error: String? = null,
    val isVerified: Boolean = false
)
