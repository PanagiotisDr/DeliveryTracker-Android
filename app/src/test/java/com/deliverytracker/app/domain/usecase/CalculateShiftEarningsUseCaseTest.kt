package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Shift
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για το CalculateShiftEarningsUseCase.
 *
 * Ελέγχει:
 * - Βασικοί υπολογισμοί κερδών
 * - ΦΠΑ υπολογισμός
 * - ΕΦΚΑ αφαίρεση
 * - Rate ανά ώρα/παραγγελία/χλμ
 * - Edge cases
 */
@DisplayName("CalculateShiftEarningsUseCase")
class CalculateShiftEarningsUseCaseTest {

    private val useCase = CalculateShiftEarningsUseCase()

    // ═══════════════════════════════════════════════════
    // Τυπική βάρδια
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Τυπική βάρδια")
    inner class TypicalShift {

        private val shift = Shift(
            grossIncome = 80.0,
            tips = 15.0,
            bonus = 5.0,
            workedHours = 8,
            workedMinutes = 0,
            ordersCount = 20,
            kilometers = 60.0
        )

        @Test
        @DisplayName("netIncome = gross + tips + bonus")
        fun `net income is sum of all income`() {
            val result = useCase(shift)
            assertEquals(100.0, result.netIncome)
        }

        @Test
        @DisplayName("ΦΠΑ εφαρμόζεται μόνο σε gross + bonus, ΟΧΙ tips")
        fun `vat applies to gross and bonus only`() {
            // ΦΠΑ = (80 + 5) × 0.24 = 20.40
            val result = useCase(shift)
            assertEquals(20.40, result.vatAmount, 0.001)
        }

        @Test
        @DisplayName("Ημερήσιο ΕΦΚΑ = 254 / 22")
        fun `daily efka is correct`() {
            val result = useCase(shift)
            assertEquals(254.0 / 22.0, result.dailyEfka, 0.001)
        }

        @Test
        @DisplayName("netAfterTax = netIncome - ΦΠΑ - ΕΦΚΑ")
        fun `net after tax calculation`() {
            val result = useCase(shift)
            val expected = 100.0 - 20.40 - (254.0 / 22.0)
            assertEquals(expected, result.netAfterTax, 0.01)
        }

        @Test
        @DisplayName("incomePerHour = 100 / 8 = 12.5")
        fun `income per hour calculation`() {
            val result = useCase(shift)
            assertEquals(12.5, result.incomePerHour)
        }

        @Test
        @DisplayName("incomePerOrder = 100 / 20 = 5.0")
        fun `income per order calculation`() {
            val result = useCase(shift)
            assertEquals(5.0, result.incomePerOrder)
        }

        @Test
        @DisplayName("incomePerKm = 100 / 60 ≈ 1.67")
        fun `income per km calculation`() {
            val result = useCase(shift)
            assertEquals(100.0 / 60.0, result.incomePerKm, 0.01)
        }
    }

    // ═══════════════════════════════════════════════════
    // Custom parameters
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Custom parameters")
    inner class CustomParameters {

        @Test
        @DisplayName("Custom ΦΠΑ 13%")
        fun `custom vat rate 13 percent`() {
            val shift = Shift(grossIncome = 100.0, bonus = 0.0)
            val result = useCase(shift, vatRate = 0.13)
            assertEquals(13.0, result.vatAmount, 0.001)
        }

        @Test
        @DisplayName("Custom ΕΦΚΑ 300€")
        fun `custom monthly efka`() {
            val shift = Shift(grossIncome = 100.0)
            val result = useCase(shift, monthlyEfka = 300.0)
            assertEquals(300.0 / 22.0, result.dailyEfka, 0.001)
        }

        @Test
        @DisplayName("Custom εργάσιμες μέρες 20")
        fun `custom working days per month`() {
            val shift = Shift(grossIncome = 100.0)
            val result = useCase(shift, workingDaysPerMonth = 20)
            assertEquals(254.0 / 20.0, result.dailyEfka, 0.001)
        }
    }

    // ═══════════════════════════════════════════════════
    // Edge cases
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Edge cases")
    inner class EdgeCases {

        @Test
        @DisplayName("Μηδενική βάρδια → μηδενικά κέρδη")
        fun `zero shift produces zero earnings`() {
            val result = useCase(Shift())
            assertEquals(0.0, result.netIncome)
            assertEquals(0.0, result.vatAmount)
            assertEquals(0.0, result.incomePerHour)
            assertEquals(0.0, result.incomePerOrder)
        }

        @Test
        @DisplayName("Μόνο tips → ΦΠΑ = 0")
        fun `only tips produces zero vat`() {
            val shift = Shift(grossIncome = 0.0, tips = 50.0, bonus = 0.0)
            val result = useCase(shift)
            assertEquals(50.0, result.netIncome)
            assertEquals(0.0, result.vatAmount)
        }

        @Test
        @DisplayName("netAfterTax μπορεί να είναι αρνητικό")
        fun `net after tax can be negative`() {
            // Πολύ μικρή βάρδια — μετά ΦΠΑ + ΕΦΚΑ γίνεται αρνητικά
            val shift = Shift(grossIncome = 10.0, tips = 0.0, bonus = 0.0)
            val result = useCase(shift)
            assertTrue(result.netAfterTax < 0)
        }
    }
}
