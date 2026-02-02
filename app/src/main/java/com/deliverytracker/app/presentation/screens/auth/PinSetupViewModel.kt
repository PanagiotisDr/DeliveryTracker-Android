package com.deliverytracker.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για τη ρύθμιση PIN.
 */
@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()
    
    /**
     * Προσθέτει ψηφίο.
     */
    fun addDigit(digit: String) {
        val state = _uiState.value
        
        if (!state.isConfirming) {
            // Πρώτη εισαγωγή
            if (state.pin.length < 4) {
                val newPin = state.pin + digit
                _uiState.update { it.copy(pin = newPin, error = null) }
                
                if (newPin.length == 4) {
                    // Πήγαινε σε confirmation
                    _uiState.update { it.copy(isConfirming = true) }
                }
            }
        } else {
            // Επιβεβαίωση
            if (state.confirmPin.length < 4) {
                val newConfirmPin = state.confirmPin + digit
                _uiState.update { it.copy(confirmPin = newConfirmPin, error = null) }
                
                if (newConfirmPin.length == 4) {
                    // Έλεγχος αν ταιριάζουν
                    if (newConfirmPin == state.pin) {
                        savePin(state.pin)
                    } else {
                        _uiState.update { 
                            it.copy(
                                error = "Τα PIN δεν ταιριάζουν",
                                confirmPin = "",
                                isConfirming = false,
                                pin = ""
                            ) 
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Αφαιρεί το τελευταίο ψηφίο.
     */
    fun removeDigit() {
        val state = _uiState.value
        
        if (state.isConfirming) {
            if (state.confirmPin.isNotEmpty()) {
                _uiState.update { it.copy(confirmPin = state.confirmPin.dropLast(1)) }
            }
        } else {
            if (state.pin.isNotEmpty()) {
                _uiState.update { it.copy(pin = state.pin.dropLast(1)) }
            }
        }
    }
    
    /**
     * Αποθηκεύει το PIN.
     */
    private fun savePin(pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Δεν βρέθηκε χρήστης") 
                }
                return@launch
            }
            
            when (val result = authRepository.updatePin(userId, pin)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isSaved = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    /**
     * Παράλειψη ρύθμισης PIN.
     */
    fun skip() {
        _uiState.update { it.copy(isSkipped = true) }
    }
    
}

/**
 * UI State για το PIN setup.
 */
data class PinSetupUiState(
    val isLoading: Boolean = false,
    val pin: String = "",
    val confirmPin: String = "",
    val isConfirming: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isSkipped: Boolean = false
)
