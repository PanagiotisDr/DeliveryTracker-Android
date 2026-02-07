package com.deliverytracker.app.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για το ValidateExpenseUseCase.
 *
 * Ελέγχει:
 * 1. Μηδενικό/αρνητικό ποσό
 * 2. Υπέρβαση μέγιστου ποσού
 * 3. Μελλοντική ημερομηνία
 * 4. Boundary values
 */
@DisplayName("ValidateExpenseUseCase")
class ValidateExpenseUseCaseTest {

    private val useCase = ValidateExpenseUseCase()

    // Σταθερή ώρα για deterministic tests
    private val now = 1700000000000L

    // ═══════════════════════════════════════════════════
    // Valid Expense
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Έγκυρο έξοδο → Valid")
    fun `valid expense returns Valid`() {
        val result = useCase(
            amount = 50.0,
            dateMillis = now - 86400000,
            currentTimeMillis = now
        )
        assertTrue(result is ExpenseValidationResult.Valid)
    }

    // ═══════════════════════════════════════════════════
    // Rule 1: Zero / Negative Amount
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Κανόνας 1: Μηδενικό/αρνητικό ποσό")
    inner class ZeroAmount {

        @Test
        @DisplayName("amount = 0 → ZERO_AMOUNT")
        fun `zero amount returns error`() {
            val result = useCase(
                amount = 0.0,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ExpenseValidationResult.Invalid(ExpenseValidationError.ZERO_AMOUNT),
                result
            )
        }

        @Test
        @DisplayName("Αρνητικό ποσό → ZERO_AMOUNT")
        fun `negative amount returns error`() {
            val result = useCase(
                amount = -5.0,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ExpenseValidationResult.Invalid(ExpenseValidationError.ZERO_AMOUNT),
                result
            )
        }
    }

    // ═══════════════════════════════════════════════════
    // Rule 2: Max Amount
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Κανόνας 2: Υπέρβαση μέγιστου ποσού")
    inner class MaxAmount {

        @Test
        @DisplayName("10001€ → EXCEEDS_MAX_AMOUNT")
        fun `amount over max returns error`() {
            val result = useCase(
                amount = 10001.0,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertEquals(
                ExpenseValidationResult.Invalid(ExpenseValidationError.EXCEEDS_MAX_AMOUNT),
                result
            )
        }

        @Test
        @DisplayName("Ακριβώς 10000€ → Valid")
        fun `exactly max amount is valid`() {
            val result = useCase(
                amount = 10000.0,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertTrue(result is ExpenseValidationResult.Valid)
        }

        @Test
        @DisplayName("9999.99€ → Valid")
        fun `just under max is valid`() {
            val result = useCase(
                amount = 9999.99,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertTrue(result is ExpenseValidationResult.Valid)
        }
    }

    // ═══════════════════════════════════════════════════
    // Rule 3: Future Date
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Κανόνας 3: Μελλοντική ημερομηνία → FUTURE_DATE")
    fun `future date returns error`() {
        val result = useCase(
            amount = 50.0,
            dateMillis = now + 86400000, // αύριο
            currentTimeMillis = now
        )
        assertEquals(
            ExpenseValidationResult.Invalid(ExpenseValidationError.FUTURE_DATE),
            result
        )
    }

    // ═══════════════════════════════════════════════════
    // Boundary values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Boundary values")
    inner class Boundaries {

        @Test
        @DisplayName("Πολύ μικρό ποσό (0.01€) → Valid")
        fun `minimum positive amount is valid`() {
            val result = useCase(
                amount = 0.01,
                dateMillis = now - 86400000,
                currentTimeMillis = now
            )
            assertTrue(result is ExpenseValidationResult.Valid)
        }

        @Test
        @DisplayName("Ημερομηνία = τώρα → Valid (not future)")
        fun `current time is valid`() {
            val result = useCase(
                amount = 50.0,
                dateMillis = now,
                currentTimeMillis = now
            )
            assertTrue(result is ExpenseValidationResult.Valid)
        }
    }

    // ═══════════════════════════════════════════════════
    // Priority
    // ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Πολλαπλά σφάλματα → επιστρέφει το πρώτο")
    fun `multiple errors returns first one`() {
        // Μηδέν ποσό ΚΑΙ μελλοντική ημερομηνία
        val result = useCase(
            amount = 0.0,
            dateMillis = now + 86400000,
            currentTimeMillis = now
        )
        assertEquals(
            ExpenseValidationResult.Invalid(ExpenseValidationError.ZERO_AMOUNT),
            result
        )
    }
}
