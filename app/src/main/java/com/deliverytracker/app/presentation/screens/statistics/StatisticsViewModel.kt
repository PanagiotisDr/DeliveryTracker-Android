package com.deliverytracker.app.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
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
 * Period filter για στατιστικά.
 */
enum class StatsPeriod(val label: String) {
    TODAY("Σήμερα"),
    WEEK("Εβδομάδα"),
    MONTH("Μήνας"),
    YEAR("Έτος"),
    ALL("Όλα")
}

/**
 * State για τα στατιστικά.
 */
data class StatisticsUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: StatsPeriod = StatsPeriod.MONTH,
    
    // Έσοδα
    val grossIncome: Double = 0.0,
    val tips: Double = 0.0,
    val bonus: Double = 0.0,
    val netIncome: Double = 0.0,
    
    // Έξοδα
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    
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
    
    // Chart data - daily income for the period
    val dailyIncomes: List<Pair<String, Double>> = emptyList()
)

/**
 * ViewModel για τα στατιστικά.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    private var allShifts: List<Shift> = emptyList()
    private var allExpenses: List<Expense> = emptyList()
    
    init {
        loadData()
    }
    
    /**
     * Φορτώνει τα δεδομένα.
     */
    private fun loadData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            shiftRepository.getShifts(userId)
                .catch { /* Ignore errors */ }
                .collect { shifts ->
                    allShifts = shifts
                    calculateStats()
                }
        }
        
        viewModelScope.launch {
            expenseRepository.getExpenses(userId)
                .catch { /* Ignore errors */ }
                .collect { expenses ->
                    allExpenses = expenses
                    calculateStats()
                }
        }
    }
    
    /**
     * Αλλάζει την περίοδο.
     */
    fun selectPeriod(period: StatsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        calculateStats()
    }
    
    /**
     * Υπολογίζει όλα τα στατιστικά.
     */
    private fun calculateStats() {
        val period = _uiState.value.selectedPeriod
        val (startDate, endDate) = getDateRange(period)
        
        val filteredShifts = allShifts.filter { it.date in startDate..endDate }
        val filteredExpenses = allExpenses.filter { it.date in startDate..endDate }
        
        // Βασικά έσοδα
        val grossIncome = filteredShifts.sumOf { it.grossIncome }
        val tips = filteredShifts.sumOf { it.tips }
        val bonus = filteredShifts.sumOf { it.bonus }
        val netIncome = filteredShifts.sumOf { it.netIncome }
        
        // Έξοδα per category
        val expensesByCategory = filteredExpenses
            .groupBy { it.category.displayName }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
        val totalExpenses = filteredExpenses.sumOf { it.amount }
        
        // Στατιστικά
        val totalOrders = filteredShifts.sumOf { it.ordersCount }
        val totalHours = filteredShifts.sumOf { it.hoursWorked }
        val totalKilometers = filteredShifts.sumOf { it.totalKilometers }
        val totalShifts = filteredShifts.size
        
        // Μέσοι όροι
        val avgIncomePerHour = if (totalHours > 0) netIncome / totalHours else 0.0
        val avgOrdersPerShift = if (totalShifts > 0) totalOrders.toDouble() / totalShifts else 0.0
        val avgIncomePerOrder = if (totalOrders > 0) netIncome / totalOrders else 0.0
        val avgIncomePerKm = if (totalKilometers > 0) netIncome / totalKilometers else 0.0
        
        // Best day
        val shiftsByDay = filteredShifts.groupBy { shift ->
            val cal = Calendar.getInstance().apply { timeInMillis = shift.date }
            cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
        }
        val bestDay = shiftsByDay.maxByOrNull { (_, shifts) -> shifts.sumOf { it.netIncome } }
        
        // Daily incomes for chart
        val dailyIncomes = shiftsByDay.entries
            .sortedBy { it.key }
            .takeLast(14) // Last 14 days
            .map { (_, shifts) ->
                val date = Calendar.getInstance().apply { timeInMillis = shifts.first().date }
                val label = "${date.get(Calendar.DAY_OF_MONTH)}/${date.get(Calendar.MONTH) + 1}"
                label to shifts.sumOf { it.netIncome }
            }
        
        _uiState.update {
            it.copy(
                isLoading = false,
                grossIncome = grossIncome,
                tips = tips,
                bonus = bonus,
                netIncome = netIncome,
                totalExpenses = totalExpenses,
                expensesByCategory = expensesByCategory,
                totalOrders = totalOrders,
                totalHours = totalHours,
                totalKilometers = totalKilometers,
                totalShifts = totalShifts,
                avgIncomePerHour = avgIncomePerHour,
                avgOrdersPerShift = avgOrdersPerShift,
                avgIncomePerOrder = avgIncomePerOrder,
                avgIncomePerKm = avgIncomePerKm,
                bestDayDate = bestDay?.value?.firstOrNull()?.date,
                bestDayIncome = bestDay?.value?.sumOf { s -> s.netIncome } ?: 0.0,
                dailyIncomes = dailyIncomes
            )
        }
    }
    
    /**
     * Επιστρέφει το date range για την επιλεγμένη περίοδο.
     */
    private fun getDateRange(period: StatsPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        val startDate = when (period) {
            StatsPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            StatsPeriod.WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            StatsPeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.timeInMillis
            }
            StatsPeriod.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.timeInMillis
            }
            StatsPeriod.ALL -> 0L
        }
        
        return startDate to endDate
    }
}
