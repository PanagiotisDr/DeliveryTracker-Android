package com.deliverytracker.app.presentation.screens.shifts

import com.deliverytracker.app.domain.model.Shift
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για τo ShiftFormUiState.
 *
 * Σημείωση: Η πλήρης ViewModel instantiation απαιτεί
 * Android context (FirebaseAuth + SavedStateHandle).
 * Αυτά τα tests ελέγχουν τη σωστή δομή του UiState,
 * τις default τιμές και τις state transitions.
 *
 * Η integration testing γίνεται σε instrumented tests.
 */
@DisplayName("ShiftFormUiState")
class ShiftFormViewModelTest {

    // ═══════════════════════════════════════════════════
    // Default Values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Προεπιλεγμένες τιμές")
    inner class DefaultValues {

        @Test
        @DisplayName("Αρχικό state → isLoading = false")
        fun `initial state has loading false`() {
            val state = ShiftFormUiState()
            assertFalse(state.isLoading)
        }

        @Test
        @DisplayName("Αρχικό state → isSaved = false")
        fun `initial state has saved false`() {
            val state = ShiftFormUiState()
            assertFalse(state.isSaved)
        }

        @Test
        @DisplayName("Αρχικό state → κενά form fields")
        fun `initial state has empty form fields`() {
            val state = ShiftFormUiState()
            assertEquals("", state.dateText)
            assertEquals("", state.workedHours)
            assertEquals("", state.workedMinutes)
            assertEquals("", state.grossIncome)
            assertEquals("", state.tips)
            assertEquals("", state.bonus)
            assertEquals("", state.ordersCount)
            assertEquals("", state.kilometers)
            assertEquals("", state.notes)
        }

        @Test
        @DisplayName("Αρχικό state → null errors")
        fun `initial state has null errors`() {
            val state = ShiftFormUiState()
            assertNull(state.errorResId)
            assertNull(state.errorMessage)
        }

        @Test
        @DisplayName("Αρχικό state → null existingShift")
        fun `initial state has null existing shift`() {
            val state = ShiftFormUiState()
            assertNull(state.existingShift)
        }
    }

    // ═══════════════════════════════════════════════════
    // State Transitions
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("State transitions")
    inner class StateTransitions {

        @Test
        @DisplayName("Create mode → συμπλήρωση form fields")
        fun `create mode form filling`() {
            val state = ShiftFormUiState().copy(
                dateText = "07/02/2026",
                workedHours = "8",
                workedMinutes = "30",
                grossIncome = "80,00",
                tips = "15,50",
                bonus = "5,00",
                ordersCount = "22",
                kilometers = "65,5",
                notes = "Βροχερή μέρα"
            )

            assertEquals("07/02/2026", state.dateText)
            assertEquals("8", state.workedHours)
            assertEquals("30", state.workedMinutes)
            assertEquals("80,00", state.grossIncome)
            assertEquals("15,50", state.tips)
            assertEquals("5,00", state.bonus)
            assertEquals("22", state.ordersCount)
            assertEquals("65,5", state.kilometers)
            assertEquals("Βροχερή μέρα", state.notes)
        }

        @Test
        @DisplayName("Edit mode → φορτωμένο existing shift")
        fun `edit mode with existing shift`() {
            val existingShift = Shift(
                id = "shift-1",
                grossIncome = 80.0,
                tips = 15.0,
                workedHours = 8,
                workedMinutes = 30
            )
            val state = ShiftFormUiState(
                grossIncome = "80,0",
                tips = "15,0",
                workedHours = "8",
                workedMinutes = "30",
                existingShift = existingShift
            )

            assertNotNull(state.existingShift)
            assertEquals("shift-1", state.existingShift?.id)
        }

        @Test
        @DisplayName("Saving transition → isLoading = true")
        fun `saving transition`() {
            val state = ShiftFormUiState(
                grossIncome = "80,00",
                workedHours = "8"
            )
            val saving = state.copy(isLoading = true)

            assertTrue(saving.isLoading)
            assertFalse(saving.isSaved)
        }

        @Test
        @DisplayName("Saved transition → isSaved = true")
        fun `saved transition`() {
            val state = ShiftFormUiState(isLoading = true)
            val saved = state.copy(isLoading = false, isSaved = true)

            assertFalse(saved.isLoading)
            assertTrue(saved.isSaved)
        }

        @Test
        @DisplayName("Validation error transition (με errorResId)")
        fun `validation error transition`() {
            val state = ShiftFormUiState()
            val withError = state.copy(errorResId = 123)

            assertEquals(123, withError.errorResId)
            assertNull(withError.errorMessage)
        }

        @Test
        @DisplayName("Dynamic error transition (με errorMessage)")
        fun `dynamic error transition`() {
            val state = ShiftFormUiState()
            val withError = state.copy(errorMessage = "Σφάλμα Firebase")

            assertNull(withError.errorResId)
            assertEquals("Σφάλμα Firebase", withError.errorMessage)
        }

        @Test
        @DisplayName("Clear όλα τα errors")
        fun `clear all errors`() {
            val state = ShiftFormUiState(errorResId = 123, errorMessage = "Κάποιο σφάλμα")
            val cleared = state.copy(errorResId = null, errorMessage = null)

            assertNull(cleared.errorResId)
            assertNull(cleared.errorMessage)
        }
    }

    // ═══════════════════════════════════════════════════
    // Data integrity
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Data integrity")
    inner class DataIntegrity {

        @Test
        @DisplayName("Copy δεν αλλάζει unrelated fields")
        fun `copy preserves unrelated fields`() {
            val state = ShiftFormUiState(
                grossIncome = "80,00",
                tips = "15,00",
                ordersCount = "20"
            )
            val updated = state.copy(notes = "Test note")

            // Τα υπόλοιπα παραμένουν αμετάβλητα
            assertEquals("80,00", updated.grossIncome)
            assertEquals("15,00", updated.tips)
            assertEquals("20", updated.ordersCount)
            assertEquals("Test note", updated.notes)
        }

        @Test
        @DisplayName("Equality βασίζεται σε τιμές")
        fun `equality is value based`() {
            val a = ShiftFormUiState(grossIncome = "50,00")
            val b = ShiftFormUiState(grossIncome = "50,00")
            assertEquals(a, b)
        }
    }
}
