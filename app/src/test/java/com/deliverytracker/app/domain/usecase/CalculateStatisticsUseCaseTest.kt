package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.Shift
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Unit Tests για το CalculateStatisticsUseCase.
 *
 * Ελέγχει:
 * - Αθροίσματα εσόδων (gross, tips, bonus, net)
 * - Κατηγοριοποίηση εξόδων
 * - Μέσους όρους (ανά ώρα, ανά παραγγελία, ανά χιλιόμετρο)
 * - Ασφαλή διαίρεση με μηδέν
 * - Εύρεση καλύτερης ημέρας
 * - Κενά δεδομένα
 */
class CalculateStatisticsUseCaseTest {

    private lateinit var useCase: CalculateStatisticsUseCase

    @BeforeEach
    fun setup() {
        useCase = CalculateStatisticsUseCase()
    }

    // ═══════════════════════════════════════════════════
    // Βοηθητικές μέθοδοι
    // ═══════════════════════════════════════════════════

    private fun createShift(
        grossIncome: Double = 50.0,
        tips: Double = 10.0,
        bonus: Double = 5.0,
        orders: Int = 8,
        hours: Int = 5,
        minutes: Int = 30,
        km: Double = 45.0,
        daysAgo: Int = 0
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
            workedHours = hours,
            workedMinutes = minutes,
            kilometers = km
        )
    }

    private fun createExpense(
        amount: Double = 20.0,
        category: ExpenseCategory = ExpenseCategory.FUEL
    ) = Expense(
        id = UUID.randomUUID().toString(),
        amount = amount,
        category = category
    )

    @Nested
    @DisplayName("Κενά Δεδομένα")
    inner class EmptyData {

        @Test
        @DisplayName("Επιστρέφει μηδενικά στατιστικά χωρίς βάρδιες")
        fun returnsZerosForEmptyShifts() {
            val result = useCase(emptyList(), emptyList())

            assertEquals(0.0, result.grossIncome, 0.001)
            assertEquals(0.0, result.netIncome, 0.001)
            assertEquals(0, result.totalOrders)
            assertEquals(0, result.totalShifts)
            assertEquals(0.0, result.totalExpenses, 0.001)
        }

        @Test
        @DisplayName("Μέσοι όροι = 0 αν δεν υπάρχουν δεδομένα (ασφαλής διαίρεση)")
        fun averagesAreZeroForEmptyData() {
            val result = useCase(emptyList(), emptyList())

            assertEquals(0.0, result.avgIncomePerHour, 0.001)
            assertEquals(0.0, result.avgOrdersPerShift, 0.001)
            assertEquals(0.0, result.avgIncomePerOrder, 0.001)
            assertEquals(0.0, result.avgIncomePerKm, 0.001)
        }

        @Test
        @DisplayName("Καλύτερη μέρα = null αν δεν υπάρχουν βάρδιες")
        fun bestDayIsNullForEmptyData() {
            val result = useCase(emptyList(), emptyList())

            assertNull(result.bestDayDate)
            assertEquals(0.0, result.bestDayIncome, 0.001)
        }
    }

    @Nested
    @DisplayName("Υπολογισμός Εσόδων")
    inner class IncomeCalculation {

        @Test
        @DisplayName("Αθροίζει σωστά gross, tips, bonus, net")
        fun sumsIncomeCorrectly() {
            val shifts = listOf(
                createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0),
                createShift(grossIncome = 60.0, tips = 12.0, bonus = 8.0)
            )
            val result = useCase(shifts, emptyList())

            assertEquals(110.0, result.grossIncome, 0.001)
            assertEquals(22.0, result.tips, 0.001)
            assertEquals(13.0, result.bonus, 0.001)
            // netIncome = (50+10+5) + (60+12+8) = 65 + 80 = 145
            assertEquals(145.0, result.netIncome, 0.001)
        }
    }

    @Nested
    @DisplayName("Υπολογισμός Εξόδων")
    inner class ExpenseCalculation {

        @Test
        @DisplayName("Αθροίζει σωστά τα συνολικά έξοδα")
        fun sumsTotalExpenses() {
            val expenses = listOf(
                createExpense(amount = 30.0, category = ExpenseCategory.FUEL),
                createExpense(amount = 50.0, category = ExpenseCategory.MAINTENANCE),
                createExpense(amount = 20.0, category = ExpenseCategory.FUEL)
            )
            val result = useCase(emptyList(), expenses)

            assertEquals(100.0, result.totalExpenses, 0.001)
        }

        @Test
        @DisplayName("Ομαδοποιεί σωστά ανά κατηγορία")
        fun groupsByCategory() {
            val expenses = listOf(
                createExpense(amount = 30.0, category = ExpenseCategory.FUEL),
                createExpense(amount = 50.0, category = ExpenseCategory.MAINTENANCE),
                createExpense(amount = 20.0, category = ExpenseCategory.FUEL)
            )
            val result = useCase(emptyList(), expenses)

            // FUEL: 30 + 20 = 50
            assertEquals(50.0, result.expensesByCategory[ExpenseCategory.FUEL]!!, 0.001)
            // MAINTENANCE: 50
            assertEquals(50.0, result.expensesByCategory[ExpenseCategory.MAINTENANCE]!!, 0.001)
        }
    }

    @Nested
    @DisplayName("Μέσοι Όροι")
    inner class Averages {

        @Test
        @DisplayName("Υπολογίζει income per hour σωστά")
        fun calculatesIncomePerHour() {
            // 1 βάρδια: net=65, hours=5.5
            val shifts = listOf(
                createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, hours = 5, minutes = 30)
            )
            val result = useCase(shifts, emptyList())

            // 65 / 5.5 ≈ 11.818
            assertEquals(11.818, result.avgIncomePerHour, 0.01)
        }

        @Test
        @DisplayName("Υπολογίζει orders per shift σωστά")
        fun calculatesOrdersPerShift() {
            val shifts = listOf(
                createShift(orders = 8),
                createShift(orders = 12)
            )
            val result = useCase(shifts, emptyList())

            // (8+12) / 2 = 10
            assertEquals(10.0, result.avgOrdersPerShift, 0.001)
        }

        @Test
        @DisplayName("Υπολογίζει income per km σωστά")
        fun calculatesIncomePerKm() {
            val shifts = listOf(
                createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, km = 65.0)
            )
            val result = useCase(shifts, emptyList())

            // net=65, km=65 → 1.0 €/km
            assertEquals(1.0, result.avgIncomePerKm, 0.001)
        }

        @Test
        @DisplayName("Ασφαλής διαίρεση: 0 χιλιόμετρα")
        fun safeDivisionZeroKm() {
            val shifts = listOf(
                createShift(km = 0.0)
            )
            val result = useCase(shifts, emptyList())

            assertEquals(0.0, result.avgIncomePerKm, 0.001)
        }
    }

    @Nested
    @DisplayName("Καλύτερη Ημέρα")
    inner class BestDay {

        @Test
        @DisplayName("Βρίσκει σωστά την καλύτερη ημέρα")
        fun findsBestDay() {
            val shifts = listOf(
                createShift(grossIncome = 30.0, tips = 5.0, bonus = 2.0, daysAgo = 2),   // net=37
                createShift(grossIncome = 80.0, tips = 15.0, bonus = 10.0, daysAgo = 1), // net=105
                createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, daysAgo = 3)   // net=65
            )
            val result = useCase(shifts, emptyList())

            // Η καλύτερη μέρα πρέπει να είναι daysAgo=1 με net=105
            assertNotNull(result.bestDayDate)
            assertEquals(105.0, result.bestDayIncome, 0.001)
        }
    }

    @Nested
    @DisplayName("Στατιστικά Στοιχεία")
    inner class Stats {

        @Test
        @DisplayName("Μετράει σωστά shifts, orders, hours, km")
        fun countsCorrectly() {
            val shifts = listOf(
                createShift(orders = 8, hours = 5, minutes = 30, km = 45.0),
                createShift(orders = 10, hours = 6, minutes = 0, km = 55.0)
            )
            val result = useCase(shifts, emptyList())

            assertEquals(2, result.totalShifts)
            assertEquals(18, result.totalOrders)
            // 5.5 + 6.0 = 11.5
            assertEquals(11.5, result.totalHours, 0.001)
            assertEquals(100.0, result.totalKilometers, 0.001)
        }
    }
}
