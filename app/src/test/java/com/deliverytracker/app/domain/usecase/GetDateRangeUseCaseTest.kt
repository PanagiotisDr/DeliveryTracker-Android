package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.presentation.screens.statistics.StatsPeriod
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Unit Tests για το GetDateRangeUseCase.
 *
 * Ελέγχει:
 * - Κάθε StatsPeriod επιστρέφει σωστό εύρος
 * - TODAY: startDate = αρχή ημέρας
 * - WEEK: startDate = 7 μέρες πριν
 * - MONTH: startDate = 1η τρέχοντος μήνα
 * - YEAR: startDate = 1η Ιανουαρίου
 * - ALL: startDate = 0
 */
class GetDateRangeUseCaseTest {

    private lateinit var useCase: GetDateRangeUseCase

    @BeforeEach
    fun setup() {
        useCase = GetDateRangeUseCase()
    }

    @Test
    @DisplayName("TODAY: startDate πρέπει να είναι αρχή σημερινής ημέρας")
    fun todayStartDateIsStartOfDay() {
        val (startDate, endDate) = useCase(StatsPeriod.TODAY)

        // Ελέγχουμε ότι start <= end
        assertTrue(startDate <= endDate, "startDate πρέπει να είναι <= endDate")

        // Ελέγχουμε ότι startDate είναι σήμερα στις 00:00
        val cal = Calendar.getInstance().apply { timeInMillis = startDate }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY), "Η ώρα πρέπει να είναι 00")
        assertEquals(0, cal.get(Calendar.MINUTE), "Τα λεπτά πρέπει να είναι 00")
    }

    @Test
    @DisplayName("WEEK: startDate ~7 ημέρες πριν")
    fun weekStartDateIs7DaysAgo() {
        val (startDate, endDate) = useCase(StatsPeriod.WEEK)

        assertTrue(startDate <= endDate)

        // Διαφορά πρέπει να είναι περίπου 7 ημέρες
        val diffDays = (endDate - startDate) / (1000 * 60 * 60 * 24)
        assertTrue(diffDays in 6..8, "Η διαφορά πρέπει να είναι ~7 ημέρες, ήταν $diffDays")
    }

    @Test
    @DisplayName("MONTH: startDate = 1η τρέχοντος μήνα")
    fun monthStartDateIsFirstOfMonth() {
        val (startDate, endDate) = useCase(StatsPeriod.MONTH)

        assertTrue(startDate <= endDate)

        val cal = Calendar.getInstance().apply { timeInMillis = startDate }
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH), "Η ημέρα πρέπει να είναι 1η")
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY), "Η ώρα πρέπει να είναι 00")
    }

    @Test
    @DisplayName("YEAR: startDate = 1η Ιανουαρίου")
    fun yearStartDateIsJanFirst() {
        val (startDate, endDate) = useCase(StatsPeriod.YEAR)

        assertTrue(startDate <= endDate)

        val cal = Calendar.getInstance().apply { timeInMillis = startDate }
        assertEquals(1, cal.get(Calendar.DAY_OF_YEAR), "Η ημέρα πρέπει να είναι 1η του χρόνου")
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY), "Η ώρα πρέπει να είναι 00")
    }

    @Test
    @DisplayName("ALL: startDate = 0 (epoch)")
    fun allStartDateIsZero() {
        val (startDate, endDate) = useCase(StatsPeriod.ALL)

        assertEquals(0L, startDate, "Για ALL, startDate πρέπει να είναι 0")
        assertTrue(endDate > 0, "endDate πρέπει να είναι > 0")
    }

    @Test
    @DisplayName("Όλες οι περίοδοι: startDate <= endDate")
    fun allPeriodsHaveValidRange() {
        StatsPeriod.entries.forEach { period ->
            val (start, end) = useCase(period)
            assertTrue(start <= end, "Για $period: startDate ($start) πρέπει να είναι <= endDate ($end)")
        }
    }
}
