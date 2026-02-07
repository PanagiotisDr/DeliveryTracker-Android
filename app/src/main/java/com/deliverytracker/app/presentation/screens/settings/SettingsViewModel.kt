package com.deliverytracker.app.presentation.screens.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.model.UserSettings
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State για τις Ρυθμίσεις.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    @StringRes val errorResId: Int? = null,  // Static validation errors
    val errorMessage: String? = null,         // Dynamic errors
    
    // Goals
    val dailyGoal: String = "",
    val weeklyGoal: String = "",
    val monthlyGoal: String = "",
    val yearlyGoal: String = "",
    
    // Tax Settings
    val vatRate: String = "24",
    val monthlyEfka: String = "254",
    
    // Theme
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    
    // User Info
    val username: String = "",
    val email: String = "",
    
    // PIN
    val hasPin: Boolean = false,
    val pinRemoved: Boolean = false
)

/**
 * ViewModel για τις Ρυθμίσεις.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Φορτώνει τις ρυθμίσεις του χρήστη.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            // Φόρτωση user info
            authRepository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        username = user?.username ?: "",
                        email = user?.email ?: "",
                        hasPin = user?.hasPin ?: false
                    )
                }
            }
        }
        
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            userSettingsRepository.getUserSettings(userId)
                .catch { e ->
                    // Dynamic error
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { settings ->
                    if (settings != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                dailyGoal = settings.dailyGoal?.toString()?.replace(".", ",") ?: "",
                                weeklyGoal = settings.weeklyGoal?.toString()?.replace(".", ",") ?: "",
                                monthlyGoal = settings.monthlyGoal?.toString()?.replace(".", ",") ?: "",
                                yearlyGoal = settings.yearlyGoal?.toString()?.replace(".", ",") ?: "",
                                vatRate = (settings.vatRate * 100).toInt().toString(),
                                monthlyEfka = settings.monthlyEfkaAmount.toString().replace(".", ","),
                                theme = settings.theme,
                                dynamicColor = settings.dynamicColor
                            )
                        }
                    } else {
                        // Δημιουργία default settings
                        userSettingsRepository.createDefaultSettings(userId)
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
        }
    }
    
    // ============ Input Handlers ============
    
    fun updateDailyGoal(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(dailyGoal = normalizeDecimal(value)) }
        }
    }
    
    fun updateWeeklyGoal(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(weeklyGoal = normalizeDecimal(value)) }
        }
    }
    
    fun updateMonthlyGoal(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(monthlyGoal = normalizeDecimal(value)) }
        }
    }
    
    fun updateYearlyGoal(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(yearlyGoal = normalizeDecimal(value)) }
        }
    }
    
    fun updateVatRate(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d{1,2}$"))) {
            _uiState.update { it.copy(vatRate = value) }
        }
    }
    
    fun updateMonthlyEfka(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(monthlyEfka = normalizeDecimal(value)) }
        }
    }
    
    fun updateTheme(theme: ThemeMode) {
        _uiState.update { it.copy(theme = theme) }
        // Άμεση αποθήκευση στο Firebase για να αλλάζει το theme αμέσως
        saveThemeImmediately(theme)
    }
    
    fun updateDynamicColor(enabled: Boolean) {
        _uiState.update { it.copy(dynamicColor = enabled) }
        // Άμεση αποθήκευση στο Firebase για να αλλάζει αμέσως
        saveDynamicColorImmediately(enabled)
    }
    
    /**
     * Αποθηκεύει μόνο το theme αμέσως στο Firebase.
     * Έτσι η αλλαγή εφαρμόζεται χωρίς να χρειάζεται Save button.
     */
    private fun saveThemeImmediately(theme: ThemeMode) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            userSettingsRepository.updateThemeOnly(userId, theme)
        }
    }
    
    /**
     * Αποθηκεύει μόνο το dynamicColor αμέσως στο Firebase.
     */
    private fun saveDynamicColorImmediately(enabled: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            userSettingsRepository.updateDynamicColorOnly(userId, enabled)
        }
    }
    
    private fun normalizeDecimal(value: String): String {
        return value.replace(".", ",")
    }
    
    private fun isValidDecimal(value: String): Boolean {
        if (value.isEmpty()) return true
        return value.matches(Regex("^\\d*[.,]?\\d*$"))
    }
    
    private fun parseDecimal(value: String): Double? {
        if (value.isEmpty()) return null
        return value.replace(",", ".").toDoubleOrNull()
    }
    
    /**
     * Αποθήκευση ρυθμίσεων.
     */
    fun saveSettings() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(errorResId = R.string.error_not_logged_in) }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            val state = _uiState.value
            val settings = UserSettings(
                id = userId,
                userId = userId,
                theme = state.theme,
                vatRate = (state.vatRate.toIntOrNull() ?: 24) / 100.0,
                monthlyEfkaAmount = parseDecimal(state.monthlyEfka) ?: 254.0,
                dailyGoal = parseDecimal(state.dailyGoal),
                weeklyGoal = parseDecimal(state.weeklyGoal),
                monthlyGoal = parseDecimal(state.monthlyGoal),
                yearlyGoal = parseDecimal(state.yearlyGoal),
                dynamicColor = state.dynamicColor
            )
            
            when (val result = userSettingsRepository.updateUserSettings(settings)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                }
                is Result.Error -> {
                    // Dynamic error
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null, isSaved = false, pinRemoved = false) }
    }
    
    /**
     * Αφαιρεί το PIN του χρήστη.
     * Καλεί updatePin(null) → αφαιρεί το hash από Firebase.
     */
    fun removePin() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            try {
                when (val result = authRepository.updatePin(userId, null)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(hasPin = false, pinRemoved = true) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(errorMessage = result.message) }
                    }
                    is Result.Loading -> { /* Ignore */ }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
