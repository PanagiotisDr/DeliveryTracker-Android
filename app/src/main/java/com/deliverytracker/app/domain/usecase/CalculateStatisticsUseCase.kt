package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.Shift
import java.util.*
import javax.inject.Inject

/**
 * Αποτελέσματα υπολογισμού στατιστικών.
 */
data class StatisticsResult(
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
 * UseCase: Υπολογίζει λεπτομερή στατιστικά για μια περίοδο.
 * Εξάγει την business logic από το StatisticsViewModel.calculateStats().
 */
class CalculateStatisticsUseCase @Inject constructor() {
    
    /**
     * @param shifts Βάρδιες εντός περιόδου
     * @param expenses Έξοδα εντός περιόδου
     * @return Πλήρη στατιστικά
     */
    operator fun invoke(
        shifts: List<Shift>,
        expenses: List<Expense>
    ): StatisticsResult {
        // Βασικά έσοδα
        val grossIncome = shifts.sumOf { it.grossIncome }
        val tips = shifts.sumOf { it.tips }
        val bonus = shifts.sumOf { it.bonus }
        val netIncome = shifts.sumOf { it.netIncome }
        
        // Έξοδα ανά κατηγορία
        val expensesByCategory = expenses
            .groupBy { it.category }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
        val totalExpenses = expenses.sumOf { it.amount }
        
        // Στατιστικά
        val totalOrders = shifts.sumOf { it.ordersCount }
        val totalHours = shifts.sumOf { it.hoursWorked }
        val totalKilometers = shifts.sumOf { it.totalKilometers }
        val totalShifts = shifts.size
        
        // Μέσοι όροι — ασφαλής διαίρεση με μηδέν
        val avgIncomePerHour = if (totalHours > 0) netIncome / totalHours else 0.0
        val avgOrdersPerShift = if (totalShifts > 0) totalOrders.toDouble() / totalShifts else 0.0
        val avgIncomePerOrder = if (totalOrders > 0) netIncome / totalOrders else 0.0
        val avgIncomePerKm = if (totalKilometers > 0) netIncome / totalKilometers else 0.0
        
        // Εύρεση καλύτερης ημέρας
        val shiftsByDay = shifts.groupBy { shift ->
            val cal = Calendar.getInstance().apply { timeInMillis = shift.date }
            cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
        }
        val bestDay = shiftsByDay.maxByOrNull { (_, dayShifts) -> 
            dayShifts.sumOf { it.netIncome } 
        }
        
        // Ημερήσια έσοδα για γράφημα (τελευταίες 14 ημέρες)
        val dailyIncomes = shiftsByDay.entries
            .sortedBy { it.key }
            .takeLast(14)
            .map { (_, dayShifts) ->
                val date = Calendar.getInstance().apply { timeInMillis = dayShifts.first().date }
                val label = "${date.get(Calendar.DAY_OF_MONTH)}/${date.get(Calendar.MONTH) + 1}"
                label to dayShifts.sumOf { it.netIncome }
            }
        
        return StatisticsResult(
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
