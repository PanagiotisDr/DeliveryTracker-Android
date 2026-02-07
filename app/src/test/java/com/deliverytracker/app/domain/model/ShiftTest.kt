package com.deliverytracker.app.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit Tests για το Shift domain model.
 *
 * Ελέγχει τα computed properties:
 * - netIncome
 * - hoursWorked
 * - totalKilometers (νέο + legacy)
 * - incomePerHour, incomePerOrder, incomePerKm
 * - calculateVAT, grossIncomeWithVAT
 */
class ShiftTest {

    // ═══════════════════════════════════════════════════
    // Βοηθητικό factory
    // ═══════════════════════════════════════════════════

    private fun createShift(
        grossIncome: Double = 50.0,
        tips: Double = 10.0,
        bonus: Double = 5.0,
        hours: Int = 5,
        minutes: Int = 30,
        orders: Int = 8,
        km: Double = 0.0,
        kmStart: Double? = null,
        kmEnd: Double? = null
    ) = Shift(
        grossIncome = grossIncome,
        tips = tips,
        bonus = bonus,
        workedHours = hours,
        workedMinutes = minutes,
        ordersCount = orders,
        kilometers = km,
        kilometersStart = kmStart,
        kilometersEnd = kmEnd
    )

    @Nested
    @DisplayName("Net Income")
    inner class NetIncomeTests {

        @Test
        @DisplayName("netIncome = gross + tips + bonus")
        fun calculatesNetIncome() {
            val shift = createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0)
            assertEquals(65.0, shift.netIncome, 0.001)
        }

        @Test
        @DisplayName("netIncome = 0 αν όλα μηδενικά")
        fun zeroNetIncome() {
            val shift = createShift(grossIncome = 0.0, tips = 0.0, bonus = 0.0)
            assertEquals(0.0, shift.netIncome, 0.001)
        }

        @Test
        @DisplayName("netIncome χωρίς tips/bonus")
        fun netIncomeWithoutExtras() {
            val shift = createShift(grossIncome = 100.0, tips = 0.0, bonus = 0.0)
            assertEquals(100.0, shift.netIncome, 0.001)
        }
    }

    @Nested
    @DisplayName("Hours Worked")
    inner class HoursWorkedTests {

        @Test
        @DisplayName("Μετατρέπει ώρες+λεπτά σε δεκαδικό (5h 30m = 5.5)")
        fun convertsToDecimal() {
            val shift = createShift(hours = 5, minutes = 30)
            assertEquals(5.5, shift.hoursWorked, 0.001)
        }

        @Test
        @DisplayName("Ακέραιες ώρες (8h 0m = 8.0)")
        fun integerHours() {
            val shift = createShift(hours = 8, minutes = 0)
            assertEquals(8.0, shift.hoursWorked, 0.001)
        }

        @Test
        @DisplayName("Μόνο λεπτά (0h 45m = 0.75)")
        fun minutesOnly() {
            val shift = createShift(hours = 0, minutes = 45)
            assertEquals(0.75, shift.hoursWorked, 0.001)
        }
    }

    @Nested
    @DisplayName("Total Kilometers")
    inner class KilometersTests {

        @Test
        @DisplayName("Χρησιμοποιεί νέο km field αν > 0")
        fun usesNewKmField() {
            val shift = createShift(km = 45.0, kmStart = 100.0, kmEnd = 120.0)
            // Νέο field (45) υπερισχύει του legacy (120-100=20)
            assertEquals(45.0, shift.totalKilometers, 0.001)
        }

        @Test
        @DisplayName("Fallback στο legacy (end - start)")
        fun fallsBackToLegacy() {
            val shift = createShift(km = 0.0, kmStart = 100.0, kmEnd = 145.0)
            assertEquals(45.0, shift.totalKilometers, 0.001)
        }

        @Test
        @DisplayName("0 αν δεν υπάρχει κανένα field")
        fun zeroIfNoneSet() {
            val shift = createShift(km = 0.0, kmStart = null, kmEnd = null)
            assertEquals(0.0, shift.totalKilometers, 0.001)
        }
    }

    @Nested
    @DisplayName("Income Ratios")
    inner class IncomeRatioTests {

        @Test
        @DisplayName("incomePerHour = netIncome / hoursWorked")
        fun calculatesPerHour() {
            val shift = createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, hours = 5, minutes = 0)
            // net=65, hours=5.0 → 13.0
            assertEquals(13.0, shift.incomePerHour, 0.001)
        }

        @Test
        @DisplayName("incomePerHour = 0 αν hours = 0")
        fun perHourZeroHours() {
            val shift = createShift(hours = 0, minutes = 0)
            assertEquals(0.0, shift.incomePerHour, 0.001)
        }

        @Test
        @DisplayName("incomePerOrder = netIncome / orders")
        fun calculatesPerOrder() {
            val shift = createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, orders = 10)
            // net=65, orders=10 → 6.5
            assertEquals(6.5, shift.incomePerOrder, 0.001)
        }

        @Test
        @DisplayName("incomePerOrder = 0 αν orders = 0")
        fun perOrderZeroOrders() {
            val shift = createShift(orders = 0)
            assertEquals(0.0, shift.incomePerOrder, 0.001)
        }

        @Test
        @DisplayName("incomePerKm = netIncome / km")
        fun calculatesPerKm() {
            val shift = createShift(grossIncome = 50.0, tips = 10.0, bonus = 5.0, km = 65.0)
            // net=65, km=65 → 1.0
            assertEquals(1.0, shift.incomePerKm, 0.001)
        }
    }

    @Nested
    @DisplayName("VAT Υπολογισμοί")
    inner class VATTests {

        @Test
        @DisplayName("ΦΠΑ 24% επί gross + bonus (ΟΧΙ tips)")
        fun calculatesVAT() {
            val shift = createShift(grossIncome = 100.0, tips = 20.0, bonus = 10.0)
            // VAT = (100 + 10) * 0.24 = 26.4
            assertEquals(26.4, shift.calculateVAT(), 0.001)
        }

        @Test
        @DisplayName("ΦΠΑ με custom rate (13%)")
        fun calculatesCustomVAT() {
            val shift = createShift(grossIncome = 100.0, tips = 0.0, bonus = 0.0)
            // VAT = 100 * 0.13 = 13.0
            assertEquals(13.0, shift.calculateVAT(0.13), 0.001)
        }

        @Test
        @DisplayName("grossIncomeWithVAT = net + VAT")
        fun calculatesGrossWithVAT() {
            val shift = createShift(grossIncome = 100.0, tips = 20.0, bonus = 10.0)
            // net = 100+20+10 = 130
            // VAT = (100+10)*0.24 = 26.4
            // grossWithVAT = 130 + 26.4 = 156.4
            assertEquals(156.4, shift.grossIncomeWithVAT(), 0.001)
        }
    }
}
