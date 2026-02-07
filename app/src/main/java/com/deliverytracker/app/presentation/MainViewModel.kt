package com.deliverytracker.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State για το κυρίως UI shell (theme, auth status, loading).
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val hasPin: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false
)

/**
 * MainViewModel: Διαχειρίζεται το auth state και τις theme settings.
 * Αντικαθιστά τα field-injected repositories που ήταν πριν στο MainActivity.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialState()
    }
    
    /**
     * Φορτώνει τα αρχικά δεδομένα: login status, user info, theme.
     */
    private fun loadInitialState() {
        val isLoggedIn = authRepository.isLoggedIn
        _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
        
        if (!isLoggedIn) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        
        // Παρακολούθηση user info (για PIN status)
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasPin = user?.hasPin ?: false
                    )
                }
            }
        }
        
        // Παρακολούθηση theme settings
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            userSettingsRepository.getUserSettings(userId)
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            themeMode = settings?.theme ?: ThemeMode.SYSTEM,
                            dynamicColor = settings?.dynamicColor ?: false
                        )
                    }
                }
        }
    }
    
    /**
     * Αποσύνδεση χρήστη.
     * Χρησιμοποιείται από το NavGraph (PinLogin → Use Password).
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
