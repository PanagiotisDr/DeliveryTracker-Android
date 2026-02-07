package com.deliverytracker.app.presentation.screens.commandcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.deliverytracker.app.presentation.theme.AppEmojis
import com.deliverytracker.app.presentation.theme.BusinessRules
import com.deliverytracker.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import androidx.annotation.StringRes
import com.deliverytracker.app.R

/**
 * 🎮 Command Center ViewModel
 * 
 * Διαχειρίζεται τα data για το Command Center dashboard.
 */
@HiltViewModel
class CommandCenterViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CommandCenterUiState())
    val uiState: StateFlow<CommandCenterUiState> = _uiState.asStateFlow()
    
    // Παίρνουμε το userId μέσω AuthRepository
    private val userId: String
        get() = authRepository.getCurrentUserId() ?: ""
    
    init {
        loadData()
    }
    
    /**
     * Φορτώνει όλα τα δεδομένα για το Command Center
     */
    private fun loadData() {
        if (userId.isEmpty()) {
            // Χρησιμοποιούμε το ήδη υπάρχον error_not_logged_in
            _uiState.update { it.copy(isLoading = false, errorResId = com.deliverytracker.app.R.string.error_not_logged_in) }
            return
        }
        
        viewModelScope.launch {
            try {
                // Φόρτωση user settings
                userSettingsRepository.getUserSettings(userId).collect { settings ->
                    val dailyGoal = settings?.dailyGoal ?: 100.0
                    // Παίρνουμε το όνομα μέσω AuthRepository
                    val userName = authRepository.getCurrentUser()?.username ?: ""
                    
                    // Φόρτωση shifts
                    shiftRepository.getShifts(userId).collect { shifts ->
                        calculateStats(shifts, dailyGoal, userName)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
        
        // Φόρτωση εξόδων σε ξεχωριστό coroutine
        viewModelScope.launch {
            try {
                expenseRepository.getExpenses(userId)
                    .collect { expenses ->
                        _uiState.update { it.copy(allExpenses = expenses) }
                    }
            } catch (e: Exception) {
                // Δεν σταματάμε την εφαρμογή αν αποτύχουν τα έξοδα
                android.util.Log.e("CommandCenterVM", "Σφάλμα φόρτωσης εξόδων", e)
            }
        }
    }
    
    /**
     * Υπολογίζει τα στατιστικά για το dashboard
     */
    private fun calculateStats(shifts: List<Shift>, dailyGoal: Double, userName: String) {
        val today = getStartOfDay(System.currentTimeMillis())
        val weekAgo = today - (7 * 24 * 60 * 60 * 1000L)
        val twoWeeksAgo = today - (14 * 24 * 60 * 60 * 1000L)
        
        // Today's shifts
        val todayShifts = shifts.filter { it.date >= today }
        val todayEarnings = todayShifts.sumOf { it.netIncome }
        val todayHours = todayShifts.sumOf { it.hoursWorked }
        
        // This week
        val thisWeekShifts = shifts.filter { it.date >= weekAgo }
        val thisWeekEarnings = thisWeekShifts.sumOf { it.netIncome }
        val thisWeekHours = thisWeekShifts.sumOf { it.hoursWorked }
        
        // Last week (for trend)
        val lastWeekShifts = shifts.filter { it.date >= twoWeeksAgo && it.date < weekAgo }
        val lastWeekEarnings = lastWeekShifts.sumOf { it.netIncome }
        
        // Calculate trend percentage
        val weeklyTrend = if (lastWeekEarnings > 0) {
            ((thisWeekEarnings - lastWeekEarnings) / lastWeekEarnings) * 100
        } else {
            0.0
        }
        
        // Average per hour (this week)
        val avgPerHour = if (thisWeekHours > 0) {
            thisWeekEarnings / thisWeekHours
        } else {
            0.0
        }
        
        // Recent shifts (sorted by date desc)
        val recentShifts = shifts.sortedByDescending { it.date }.take(10)
        
        // Smart suggestion
        val suggestion = generateSmartSuggestion(
            todayShifts = todayShifts,
            avgPerHour = avgPerHour,
            dailyGoal = dailyGoal,
            todayEarnings = todayEarnings
        )
        
        _uiState.update {
            it.copy(
                userName = userName,
                todayEarnings = todayEarnings,
                todayHours = todayHours,
                dailyGoal = dailyGoal,
                avgPerHour = avgPerHour,
                weeklyTrend = weeklyTrend,
                todayShifts = todayShifts,
                recentShifts = recentShifts,
                allShifts = shifts.sortedByDescending { s -> s.date },
                smartSuggestion = suggestion,
                isLoading = false
            )
        }
    }
    
    /**
     * Δημιουργεί smart suggestion με βάση τα δεδομένα
     */
    private fun generateSmartSuggestion(
        todayShifts: List<Shift>,
        avgPerHour: Double,
        dailyGoal: Double,
        todayEarnings: Double
    ): SmartSuggestion? {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        return when {
            // Αν δεν έχει ξεκινήσει βάρδια σήμερα και είναι ώρα αιχμής
            todayShifts.isEmpty() && hour in 11..14 -> {
                SmartSuggestion(
                    emoji = AppEmojis.FIRE,
                    titleResId = R.string.suggestion_peak_morning_title,
                    subtitleResId = R.string.suggestion_peak_morning_subtitle,
                    action = {}
                )
            }
            todayShifts.isEmpty() && hour in 18..21 -> {
                SmartSuggestion(
                    emoji = AppEmojis.MOON,
                    titleResId = R.string.suggestion_peak_evening_title,
                    subtitleResId = R.string.suggestion_peak_evening_subtitle,
                    action = {}
                )
            }
            // Αν είναι κοντά στον στόχο
            todayEarnings >= dailyGoal * 0.8 && todayEarnings < dailyGoal -> {
                val remaining = dailyGoal - todayEarnings
                SmartSuggestion(
                    emoji = AppEmojis.TARGET,
                    titleResId = R.string.suggestion_almost_there_title,
                    subtitleResId = R.string.suggestion_almost_there_subtitle,
                    formatArgs = listOf(String.format("%.0f", remaining)),
                    action = {}
                )
            }
            // Αν ξεπέρασε τον στόχο
            todayEarnings >= dailyGoal -> {
                SmartSuggestion(
                    emoji = AppEmojis.TROPHY,
                    titleResId = R.string.suggestion_goal_achieved_title,
                    subtitleResId = R.string.suggestion_goal_achieved_subtitle,
                    formatArgs = listOf(String.format("%.0f", todayEarnings - dailyGoal)),
                    action = {}
                )
            }
            // Default
            else -> null
        }
    }
    
    /**
     * Διαγράφει βάρδια (soft delete)
     */
    fun deleteShift(shiftId: String) {
        viewModelScope.launch {
            try {
                shiftRepository.softDeleteShift(shiftId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
    
    /**
     * Παίρνει την αρχή της ημέρας (00:00:00)
     */
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

/**
 * 📊 UI State για το Command Center
 */
data class CommandCenterUiState(
    val userName: String = "",
    val todayEarnings: Double = 0.0,
    val todayHours: Double = 0.0,
    val dailyGoal: Double = BusinessRules.DEFAULT_DAILY_GOAL,
    val avgPerHour: Double = 0.0,
    val weeklyTrend: Double = 0.0,
    val todayShifts: List<Shift> = emptyList(),
    val recentShifts: List<Shift> = emptyList(),
    val allShifts: List<Shift> = emptyList(),
    val allExpenses: List<Expense> = emptyList(),
    val smartSuggestion: SmartSuggestion? = null,
    val isLoading: Boolean = true,
    @StringRes val errorResId: Int? = null,
    val errorMessage: String? = null
)

/**
 * 🔮 Smart Suggestion model
 * Χρησιμοποιεί @StringRes για proper i18n
 */
data class SmartSuggestion(
    val emoji: String,
    @StringRes val titleResId: Int,
    @StringRes val subtitleResId: Int,
    val formatArgs: List<Any> = emptyList(),  // Για dynamic placeholders (%.0f κτλ)
    val action: () -> Unit
)
