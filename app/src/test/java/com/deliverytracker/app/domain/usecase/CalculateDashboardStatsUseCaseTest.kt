package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Shift
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Unit Tests για το CalculateDashboardStatsUseCase.
 *
 * Ελέγχει:
 * - Σωστό φιλτράρισμα σημερινών βαρδιών
 * - Σωστό φιλτράρισμα μηνιαίων βαρδιών
 * - Αθροίσματα εσόδων, παραγγελιών, ωρών
 * - Κενή λίστα βαρδιών
 */
class CalculateDashboardStatsUseCaseTest {

    private lateinit var useCase: CalculateDashboardStatsUseCase

    @BeforeEach
    fun setup() {
        useCase = CalculateDashboardStatsUseCase()
    }

    // ═══════════════════════════════════════════════════
    // Βοηθητικές μέθοδοι για δημιουργία test data
    // ═══════════════════════════════════════════════════

    /** Δημιουργεί βάρδια για σήμερα με τα δοσμένα στοιχεία */
    private fun todayShift(
        grossIncome: Double = 50.0,
        tips: Double = 10.0,
        bonus: Double = 5.0,
        orders: Int = 8,
        hours: Int = 5,
        minutes: Int = 30
    ) = Shift(
        id = UUID.randomUUID().toString(),
        date = System.currentTimeMillis(),
        grossIncome = grossIncome,
        tips = tips,
        bonus = bonus,
        ordersCount = orders,
        workedHours = hours,
        workedMinutes = minutes
    )

    /** Δημιουργεί βάρδια για συγκεκριμένη ημέρα */
    private fun shiftOnDate(
        daysAgo: Int,
        grossIncome: Double = 40.0,
        tips: Double = 8.0,
        bonus: Double = 3.0,
        orders: Int = 6
    ): Shift {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }
        return Shift(
            id = UUID.randomUUID().toString(),
            date = cal.timeInMillis,
            grossIncome = grossIncome,
            tips = tips,
            bonus = bonus,
            ordersCount = orders,
            workedHours = 4,
            workedMinutes = 0
        )
    }

    @Nested
    @DisplayName("Κενή Λίστα Βαρδιών")
    inner class EmptyShifts {

        @Test
        @DisplayName("Επιστρέφει μηδενικά στατιστικά όταν δεν υπάρχουν βάρδιες")
        fun returnsZeroStatsForEmptyList() {
            val result = useCase(emptyList())

            assertEquals(0.0, result.todayNetIncome, 0.001)
            assertEquals(0, result.todayOrders)
            assertEquals(0.0, result.todayHours, 0.001)
            assertEquals(0.0, result.monthNetIncome, 0.001)
            assertEquals(0, result.monthOrders)
            assertEquals(0, result.monthShifts)
        }
    }

    @Nested
    @DisplayName("Σημερινά Στατιστικά")
    inner class TodayStats {

        @Test
        @DisplayName("Υπολογίζει σωστά τα σημερινά στατιστικά για μία βάρδια")
        fun calculatesCorrectlyForSingleTodayShift() {
            val shift = todayShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, orders = 8)
            val result = useCase(listOf(shift))

            // netIncome = grossIncome + tips + bonus = 65.0
            assertEquals(65.0, result.todayNetIncome, 0.001)
            assertEquals(8, result.todayOrders)
        }

        @Test
        @DisplayName("Αθροίζει σημερινές βάρδιες σωστά")
        fun sumsTodayShiftsCorrectly() {
            val shifts = listOf(
                todayShift(grossIncome = 30.0, tips = 5.0, bonus = 2.0, orders = 5),
                todayShift(grossIncome = 40.0, tips = 8.0, bonus = 3.0, orders = 6)
            )
            val result = useCase(shifts)

            // (30+5+2) + (40+8+3) = 37 + 51 = 88
            assertEquals(88.0, result.todayNetIncome, 0.001)
            assertEquals(11, result.todayOrders)
        }

        @Test
        @DisplayName("Δεν μετράει παλιές βάρδιες ως σημερινές")
        fun excludesOldShiftsFromToday() {
            val shifts = listOf(
                todayShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, orders = 8),
                shiftOnDate(daysAgo = 3, grossIncome = 100.0, tips = 20.0, bonus = 10.0, orders = 15)
            )
            val result = useCase(shifts)

            // Μόνο η σημερινή βάρδια
            assertEquals(65.0, result.todayNetIncome, 0.001)
            assertEquals(8, result.todayOrders)
        }

        @Test
        @DisplayName("Υπολογίζει ώρες εργασίας σε δεκαδική μορφή")
        fun calculatesHoursInDecimalFormat() {
            val shift = todayShift(hours = 5, minutes = 30)
            val result = useCase(listOf(shift))

            // 5 ώρες + 30 λεπτά = 5.5 ώρες
            assertEquals(5.5, result.todayHours, 0.001)
        }
    }

    @Nested
    @DisplayName("Μηνιαία Στατιστικά")
    inner class MonthlyStats {

        @Test
        @DisplayName("Μετράει πρόσφατες βάρδιες στον τρέχοντα μήνα")
        fun countsRecentShiftsInCurrentMonth() {
            // Δημιουργία βάρδιας πριν 2 ημέρες (αν είμαστε πάνω από 2ης του μήνα)
            val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (dayOfMonth < 3) return // Skip αν δεν μπορούμε να πάμε πίσω

            val shifts = listOf(
                todayShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, orders = 8),
                shiftOnDate(daysAgo = 1, grossIncome = 40.0, tips = 8.0, bonus = 3.0, orders = 6)
            )
            val result = useCase(shifts)

            // Αν δεν αλλάξαμε μήνα, πρέπει να μετράει 2 βάρδιες
            val cal = Calendar.getInstance()
            val currentMonth = cal.get(Calendar.MONTH)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayMonth = cal.get(Calendar.MONTH)

            if (currentMonth == yesterdayMonth) {
                assertEquals(2, result.monthShifts)
                // (50+10+5) + (40+8+3) = 65+51 = 116
                assertEquals(116.0, result.monthNetIncome, 0.001)
            }
        }
    }
}
