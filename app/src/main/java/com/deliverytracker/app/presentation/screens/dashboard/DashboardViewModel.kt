package com.deliverytracker.app.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.model.UserSettings
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.deliverytracker.app.domain.usecase.CalculateDashboardStatsUseCase
import com.deliverytracker.app.domain.usecase.CalculateGoalProgressUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import java.util.*
import javax.inject.Inject

/**
 * State για το Dashboard.
 */
data class DashboardUiState(
    val username: String? = null,
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    
    // Σημερινά στατιστικά
    val todayNetIncome: Double = 0.0,
    val todayOrders: Int = 0,
    val todayHours: Double = 0.0,
    val todayBonus: Double = 0.0,
    
    // Μηνιαία στατιστικά
    val monthNetIncome: Double = 0.0,
    val monthOrders: Int = 0,
    val monthShifts: Int = 0,
    
    // Goals
    val dailyGoal: Double? = null,
    val monthlyGoal: Double? = null,
    val dailyProgress: Float = 0f,
    val monthlyProgress: Float = 0f
)

/**
 * ViewModel για το Dashboard.
 * Χρησιμοποιεί UseCases για τους υπολογισμούς αντί inline logic.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val shiftRepository: ShiftRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val calculateDashboardStats: CalculateDashboardStatsUseCase,
    private val calculateGoalProgress: CalculateGoalProgressUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Φορτώνει όλα τα δεδομένα του Dashboard.
     */
    private fun loadData() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        // Φόρτωση user info
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { 
                    it.copy(username = user?.username)
                }
            }
        }
        
        // Φόρτωση settings (goals)
        viewModelScope.launch {
            userSettingsRepository.getUserSettings(userId)
                .catch { e -> 
                    // Καταγραφή σφάλματος για debugging
                    Log.e("DashboardViewModel", "Σφάλμα φόρτωσης ρυθμίσεων", e)
                }
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            dailyGoal = settings?.dailyGoal,
                            monthlyGoal = settings?.monthlyGoal
                        )
                    }
                    updateProgress()
                }
        }
        
        // Φόρτωση shifts
        viewModelScope.launch {
            shiftRepository.getShifts(userId)
                .catch { e -> 
                    // Καταγραφή σφάλματος και ενημέρωση UI
                    Log.e("DashboardViewModel", "Σφάλμα φόρτωσης βαρδιών", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { shifts ->
                    // Χρήση UseCase αντί inline υπολογισμών
                    val stats = calculateDashboardStats(shifts)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            todayNetIncome = stats.todayNetIncome,
                            todayOrders = stats.todayOrders,
                            todayHours = stats.todayHours,
                            todayBonus = stats.todayBonus,
                            monthNetIncome = stats.monthNetIncome,
                            monthOrders = stats.monthOrders,
                            monthShifts = stats.monthShifts
                        )
                    }
                    updateProgress()
                }
        }
    }
    
    /**
     * Ενημερώνει την πρόοδο προς τους στόχους χρησιμοποιώντας UseCase.
     */
    private fun updateProgress() {
        val state = _uiState.value
        val progress = calculateGoalProgress(
            todayIncome = state.todayNetIncome,
            monthIncome = state.monthNetIncome,
            dailyGoal = state.dailyGoal,
            monthlyGoal = state.monthlyGoal
        )
        _uiState.update {
            it.copy(
                dailyProgress = progress.dailyProgress,
                monthlyProgress = progress.monthlyProgress
            )
        }
    }
    
    /**
     * Αποσύνδεση χρήστη.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
