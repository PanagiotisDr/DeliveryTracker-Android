package com.deliverytracker.app.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για το UserSettings domain model.
 *
 * Ελέγχει:
 * - Default values (vatRate, EFKA, goals)
 * - Computed properties (vatPercentage, calculatedWeeklyGoal, calculatedMonthlyGoal)
 * - Edge cases (null goals, boundary values)
 */
@DisplayName("UserSettings Model")
class UserSettingsTest {

    // ═══════════════════════════════════════════════════
    // Default Values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Προεπιλεγμένες τιμές")
    inner class DefaultValues {

        @Test
        @DisplayName("Default ΦΠΑ = 24%")
        fun `default vat rate is 24 percent`() {
            val settings = UserSettings()
            assertEquals(0.24, settings.vatRate)
        }

        @Test
        @DisplayName("Default ΕΦΚΑ = 254€")
        fun `default monthly efka is 254`() {
            val settings = UserSettings()
            assertEquals(254.0, settings.monthlyEfkaAmount)
        }

        @Test
        @DisplayName("Default theme = SYSTEM")
        fun `default theme is system`() {
            val settings = UserSettings()
            assertEquals(ThemeMode.SYSTEM, settings.theme)
        }

        @Test
        @DisplayName("Στόχοι είναι null by default")
        fun `goals are null by default`() {
            val settings = UserSettings()
            assertNull(settings.dailyGoal)
            assertNull(settings.weeklyGoal)
            assertNull(settings.monthlyGoal)
            assertNull(settings.yearlyGoal)
        }
    }

    // ═══════════════════════════════════════════════════
    // VAT Percentage
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("vatPercentage computed property")
    inner class VatPercentageTests {

        @Test
        @DisplayName("24% ΦΠΑ → vatPercentage = 24.0")
        fun `24 percent vat returns 24`() {
            val settings = UserSettings(vatRate = 0.24)
            assertEquals(24.0, settings.vatPercentage)
        }

        @Test
        @DisplayName("13% ΦΠΑ → vatPercentage = 13.0")
        fun `13 percent vat returns 13`() {
            val settings = UserSettings(vatRate = 0.13)
            assertEquals(13.0, settings.vatPercentage, 0.001)
        }

        @Test
        @DisplayName("0% ΦΠΑ → vatPercentage = 0.0")
        fun `zero vat returns zero`() {
            val settings = UserSettings(vatRate = 0.0)
            assertEquals(0.0, settings.vatPercentage)
        }
    }

    // ═══════════════════════════════════════════════════
    // Calculated Weekly Goal
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("calculatedWeeklyGoal computed property")
    inner class WeeklyGoalTests {

        @Test
        @DisplayName("Αν υπάρχει weeklyGoal, χρησιμοποιείται αυτό")
        fun `explicit weekly goal takes priority`() {
            val settings = UserSettings(dailyGoal = 50.0, weeklyGoal = 300.0)
            assertEquals(300.0, settings.calculatedWeeklyGoal)
        }

        @Test
        @DisplayName("Αν δεν υπάρχει weeklyGoal, υπολογίζεται × 7")
        fun `weekly goal calculated from daily times 7`() {
            val settings = UserSettings(dailyGoal = 100.0) // weeklyGoal = null
            assertEquals(700.0, settings.calculatedWeeklyGoal)
        }

        @Test
        @DisplayName("Αν δεν υπάρχει ούτε daily ούτε weekly, null")
        fun `no daily no weekly returns null`() {
            val settings = UserSettings()
            assertNull(settings.calculatedWeeklyGoal)
        }
    }

    // ═══════════════════════════════════════════════════
    // Calculated Monthly Goal
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("calculatedMonthlyGoal computed property")
    inner class MonthlyGoalTests {

        @Test
        @DisplayName("Αν υπάρχει monthlyGoal, χρησιμοποιείται αυτό")
        fun `explicit monthly goal takes priority`() {
            val settings = UserSettings(dailyGoal = 100.0, monthlyGoal = 2000.0)
            assertEquals(2000.0, settings.calculatedMonthlyGoal)
        }

        @Test
        @DisplayName("Αν δεν υπάρχει monthlyGoal, υπολογίζεται × 22")
        fun `monthly goal calculated from daily times 22`() {
            val settings = UserSettings(dailyGoal = 100.0) // monthlyGoal = null
            assertEquals(2200.0, settings.calculatedMonthlyGoal)
        }

        @Test
        @DisplayName("Αν δεν υπάρχει ούτε daily ούτε monthly, null")
        fun `no daily no monthly returns null`() {
            val settings = UserSettings()
            assertNull(settings.calculatedMonthlyGoal)
        }

        @Test
        @DisplayName("dailyGoal = 0 → monthlyGoal = 0")
        fun `zero daily goal produces zero monthly goal`() {
            val settings = UserSettings(dailyGoal = 0.0)
            assertEquals(0.0, settings.calculatedMonthlyGoal)
        }
    }

    // ═══════════════════════════════════════════════════
    // ThemeMode
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("ThemeMode Enum")
    inner class ThemeModeTests {

        @Test
        @DisplayName("Υπάρχουν 3 theme modes")
        fun `three theme modes exist`() {
            assertEquals(3, ThemeMode.entries.size)
        }

        @Test
        @DisplayName("SYSTEM, LIGHT, DARK υπάρχουν")
        fun `all modes can be accessed`() {
            assertNotNull(ThemeMode.valueOf("SYSTEM"))
            assertNotNull(ThemeMode.valueOf("LIGHT"))
            assertNotNull(ThemeMode.valueOf("DARK"))
        }
    }
}
