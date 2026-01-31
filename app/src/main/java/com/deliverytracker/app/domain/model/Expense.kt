package com.deliverytracker.app.domain.model

/**
 * Κατηγορίες εξόδων.
 */
enum class ExpenseCategory {
    FUEL,           // Καύσιμα
    MAINTENANCE,    // Συντήρηση οχήματος
    INSURANCE,      // Ασφάλεια
    PHONE,          // Τηλέφωνο/Data
    EQUIPMENT,      // Εξοπλισμός (θερμοσάκος κλπ)
    TAX,            // Φόροι/ΕΦΚΑ
    OTHER           // Άλλα
}

/**
 * Domain model για ένα έξοδο.
 */
data class Expense(
    val id: String = "",
    val userId: String = "",
    
    // Στοιχεία εξόδου
    val amount: Double = 0.0,
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    
    // Αν συνδέεται με βάρδια
    val shiftId: String? = null,
    
    // Απόδειξη (προαιρετικό)
    val receiptUrl: String? = null,
    
    // Soft delete
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Επιστρέφει το emoji για την κατηγορία.
     */
    val categoryEmoji: String
        get() = when (category) {
            ExpenseCategory.FUEL -> "⛽"
            ExpenseCategory.MAINTENANCE -> "🔧"
            ExpenseCategory.INSURANCE -> "🛡️"
            ExpenseCategory.PHONE -> "📱"
            ExpenseCategory.EQUIPMENT -> "🎒"
            ExpenseCategory.TAX -> "📋"
            ExpenseCategory.OTHER -> "💰"
        }
    
    /**
     * Επιστρέφει το label για την κατηγορία.
     */
    val categoryLabel: String
        get() = when (category) {
            ExpenseCategory.FUEL -> "Καύσιμα"
            ExpenseCategory.MAINTENANCE -> "Συντήρηση"
            ExpenseCategory.INSURANCE -> "Ασφάλεια"
            ExpenseCategory.PHONE -> "Τηλέφωνο"
            ExpenseCategory.EQUIPMENT -> "Εξοπλισμός"
            ExpenseCategory.TAX -> "Φόροι/ΕΦΚΑ"
            ExpenseCategory.OTHER -> "Άλλα"
        }
}
