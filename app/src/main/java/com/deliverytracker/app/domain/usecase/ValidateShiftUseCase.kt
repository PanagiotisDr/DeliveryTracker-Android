package com.deliverytracker.app.domain.usecase

import javax.inject.Inject

/**
 * UseCase: Validation λογικής βάρδιας.
 *
 * Εξάγει τη validation λογική από το ShiftFormViewModel
 * ώστε να είναι testable χωρίς Android dependencies.
 *
 * Κανόνες:
 * 1. Τα συνολικά έσοδα πρέπει > 0
 * 2. Αν υπάρχουν έσοδα, πρέπει να υπάρχει διάρκεια > 0
 * 3. Η διάρκεια δεν μπορεί > 24 ώρες (1440 λεπτά)
 * 4. Αν υπάρχουν έσοδα, πρέπει να υπάρχουν παραγγελίες > 0
 * 5. Αν υπάρχουν παραγγελίες, πρέπει να υπάρχουν χιλιόμετρα > 0
 * 6. Η ημερομηνία δεν μπορεί να είναι μελλοντική
 */
class ValidateShiftUseCase @Inject constructor() {
    
    /**
     * Εκτελεί validation σε μια βάρδια.
     *
     * @param totalIncome Σύνολο εσόδων (gross + tips + bonus)
     * @param totalMinutes Σύνολο λεπτών εργασίας
     * @param ordersCount Αριθμός παραγγελιών
     * @param kilometers Χιλιόμετρα
     * @param shiftDateMillis Ημερομηνία βάρδιας (millis)
     * @param currentTimeMillis Τρέχουσα ώρα (millis) — injectable για testing
     * @return Αποτέλεσμα validation
     */
    operator fun invoke(
        totalIncome: Double,
        totalMinutes: Int,
        ordersCount: Int,
        kilometers: Double,
        shiftDateMillis: Long,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): ShiftValidationResult {
        // 1. Μηδενικά έσοδα
        if (totalIncome <= 0) {
            return ShiftValidationResult.Invalid(ShiftValidationError.ZERO_INCOME)
        }
        
        // 2. Μηδενική διάρκεια με έσοδα
        if (totalMinutes == 0 && totalIncome > 0) {
            return ShiftValidationResult.Invalid(ShiftValidationError.ZERO_DURATION)
        }
        
        // 3. Υπέρβαση 24 ωρών
        if (totalMinutes > MAX_SHIFT_MINUTES) {
            return ShiftValidationResult.Invalid(ShiftValidationError.OVER_24_HOURS)
        }
        
        // 4. Μηδενικές παραγγελίες με έσοδα
        if (ordersCount == 0 && totalIncome > 0) {
            return ShiftValidationResult.Invalid(ShiftValidationError.ZERO_ORDERS)
        }
        
        // 5. Μηδενικά χιλιόμετρα με παραγγελίες
        if (kilometers <= 0 && ordersCount > 0) {
            return ShiftValidationResult.Invalid(ShiftValidationError.ZERO_KILOMETERS)
        }
        
        // 6. Μελλοντική ημερομηνία
        if (shiftDateMillis > currentTimeMillis) {
            return ShiftValidationResult.Invalid(ShiftValidationError.FUTURE_DATE)
        }
        
        return ShiftValidationResult.Valid
    }
    
    companion object {
        /** Μέγιστα λεπτά βάρδιας = 24 * 60 = 1440 */
        const val MAX_SHIFT_MINUTES = 1440
    }
}

/**
 * Αποτέλεσμα validation βάρδιας.
 */
sealed class ShiftValidationResult {
    data object Valid : ShiftValidationResult()
    data class Invalid(val error: ShiftValidationError) : ShiftValidationResult()
}

/**
 * Τύποι σφαλμάτων validation.
 */
enum class ShiftValidationError {
    ZERO_INCOME,
    ZERO_DURATION,
    OVER_24_HOURS,
    ZERO_ORDERS,
    ZERO_KILOMETERS,
    FUTURE_DATE
}
