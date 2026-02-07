package com.deliverytracker.app.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit Tests για το CalculateGoalProgressUseCase.
 *
 * Ελέγχει:
 * - Μηδενικό progress αν δεν υπάρχει στόχος
 * - Σωστός υπολογισμός ποσοστών
 * - Coercion στο [0, 1] εύρος
 * - Edge cases (μηδενικός στόχος, αρνητικά εισοδήματα)
 */
class CalculateGoalProgressUseCaseTest {

    private lateinit var useCase: CalculateGoalProgressUseCase

    @BeforeEach
    fun setup() {
        useCase = CalculateGoalProgressUseCase()
    }

    @Nested
    @DisplayName("Χωρίς Στόχους")
    inner class NoGoals {

        @Test
        @DisplayName("Επιστρέφει 0 progress αν δεν υπάρχουν στόχοι")
        fun returnsZeroWhenNoGoalsSet() {
            val result = useCase(
                todayIncome = 100.0,
                monthIncome = 2000.0,
                dailyGoal = null,
                monthlyGoal = null
            )

            assertEquals(0f, result.dailyProgress)
            assertEquals(0f, result.monthlyProgress)
        }

        @Test
        @DisplayName("Επιστρέφει 0 αν ο στόχος είναι μηδέν")
        fun returnsZeroWhenGoalIsZero() {
            val result = useCase(
                todayIncome = 100.0,
                monthIncome = 2000.0,
                dailyGoal = 0.0,
                monthlyGoal = 0.0
            )

            assertEquals(0f, result.dailyProgress)
            assertEquals(0f, result.monthlyProgress)
        }
    }

    @Nested
    @DisplayName("Ημερήσιος Στόχος")
    inner class DailyGoal {

        @Test
        @DisplayName("50% πρόοδος (50/100)")
        fun halfwayProgress() {
            val result = useCase(
                todayIncome = 50.0,
                monthIncome = 0.0,
                dailyGoal = 100.0,
                monthlyGoal = null
            )

            assertEquals(0.5f, result.dailyProgress, 0.001f)
        }

        @Test
        @DisplayName("100% πρόοδος (100/100)")
        fun fullProgress() {
            val result = useCase(
                todayIncome = 100.0,
                monthIncome = 0.0,
                dailyGoal = 100.0,
                monthlyGoal = null
            )

            assertEquals(1.0f, result.dailyProgress, 0.001f)
        }

        @Test
        @DisplayName("Coercion: Δεν ξεπερνά το 1.0 (υπερβολή στόχου)")
        fun clampsAtOneWhenOverGoal() {
            val result = useCase(
                todayIncome = 200.0,
                monthIncome = 0.0,
                dailyGoal = 100.0,
                monthlyGoal = null
            )

            assertEquals(1.0f, result.dailyProgress, 0.001f)
        }

        @Test
        @DisplayName("0% αν δεν υπάρχει εισόδημα")
        fun zeroIncomeZeroProgress() {
            val result = useCase(
                todayIncome = 0.0,
                monthIncome = 0.0,
                dailyGoal = 100.0,
                monthlyGoal = null
            )

            assertEquals(0f, result.dailyProgress)
        }
    }

    @Nested
    @DisplayName("Μηνιαίος Στόχος")
    inner class MonthlyGoal {

        @Test
        @DisplayName("75% μηνιαία πρόοδος (1500/2000)")
        fun threeQuarterProgress() {
            val result = useCase(
                todayIncome = 0.0,
                monthIncome = 1500.0,
                dailyGoal = null,
                monthlyGoal = 2000.0
            )

            assertEquals(0.75f, result.monthlyProgress, 0.001f)
        }
    }

    @Nested
    @DisplayName("Συνδυασμένοι Στόχοι")
    inner class CombinedGoals {

        @Test
        @DisplayName("Ημερήσιος και μηνιαίος ταυτόχρονα")
        fun dailyAndMonthlySimultaneously() {
            val result = useCase(
                todayIncome = 60.0,
                monthIncome = 1200.0,
                dailyGoal = 80.0,
                monthlyGoal = 2000.0
            )

            // daily = 60/80 = 0.75
            assertEquals(0.75f, result.dailyProgress, 0.001f)
            // monthly = 1200/2000 = 0.60
            assertEquals(0.60f, result.monthlyProgress, 0.001f)
        }
    }
}
