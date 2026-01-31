package com.deliverytracker.app.domain.model

/**
 * Κατηγορίες εξόδων.
 */
enum class ExpenseCategory(val emoji: String, val displayName: String) {
    FUEL("⛽", "Καύσιμα"),
    MAINTENANCE("🔧", "Συντήρηση"),
    INSURANCE("🛡️", "Ασφάλεια"),
    PHONE("📱", "Τηλέφωνο/Data"),
    EQUIPMENT("🎒", "Εξοπλισμός"),
    TAX("📋", "Φόροι/ΕΦΚΑ"),
    KTEO("🚗", "ΚΤΕΟ"),
    ROAD_TAX("📄", "Τέλη Κυκλοφορίας"),
    FINES("⚠️", "Πρόστιμα"),
    OTHER("💰", "Άλλα")
}

/**
 * Μέθοδος πληρωμής.
 */
enum class PaymentMethod {
    CASH, CARD
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
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    
    // Αν συνδέεται με βάρδια (προαιρετικό)
    val shiftId: String? = null,
    
    // Απόδειξη (προαιρετικό)
    val receiptUrl: String? = null,
    
    // Soft delete
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

