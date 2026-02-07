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
 * ViewModel για το PIN login.
 * Χρησιμοποιεί @StringRes για proper i18n.
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
            _uiState.update { it.copy(enteredPin = newPin, errorRes = null, errorMessage = null) }
            
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
                it.copy(enteredPin = currentPin.dropLast(1), errorRes = null, errorMessage = null) 
            }
        }
    }
    
    /**
     * Καθαρίζει το PIN.
     */
    fun clearPin() {
        _uiState.update { it.copy(enteredPin = "", errorRes = null, errorMessage = null) }
    }
    
    /**
     * Επαληθεύει το PIN.
     * Ελέγχει πρώτα αν ο χρήστης είναι locked out.
     */
    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = _uiState.value.userId
            if (userId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorRes = R.string.error_user_not_found,
                        enteredPin = ""
                    ) 
                }
                return@launch
            }
            
            // Έλεγχος αν ο χρήστης είναι locked out
            val lockoutResult = authRepository.checkPinLockout(userId)
            if (lockoutResult is Result.Success && lockoutResult.data > 0) {
                val secondsRemaining = (lockoutResult.data / 1000).toInt()
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorRes = R.string.error_pin_locked,
                        enteredPin = ""
                    ) 
                }
                return@launch
            }
            
            val pinHash = hashPin(pin)
            when (val result = authRepository.verifyPin(userId, pinHash)) {
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
                                errorRes = R.string.error_wrong_pin,
                                enteredPin = ""
                            ) 
                        }
                    }
                }
                is Result.Error -> {
                    // Dynamic error
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message,
                            enteredPin = ""
                        ) 
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    /**
     * Hash του PIN για ασφαλή αποθήκευση.
     */
    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * UI State για το PIN login.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class PinLoginUiState(
    val isLoading: Boolean = false,
    val hasPin: Boolean = false,
    val userId: String? = null,
    val enteredPin: String = "",
    @StringRes val errorRes: Int? = null,    // Static validation errors
    val errorMessage: String? = null,         // Dynamic errors
    val isVerified: Boolean = false
)
