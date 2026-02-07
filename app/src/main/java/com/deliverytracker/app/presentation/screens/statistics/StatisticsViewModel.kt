package com.deliverytracker.app.presentation.screens.statistics

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.usecase.CalculateStatisticsUseCase
import com.deliverytracker.app.domain.usecase.GetDateRangeUseCase
import com.deliverytracker.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import android.util.Log
import javax.inject.Inject

/**
 * Period filter για στατιστικά.
 * Χρησιμοποιεί @StringRes για σωστή υποστήριξη πολλαπλών γλωσσών.
 */
enum class StatsPeriod(@StringRes val labelRes: Int) {
    TODAY(R.string.period_today),
    WEEK(R.string.period_week),
    MONTH(R.string.period_month),
    YEAR(R.string.period_year),
    ALL(R.string.period_all)
}

/**
 * State για τα στατιστικά.
 */
data class StatisticsUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: StatsPeriod = StatsPeriod.ALL,
    val errorMessage: String? = null,
    
    // Έσοδα
    val grossIncome: Double = 0.0,
    val tips: Double = 0.0,
    val bonus: Double = 0.0,
    val netIncome: Double = 0.0,
    
    // Έξοδα
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<ExpenseCategory, Double> = emptyMap(),
    
    // Στατιστικά
    val totalOrders: Int = 0,
    val totalHours: Double = 0.0,
    val totalKilometers: Double = 0.0,
    val totalShifts: Int = 0,
    
    // Μέσοι όροι
    val avgIncomePerHour: Double = 0.0,
    val avgOrdersPerShift: Double = 0.0,
    val avgIncomePerOrder: Double = 0.0,
    val avgIncomePerKm: Double = 0.0,
    
    // Best day
    val bestDayDate: Long? = null,
    val bestDayIncome: Double = 0.0,
    
    // Chart data
    val dailyIncomes: List<Pair<String, Double>> = emptyList()
)

/**
 * ViewModel για τα στατιστικά.
 * Χρησιμοποιεί ξεχωριστά coroutines για shifts και expenses (ίδιο pattern
 * με ShiftListViewModel και CommandCenterViewModel).
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val calculateStatistics: CalculateStatisticsUseCase,
    private val getDateRange: GetDateRangeUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "StatisticsVM"
    }
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    // Αποθήκευση raw data για re-filtering σε αλλαγή περιόδου
    private var allShifts: List<Shift> = emptyList()
    private var allExpenses: List<Expense> = emptyList()
    
    init {
        loadData()
    }
    
    /**
     * Φορτώνει δεδομένα με ξεχωριστά coroutines.
     * try/catch γύρω από ολόκληρο το flow πιάνει τα Firestore errors.
     */
    private fun loadData() {
        val userId = authRepository.getCurrentUserId()
        
        if (userId == null) {
            Log.e(TAG, "userId is NULL — δεν φορτώνονται δεδομένα")
            _uiState.update { it.copy(isLoading = false, errorMessage = "Δεν βρέθηκε χρήστης") }
            return
        }
        
        // Βάρδιες — ξεχωριστό coroutine
        viewModelScope.launch {
            try {
                shiftRepository.getShifts(userId)
                    .collect { shifts ->
                        allShifts = shifts
                        applyStatistics()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Σφάλμα φόρτωσης βαρδιών: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
        
        // Έξοδα — ξεχωριστό coroutine
        viewModelScope.launch {
            try {
                expenseRepository.getExpenses(userId)
                    .collect { expenses ->
                        allExpenses = expenses
                        applyStatistics()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Σφάλμα φόρτωσης εξόδων: ${e.message}", e)
                // Δεν σταματάμε αν αποτύχουν τα έξοδα
            }
        }
    }
    
    /**
     * Αλλάζει την περίοδο φίλτρου.
     */
    fun selectPeriod(period: StatsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        applyStatistics()
    }
    
    /**
     * Φιλτράρει δεδομένα βάσει περιόδου και υπολογίζει στατιστικά μέσω UseCase.
     */
    private fun applyStatistics() {
        val period = _uiState.value.selectedPeriod
        val (startDate, endDate) = getDateRange(period)
        
        // Φιλτράρισμα βάσει χρονικής περιόδου
        val filteredShifts = allShifts.filter { it.date in startDate..endDate }
        val filteredExpenses = allExpenses.filter { it.date in startDate..endDate }
        
        // Υπολογισμός μέσω UseCase
        val result = calculateStatistics(filteredShifts, filteredExpenses)
        
        _uiState.update {
            it.copy(
                isLoading = false,
                grossIncome = result.grossIncome,
                tips = result.tips,
                bonus = result.bonus,
                netIncome = result.netIncome,
                totalExpenses = result.totalExpenses,
                expensesByCategory = result.expensesByCategory,
                totalOrders = result.totalOrders,
                totalHours = result.totalHours,
                totalKilometers = result.totalKilometers,
                totalShifts = result.totalShifts,
                avgIncomePerHour = result.avgIncomePerHour,
                avgOrdersPerShift = result.avgOrdersPerShift,
                avgIncomePerOrder = result.avgIncomePerOrder,
                avgIncomePerKm = result.avgIncomePerKm,
                bestDayDate = result.bestDayDate,
                bestDayIncome = result.bestDayIncome,
                dailyIncomes = result.dailyIncomes
            )
        }
    }
}
