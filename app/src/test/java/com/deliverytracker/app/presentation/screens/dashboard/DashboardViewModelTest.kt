package com.deliverytracker.app.presentation.screens.dashboard

import com.deliverytracker.app.domain.model.ThemeMode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για τo DashboardUiState.
 *
 * Σημείωση: Η πλήρης ViewModel instantiation απαιτεί
 * Android context (FirebaseAuth). Αυτά τα tests ελέγχουν
 * τη σωστή δομή του UiState και τις default τιμές.
 *
 * Η integration testing γίνεται σε instrumented tests.
 */
@DisplayName("DashboardUiState")
class DashboardViewModelTest {

    // ═══════════════════════════════════════════════════
    // Default Values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Προεπιλεγμένες τιμές")
    inner class DefaultValues {

        @Test
        @DisplayName("Αρχικό state → isLoading = true")
        fun `initial state has loading true`() {
            val state = DashboardUiState()
            assertTrue(state.isLoading)
        }

        @Test
        @DisplayName("Αρχικό state → isLoggedOut = false")
        fun `initial state has logged out false`() {
            val state = DashboardUiState()
            assertFalse(state.isLoggedOut)
        }

        @Test
        @DisplayName("Αρχικό state → username = null")
        fun `initial state has null username`() {
            val state = DashboardUiState()
            assertNull(state.username)
        }

        @Test
        @DisplayName("Αρχικό state → μηδενικά σημερινά στατιστικά")
        fun `initial state has zero today stats`() {
            val state = DashboardUiState()
            assertEquals(0.0, state.todayNetIncome)
            assertEquals(0, state.todayOrders)
            assertEquals(0.0, state.todayHours)
            assertEquals(0.0, state.todayBonus)
        }

        @Test
        @DisplayName("Αρχικό state → μηδενικά μηνιαία στατιστικά")
        fun `initial state has zero monthly stats`() {
            val state = DashboardUiState()
            assertEquals(0.0, state.monthNetIncome)
            assertEquals(0, state.monthOrders)
            assertEquals(0, state.monthShifts)
        }

        @Test
        @DisplayName("Αρχικό state → null goals")
        fun `initial state has null goals`() {
            val state = DashboardUiState()
            assertNull(state.dailyGoal)
            assertNull(state.monthlyGoal)
        }

        @Test
        @DisplayName("Αρχικό state → μηδενική πρόοδος")
        fun `initial state has zero progress`() {
            val state = DashboardUiState()
            assertEquals(0f, state.dailyProgress)
            assertEquals(0f, state.monthlyProgress)
        }
    }

    // ═══════════════════════════════════════════════════
    // State Updates (copy tests)
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("State transitions")
    inner class StateTransitions {

        @Test
        @DisplayName("Loading → data loaded transition")
        fun `loading to data loaded transition`() {
            val initial = DashboardUiState()
            val loaded = initial.copy(
                isLoading = false,
                todayNetIncome = 85.0,
                todayOrders = 15,
                todayHours = 6.5,
                monthNetIncome = 1200.0,
                monthShifts = 18
            )

            assertFalse(loaded.isLoading)
            assertEquals(85.0, loaded.todayNetIncome)
            assertEquals(15, loaded.todayOrders)
            assertEquals(6.5, loaded.todayHours)
            assertEquals(1200.0, loaded.monthNetIncome)
            assertEquals(18, loaded.monthShifts)
        }

        @Test
        @DisplayName("Goal progress update")
        fun `goal progress update`() {
            val state = DashboardUiState(
                dailyGoal = 100.0,
                monthlyGoal = 2000.0,
                todayNetIncome = 75.0,
                monthNetIncome = 1500.0
            )
            val withProgress = state.copy(
                dailyProgress = 0.75f,
                monthlyProgress = 0.75f
            )

            assertEquals(0.75f, withProgress.dailyProgress)
            assertEquals(0.75f, withProgress.monthlyProgress)
        }

        @Test
        @DisplayName("Logout transition")
        fun `logout transition`() {
            val state = DashboardUiState(isLoading = false, username = "Τάκης")
            val loggedOut = state.copy(isLoggedOut = true)

            assertTrue(loggedOut.isLoggedOut)
            assertEquals("Τάκης", loggedOut.username) // username διατηρείται
        }

        @Test
        @DisplayName("Error state")
        fun `error state`() {
            val state = DashboardUiState().copy(
                isLoading = false,
                errorMessage = "Σφάλμα σύνδεσης"
            )

            assertFalse(state.isLoading)
            assertEquals("Σφάλμα σύνδεσης", state.errorMessage)
        }
    }
}
