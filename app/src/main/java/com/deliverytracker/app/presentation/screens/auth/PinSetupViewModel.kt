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
import java.security.MessageDigest
import javax.inject.Inject

/**
 * ViewModel για τη ρύθμιση PIN.
 * Χρησιμοποιεί @StringRes για proper i18n.
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
                _uiState.update { it.copy(pin = newPin, errorRes = null, errorMessage = null) }
                
                if (newPin.length == 4) {
                    // Πήγαινε σε confirmation
                    _uiState.update { it.copy(isConfirming = true) }
                }
            }
        } else {
            // Επιβεβαίωση
            if (state.confirmPin.length < 4) {
                val newConfirmPin = state.confirmPin + digit
                _uiState.update { it.copy(confirmPin = newConfirmPin, errorRes = null, errorMessage = null) }
                
                if (newConfirmPin.length == 4) {
                    // Έλεγχος αν ταιριάζουν
                    if (newConfirmPin == state.pin) {
                        savePin(state.pin)
                    } else {
                        _uiState.update { 
                            it.copy(
                                errorRes = R.string.error_pins_not_match,
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
            
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { 
                    it.copy(isLoading = false, errorRes = R.string.error_user_not_found) 
                }
                return@launch
            }
            
            val pinHash = hashPin(pin)
            when (val result = authRepository.updatePin(userId, pinHash)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isSaved = true) 
                    }
                }
                is Result.Error -> {
                    // Dynamic error
                    _uiState.update { 
                        it.copy(isLoading = false, errorMessage = result.message) 
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
    
    /**
     * Hash του PIN.
     */
    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * UI State για το PIN setup.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class PinSetupUiState(
    val isLoading: Boolean = false,
    val pin: String = "",
    val confirmPin: String = "",
    val isConfirming: Boolean = false,
    @StringRes val errorRes: Int? = null,    // Static errors
    val errorMessage: String? = null,         // Dynamic errors
    val isSaved: Boolean = false,
    val isSkipped: Boolean = false
)
