package com.deliverytracker.app.presentation.screens.expenses

import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.PaymentMethod
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για τo ExpenseFormUiState.
 *
 * Σημείωση: Η πλήρης ViewModel instantiation απαιτεί
 * Android context (FirebaseAuth + SavedStateHandle).
 * Αυτά τα tests ελέγχουν τη σωστή δομή του UiState,
 * τις default τιμές και τις state transitions.
 *
 * Η integration testing γίνεται σε instrumented tests.
 */
@DisplayName("ExpenseFormUiState")
class ExpenseFormViewModelTest {

    // ═══════════════════════════════════════════════════
    // Default Values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Προεπιλεγμένες τιμές")
    inner class DefaultValues {

        @Test
        @DisplayName("Αρχικό state → isLoading = false")
        fun `initial state has loading false`() {
            val state = ExpenseFormUiState()
            assertFalse(state.isLoading)
        }

        @Test
        @DisplayName("Αρχικό state → isSaved = false")
        fun `initial state has saved false`() {
            val state = ExpenseFormUiState()
            assertFalse(state.isSaved)
        }

        @Test
        @DisplayName("Αρχικό state → default category FUEL")
        fun `initial state has fuel category`() {
            val state = ExpenseFormUiState()
            assertEquals(ExpenseCategory.FUEL, state.category)
        }

        @Test
        @DisplayName("Αρχικό state → default payment CASH")
        fun `initial state has cash payment`() {
            val state = ExpenseFormUiState()
            assertEquals(PaymentMethod.CASH, state.paymentMethod)
        }

        @Test
        @DisplayName("Αρχικό state → κενό amount")
        fun `initial state has empty amount`() {
            val state = ExpenseFormUiState()
            assertEquals("", state.amount)
        }

        @Test
        @DisplayName("Αρχικό state → null errors")
        fun `initial state has null errors`() {
            val state = ExpenseFormUiState()
            assertNull(state.errorResId)
            assertNull(state.errorMessage)
        }
    }

    // ═══════════════════════════════════════════════════
    // State Transitions
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("State transitions")
    inner class StateTransitions {

        @Test
        @DisplayName("Συμπλήρωση form → σωστές τιμές")
        fun `form filling produces correct values`() {
            val state = ExpenseFormUiState().copy(
                dateText = "07/02/2026",
                category = ExpenseCategory.MAINTENANCE,
                amount = "45,50",
                paymentMethod = PaymentMethod.CARD,
                notes = "Αλλαγή λαδιών"
            )

            assertEquals("07/02/2026", state.dateText)
            assertEquals(ExpenseCategory.MAINTENANCE, state.category)
            assertEquals("45,50", state.amount)
            assertEquals(PaymentMethod.CARD, state.paymentMethod)
            assertEquals("Αλλαγή λαδιών", state.notes)
        }

        @Test
        @DisplayName("Edit mode → φορτωμένο existing expense")
        fun `edit mode with existing expense`() {
            val existing = Expense(
                id = "exp-1",
                amount = 45.50,
                category = ExpenseCategory.MAINTENANCE,
                paymentMethod = PaymentMethod.CARD
            )
            val state = ExpenseFormUiState(
                amount = "45,5",
                category = ExpenseCategory.MAINTENANCE,
                paymentMethod = PaymentMethod.CARD,
                existingExpense = existing
            )

            assertNotNull(state.existingExpense)
            assertEquals("exp-1", state.existingExpense?.id)
        }

        @Test
        @DisplayName("Saving transition")
        fun `saving transition`() {
            val state = ExpenseFormUiState(amount = "45,50")
            val saving = state.copy(isLoading = true)

            assertTrue(saving.isLoading)
            assertFalse(saving.isSaved)
        }

        @Test
        @DisplayName("Saved transition")
        fun `saved transition`() {
            val saving = ExpenseFormUiState(isLoading = true)
            val saved = saving.copy(isLoading = false, isSaved = true)

            assertFalse(saved.isLoading)
            assertTrue(saved.isSaved)
        }

        @Test
        @DisplayName("Validation error (resource ID)")
        fun `validation error with resource id`() {
            val state = ExpenseFormUiState()
            // Χρήση dummy resource ID
            val withError = state.copy(errorResId = 12345)

            assertEquals(12345, withError.errorResId)
        }

        @Test
        @DisplayName("Dynamic error")
        fun `dynamic error`() {
            val state = ExpenseFormUiState()
            val withError = state.copy(errorMessage = "Σφάλμα Firebase")

            assertEquals("Σφάλμα Firebase", withError.errorMessage)
        }

        @Test
        @DisplayName("Clear errors")
        fun `clear errors`() {
            val state = ExpenseFormUiState(
                errorResId = 12345,
                errorMessage = "Σφάλμα"
            )
            val cleared = state.copy(errorResId = null, errorMessage = null)

            assertNull(cleared.errorResId)
            assertNull(cleared.errorMessage)
        }
    }

    // ═══════════════════════════════════════════════════
    // Category Coverage
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Category coverage")
    inner class CategoryCoverage {

        @Test
        @DisplayName("Αλλαγή σε κάθε κατηγορία")
        fun `all categories can be set`() {
            ExpenseCategory.entries.forEach { category ->
                val state = ExpenseFormUiState().copy(category = category)
                assertEquals(category, state.category)
            }
        }

        @Test
        @DisplayName("Αλλαγή payment method σε CARD")
        fun `payment method can switch to card`() {
            val state = ExpenseFormUiState().copy(paymentMethod = PaymentMethod.CARD)
            assertEquals(PaymentMethod.CARD, state.paymentMethod)
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
            val state = ExpenseFormUiState(
                amount = "45,50",
                category = ExpenseCategory.FUEL,
                notes = "Test"
            )
            val updated = state.copy(paymentMethod = PaymentMethod.CARD)

            assertEquals("45,50", updated.amount)
            assertEquals(ExpenseCategory.FUEL, updated.category)
            assertEquals("Test", updated.notes)
            assertEquals(PaymentMethod.CARD, updated.paymentMethod)
        }

        @Test
        @DisplayName("Equality βασίζεται σε τιμές")
        fun `equality is value based`() {
            val a = ExpenseFormUiState(amount = "50,00", category = ExpenseCategory.FUEL)
            val b = ExpenseFormUiState(amount = "50,00", category = ExpenseCategory.FUEL)
            assertEquals(a, b)
        }
    }
}
