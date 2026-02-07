package com.deliverytracker.app.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για το ValidateShiftUseCase.
 *
 * Ελέγχει όλους τους κανόνες validation:
 * 1. Μηδενικά έσοδα
 * 2. Μηδενική διάρκεια
 * 3. Υπέρβαση 24 ωρών
 * 4. Μηδενικές παραγγελίες
 * 5. Μηδενικά χιλιόμετρα
 * 6. Μελλοντική ημερομηνία
 */
@DisplayName("ValidateShiftUseCase")
class ValidateShiftUseCaseTest {

    private val useCase = ValidateShiftUseCase()

    // Σταθερή ώρα για deterministic tests
    private val now = 1700000000000L

    // ═══════════════════════════════════════════════════
    // Valid Shift
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Έγκυρη βάρδια → Valid")
    fun `valid shift returns Valid`() {
        val result = useCase(
            totalIncome = 100.0,
            totalMinutes = 480,     // 8 ώρες
            ordersCount = 20,
            kilometers = 50.0,
            shiftDateMillis = now - 86400000, // χθες
            currentTimeMillis = now
        )
        assertTrue(result is ShiftValidationResult.Valid)
    }

    // ═══════════════════════════════════════════════════
    // Rule 1: Zero Income
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Κανόνας 1: Μηδενικά έσοδα")
    inner class ZeroIncome {

        @Test
        @DisplayName("totalIncome = 0 → ZERO_INCOME")
        fun `zero income returns error`() {
            val result = useCase(
                totalIncome = 0.0,
                totalMinutes = 480,
                ordersCount = 20,
                kilometers = 50.0,
                shiftDateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ShiftValidationResult.Invalid(ShiftValidationError.ZERO_INCOME),
                result
            )
        }

        @Test
        @DisplayName("Αρνητικά έσοδα → ZERO_INCOME")
        fun `negative income returns error`() {
            val result = useCase(
                totalIncome = -10.0,
                totalMinutes = 480,
                ordersCount = 20,
                kilometers = 50.0,
                shiftDateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ShiftValidationResult.Invalid(ShiftValidationError.ZERO_INCOME),
                result
            )
        }
    }

    // ═══════════════════════════════════════════════════
    // Rule 2: Zero Duration
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Κανόνας 2: Μηδενική διάρκεια με έσοδα → ZERO_DURATION")
    fun `zero duration with income returns error`() {
        val result = useCase(
            totalIncome = 50.0,
            totalMinutes = 0,
            ordersCount = 10,
            kilometers = 20.0,
            shiftDateMillis = now - 86400000,
            currentTimeMillis = now
        )
        assertEquals(
            ShiftValidationResult.Invalid(ShiftValidationError.ZERO_DURATION),
            result
        )
    }

    // ═══════════════════════════════════════════════════
    // Rule 3: Over 24 Hours
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Κανόνας 3: Υπέρβαση 24 ωρών")
    inner class Over24Hours {

        @Test
        @DisplayName("1441 λεπτά → OVER_24_HOURS")
        fun `1441 minutes returns error`() {
            val result = useCase(
                totalIncome = 100.0,
                totalMinutes = 1441,
                ordersCount = 20,
                kilometers = 50.0,
                shiftDateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ShiftValidationResult.Invalid(ShiftValidationError.OVER_24_HOURS),
                result
            )
        }

        @Test
        @DisplayName("1440 λεπτά (ακριβώς 24h) → Valid")
        fun `exactly 1440 minutes is valid`() {
            val result = useCase(
                totalIncome = 100.0,
                totalMinutes = 1440,
                ordersCount = 20,
                kilometers = 50.0,
                shiftDateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertTrue(result is ShiftValidationResult.Valid)
        }
    }

    // ═══════════════════════════════════════════════════
    // Rule 4: Zero Orders
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Κανόνας 4: Μηδέν παραγγελίες με έσοδα → ZERO_ORDERS")
    fun `zero orders with income returns error`() {
        val result = useCase(
            totalIncome = 100.0,
            totalMinutes = 480,
            ordersCount = 0,
            kilometers = 50.0,
            shiftDateMillis = now - 86400000,
            currentTimeMillis = now
        )
        assertEquals(
            ShiftValidationResult.Invalid(ShiftValidationError.ZERO_ORDERS),
            result
        )
    }

    // ═══════════════════════════════════════════════════
    // Rule 5: Zero Kilometers
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Κανόνας 5: Μηδέν χλμ με παραγγελίες → ZERO_KILOMETERS")
    fun `zero km with orders returns error`() {
        val result = useCase(
            totalIncome = 100.0,
            totalMinutes = 480,
            ordersCount = 20,
            kilometers = 0.0,
            shiftDateMillis = now - 86400000,
            currentTimeMillis = now
        )
        assertEquals(
            ShiftValidationResult.Invalid(ShiftValidationError.ZERO_KILOMETERS),
            result
        )
    }

    // ═══════════════════════════════════════════════════
    // Rule 6: Future Date
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Κανόνας 6: Μελλοντική ημερομηνία → FUTURE_DATE")
    fun `future date returns error`() {
        val result = useCase(
            totalIncome = 100.0,
            totalMinutes = 480,
            ordersCount = 20,
            kilometers = 50.0,
            shiftDateMillis = now + 86400000, // αύριο
            currentTimeMillis = now
        )
        assertEquals(
            ShiftValidationResult.Invalid(ShiftValidationError.FUTURE_DATE),
            result
        )
    }

    // ═══════════════════════════════════════════════════
    // Priority (πρώτος κανόνας που αποτυγχάνει)
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Πολλαπλά σφάλματα → επιστρέφει το πρώτο")
    fun `multiple errors returns first one`() {
        // Μηδέν έσοδα ΚΑΙ μηδέν διάρκεια → πρώτα ελέγχεται το income
        val result = useCase(
            totalIncome = 0.0,
            totalMinutes = 0,
            ordersCount = 0,
            kilometers = 0.0,
            shiftDateMillis = now + 86400000,
            currentTimeMillis = now
        )
        assertEquals(
            ShiftValidationResult.Invalid(ShiftValidationError.ZERO_INCOME),
            result
        )
    }
}
