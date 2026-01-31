package com.deliverytracker.app.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.model.UserSettings
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * State για το Dashboard.
 */
data class DashboardUiState(
    val username: String? = null,
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    
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
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val shiftRepository: ShiftRepository,
    private val userSettingsRepository: UserSettingsRepository
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
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
                .catch { /* Ignore errors */ }
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
                .catch { /* Ignore errors */ }
                .collect { shifts ->
                    calculateStats(shifts)
                }
        }
    }
    
    /**
     * Υπολογίζει τα στατιστικά από τις βάρδιες.
     */
    private fun calculateStats(shifts: List<Shift>) {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val thisMonth = calendar.get(Calendar.MONTH)
        val thisYear = calendar.get(Calendar.YEAR)
        
        // Σημερινές βάρδιες
        val todayShifts = shifts.filter { shift ->
            val shiftCal = Calendar.getInstance().apply { timeInMillis = shift.date }
            shiftCal.get(Calendar.DAY_OF_YEAR) == today &&
            shiftCal.get(Calendar.YEAR) == thisYear
        }
        
        // Μηνιαίες βάρδιες
        val monthShifts = shifts.filter { shift ->
            val shiftCal = Calendar.getInstance().apply { timeInMillis = shift.date }
            shiftCal.get(Calendar.MONTH) == thisMonth &&
            shiftCal.get(Calendar.YEAR) == thisYear
        }
        
        _uiState.update {
            it.copy(
                isLoading = false,
                // Today
                todayNetIncome = todayShifts.sumOf { s -> s.netIncome },
                todayOrders = todayShifts.sumOf { s -> s.ordersCount },
                todayHours = todayShifts.sumOf { s -> s.hoursWorked },
                todayBonus = todayShifts.sumOf { s -> s.bonus },
                // Month
                monthNetIncome = monthShifts.sumOf { s -> s.netIncome },
                monthOrders = monthShifts.sumOf { s -> s.ordersCount },
                monthShifts = monthShifts.size
            )
        }
        
        updateProgress()
    }
    
    /**
     * Ενημερώνει την πρόοδο προς τους στόχους.
     */
    private fun updateProgress() {
        val state = _uiState.value
        
        val dailyProgress = if (state.dailyGoal != null && state.dailyGoal > 0) {
            (state.todayNetIncome / state.dailyGoal).toFloat().coerceIn(0f, 1f)
        } else 0f
        
        val monthlyProgress = if (state.monthlyGoal != null && state.monthlyGoal > 0) {
            (state.monthNetIncome / state.monthlyGoal).toFloat().coerceIn(0f, 1f)
        } else 0f
        
        _uiState.update {
            it.copy(
                dailyProgress = dailyProgress,
                monthlyProgress = monthlyProgress
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
