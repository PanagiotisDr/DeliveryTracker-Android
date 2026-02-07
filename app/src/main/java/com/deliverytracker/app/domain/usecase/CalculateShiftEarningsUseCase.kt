package com.deliverytracker.app.domain.usecase

import com.deliverytracker.app.domain.model.Shift
import javax.inject.Inject

/**
 * UseCase: Υπολογισμός αναλυτικών κερδών βάρδιας.
 *
 * Περιέχει υπολογισμούς:
 * - Καθαρά κέρδη (net income)
 * - ΦΠΑ
 * - Κέρδη μετά ΦΠΑ
 * - Ωριαίο rate
 * - Rate ανά παραγγελία
 * - Rate ανά χιλιόμετρο
 *
 * Χρησιμοποιεί τη λογική του domain model (Shift.netIncome, κτλ.)
 * αλλά επεκτείνει με ΕΦΚΑ και net-after-tax υπολογισμούς.
 */
class CalculateShiftEarningsUseCase @Inject constructor() {
    
    /**
     * Υπολογίζει αναλυτικά τα κέρδη μιας βάρδιας.
     *
     * @param shift Η βάρδια
     * @param vatRate Ποσοστό ΦΠΑ (default 24%)
     * @param monthlyEfka Μηνιαίο ποσό ΕΦΚΑ (default 254€)
     * @param workingDaysPerMonth Εργάσιμες μέρες/μήνα (default 22)
     * @return Αναλυτικά κέρδη
     */
    operator fun invoke(
        shift: Shift,
        vatRate: Double = DEFAULT_VAT_RATE,
        monthlyEfka: Double = DEFAULT_MONTHLY_EFKA,
        workingDaysPerMonth: Int = DEFAULT_WORKING_DAYS
    ): ShiftEarningsResult {
        // Ημερήσια εισφορά ΕΦΚΑ
        val dailyEfka = monthlyEfka / workingDaysPerMonth
        
        // ΦΠΑ εφαρμόζεται μόνο σε grossIncome + bonus, ΟΧΙ σε tips
        val vatAmount = shift.calculateVAT(vatRate)
        
        // Καθαρά μετά ΦΠΑ & ΕΦΚΑ
        val netAfterTax = shift.netIncome - vatAmount - dailyEfka
        
        return ShiftEarningsResult(
            grossIncome = shift.grossIncome,
            tips = shift.tips,
            bonus = shift.bonus,
            netIncome = shift.netIncome,
            vatAmount = vatAmount,
            dailyEfka = dailyEfka,
            netAfterTax = netAfterTax,
            incomePerHour = shift.incomePerHour,
            incomePerOrder = shift.incomePerOrder,
            incomePerKm = shift.incomePerKm,
            hoursWorked = shift.hoursWorked,
            totalKilometers = shift.totalKilometers
        )
    }
    
    companion object {
        const val DEFAULT_VAT_RATE = 0.24
        const val DEFAULT_MONTHLY_EFKA = 254.0
        const val DEFAULT_WORKING_DAYS = 22
    }
}

/**
 * Αποτέλεσμα υπολογισμού κερδών.
 */
data class ShiftEarningsResult(
    val grossIncome: Double,
    val tips: Double,
    val bonus: Double,
    val netIncome: Double,
    val vatAmount: Double,
    val dailyEfka: Double,
    val netAfterTax: Double,
    val incomePerHour: Double,
    val incomePerOrder: Double,
    val incomePerKm: Double,
    val hoursWorked: Double,
    val totalKilometers: Double
)
