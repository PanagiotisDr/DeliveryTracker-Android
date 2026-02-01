package com.deliverytracker.app.presentation.screens.commandcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 🎮 Command Center ViewModel
 * 
 * Διαχειρίζεται τα data για το Command Center dashboard.
 */
@HiltViewModel
class CommandCenterViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CommandCenterUiState())
    val uiState: StateFlow<CommandCenterUiState> = _uiState.asStateFlow()
    
    // Παίρνουμε το userId από το Firebase Auth
    private val userId: String
        get() = auth.currentUser?.uid ?: ""
    
    init {
        loadData()
    }
    
    /**
     * Φορτώνει όλα τα δεδομένα για το Command Center
     */
    private fun loadData() {
        if (userId.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = "Δεν είστε συνδεδεμένος") }
            return
        }
        
        viewModelScope.launch {
            try {
                // Φόρτωση user settings
                userSettingsRepository.getUserSettings(userId).collect { settings ->
                    val dailyGoal = settings?.dailyGoal ?: 100.0
                    // Παίρνουμε το όνομα από το Firebase Auth
                    val userName = auth.currentUser?.displayName ?: ""
                    
                    // Φόρτωση shifts
                    shiftRepository.getShifts(userId).collect { shifts ->
                        calculateStats(shifts, dailyGoal, userName)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
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
                    emoji = "🔥",
                    title = "Peak hours τώρα!",
                    subtitle = "Ξεκίνα βάρδια - οι 11:00-14:00 είναι οι πιο κερδοφόρες",
                    action = {}
                )
            }
            todayShifts.isEmpty() && hour in 18..21 -> {
                SmartSuggestion(
                    emoji = "🌙",
                    title = "Βραδινό peak!",
                    subtitle = "Οι ώρες 18:00-21:00 έχουν υψηλή ζήτηση",
                    action = {}
                )
            }
            // Αν είναι κοντά στον στόχο
            todayEarnings >= dailyGoal * 0.8 && todayEarnings < dailyGoal -> {
                val remaining = dailyGoal - todayEarnings
                SmartSuggestion(
                    emoji = "🎯",
                    title = "Σχεδόν εκεί!",
                    subtitle = "Λείπουν ${String.format("%.0f", remaining)}€ για τον στόχο",
                    action = {}
                )
            }
            // Αν ξεπέρασε τον στόχο
            todayEarnings >= dailyGoal -> {
                SmartSuggestion(
                    emoji = "🏆",
                    title = "Στόχος επιτεύχθηκε!",
                    subtitle = "Έβγαλες ${String.format("%.0f", todayEarnings - dailyGoal)}€ πάνω από τον στόχο",
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
                _uiState.update { it.copy(error = e.message) }
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
    val dailyGoal: Double = 100.0,
    val avgPerHour: Double = 0.0,
    val weeklyTrend: Double = 0.0,
    val todayShifts: List<Shift> = emptyList(),
    val recentShifts: List<Shift> = emptyList(),
    val allShifts: List<Shift> = emptyList(),
    val smartSuggestion: SmartSuggestion? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * 🔮 Smart Suggestion model
 */
data class SmartSuggestion(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val action: () -> Unit
)
