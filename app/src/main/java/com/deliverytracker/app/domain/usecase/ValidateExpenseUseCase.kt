package com.deliverytracker.app.domain.usecase

import javax.inject.Inject

/**
 * UseCase: Validation λογικής εξόδου.
 *
 * Εξάγει τη validation λογική από το ExpenseFormViewModel
 * ώστε να είναι testable χωρίς Android dependencies.
 *
 * Κανόνες:
 * 1. Το ποσό πρέπει > 0
 * 2. Το ποσό δεν μπορεί να υπερβαίνει MAX_EXPENSE_AMOUNT
 * 3. Η ημερομηνία δεν μπορεί να είναι μελλοντική
 */
class ValidateExpenseUseCase @Inject constructor() {
    
    /**
     * Εκτελεί validation σε ένα έξοδο.
     *
     * @param amount Ποσό εξόδου
     * @param dateMillis Ημερομηνία (millis)
     * @param currentTimeMillis Τρέχουσα ώρα (millis) — injectable για testing
     * @return Αποτέλεσμα validation
     */
    operator fun invoke(
        amount: Double,
        dateMillis: Long,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): ExpenseValidationResult {
        // 1. Μηδενικό ή αρνητικό ποσό
        if (amount <= 0) {
            return ExpenseValidationResult.Invalid(ExpenseValidationError.ZERO_AMOUNT)
        }
        
        // 2. Υπέρβαση μέγιστου ποσού
        if (amount > MAX_EXPENSE_AMOUNT) {
            return ExpenseValidationResult.Invalid(ExpenseValidationError.EXCEEDS_MAX_AMOUNT)
        }
        
        // 3. Μελλοντική ημερομηνία
        if (dateMillis > currentTimeMillis) {
            return ExpenseValidationResult.Invalid(ExpenseValidationError.FUTURE_DATE)
        }
        
        return ExpenseValidationResult.Valid
    }
    
    companion object {
        /** Μέγιστο ποσό εξόδου: 10.000€ */
        const val MAX_EXPENSE_AMOUNT = 10000.0
    }
}

/**
 * Αποτέλεσμα validation εξόδου.
 */
sealed class ExpenseValidationResult {
    data object Valid : ExpenseValidationResult()
    data class Invalid(val error: ExpenseValidationError) : ExpenseValidationResult()
}

/**
 * Τύποι σφαλμάτων validation εξόδου.
 */
enum class ExpenseValidationError {
    ZERO_AMOUNT,
    EXCEEDS_MAX_AMOUNT,
    FUTURE_DATE
}
