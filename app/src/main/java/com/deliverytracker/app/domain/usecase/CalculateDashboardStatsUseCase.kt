package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Shift
import java.util.*
import javax.inject.Inject

/**
 * Αποτελέσματα στατιστικών Dashboard.
 */
data class DashboardStats(
    // Σημερινά
    val todayNetIncome: Double = 0.0,
    val todayOrders: Int = 0,
    val todayHours: Double = 0.0,
    val todayBonus: Double = 0.0,
    // Μηνιαία
    val monthNetIncome: Double = 0.0,
    val monthOrders: Int = 0,
    val monthShifts: Int = 0
)

/**
 * UseCase: Υπολογίζει τα στατιστικά του Dashboard (σημερινά & μηνιαία).
 * Εξάγει την business logic από το DashboardViewModel σε testable μονάδα.
 */
class CalculateDashboardStatsUseCase @Inject constructor() {
    
    /**
     * Φιλτράρει τις βάρδιες και υπολογίζει σημερινά + μηνιαία στατιστικά.
     * 
     * @param shifts Όλες οι βάρδιες του χρήστη
     * @return Στατιστικά για σήμερα και τον τρέχοντα μήνα
     */
    operator fun invoke(shifts: List<Shift>): DashboardStats {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val thisMonth = calendar.get(Calendar.MONTH)
        val thisYear = calendar.get(Calendar.YEAR)
        
        // Φιλτράρισμα σημερινών βαρδιών
        val todayShifts = shifts.filter { shift ->
            val shiftCal = Calendar.getInstance().apply { timeInMillis = shift.date }
            shiftCal.get(Calendar.DAY_OF_YEAR) == today &&
            shiftCal.get(Calendar.YEAR) == thisYear
        }
        
        // Φιλτράρισμα μηνιαίων βαρδιών
        val monthShifts = shifts.filter { shift ->
            val shiftCal = Calendar.getInstance().apply { timeInMillis = shift.date }
            shiftCal.get(Calendar.MONTH) == thisMonth &&
            shiftCal.get(Calendar.YEAR) == thisYear
        }
        
        return DashboardStats(
            todayNetIncome = todayShifts.sumOf { it.netIncome },
            todayOrders = todayShifts.sumOf { it.ordersCount },
            todayHours = todayShifts.sumOf { it.hoursWorked },
            todayBonus = todayShifts.sumOf { it.bonus },
            monthNetIncome = monthShifts.sumOf { it.netIncome },
            monthOrders = monthShifts.sumOf { it.ordersCount },
            monthShifts = monthShifts.size
        )
    }
}
