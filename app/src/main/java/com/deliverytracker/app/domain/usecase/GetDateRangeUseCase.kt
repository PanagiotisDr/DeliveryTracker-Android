package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.presentation.screens.statistics.StatsPeriod
import java.util.*
import javax.inject.Inject

/**
 * UseCase: Επιστρέφει το date range (startDate, endDate) για μια επιλεγμένη περίοδο.
 * 
 * ΣΗΜΑΝΤΙΚΟ: Ο date picker αποθηκεύει τις ημερομηνίες στις 12:00 μεσημέρι (noon).
 * Γι' αυτό, το endDate πρέπει να είναι τέλος ημέρας (23:59:59.999) και όχι η
 * τρέχουσα στιγμή, αλλιώς πρωινές αναζητήσεις φιλτράρουν βάρδιες "σήμερα".
 */
class GetDateRangeUseCase @Inject constructor() {
    
    /**
     * @param period Η επιλεγμένη περίοδος (TODAY, WEEK, MONTH, YEAR, ALL)
     * @return Ζεύγος (startDate, endDate) σε millis
     */
    operator fun invoke(period: StatsPeriod): Pair<Long, Long> {
        val now = Calendar.getInstance()
        
        // endDate = τέλος σημερινής ημέρας (23:59:59.999)
        val endCal = now.clone() as Calendar
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val endDate = endCal.timeInMillis
        
        // startDate — ξεχωριστό Calendar instance για κάθε περίοδο
        val startDate = when (period) {
            StatsPeriod.TODAY -> {
                val cal = now.clone() as Calendar
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            StatsPeriod.WEEK -> {
                val cal = now.clone() as Calendar
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            StatsPeriod.MONTH -> {
                val cal = now.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            StatsPeriod.YEAR -> {
                val cal = now.clone() as Calendar
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            StatsPeriod.ALL -> 0L
        }
        
        return startDate to endDate
    }
}
