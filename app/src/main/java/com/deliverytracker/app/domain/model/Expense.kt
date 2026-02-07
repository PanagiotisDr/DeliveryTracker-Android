package com.deliverytracker.app.domain.model

import androidx.annotation.StringRes
import com.deliverytracker.app.R

/**
 * Κατηγορίες εξόδων.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
enum class ExpenseCategory(val emoji: String, @StringRes val displayNameResId: Int) {
    FUEL("⛽", R.string.category_fuel),
    MAINTENANCE("🔧", R.string.category_maintenance),
    INSURANCE("🛡️", R.string.category_insurance),
    PHONE("📱", R.string.category_phone),
    EQUIPMENT("🎒", R.string.category_equipment),
    TAX("📋", R.string.category_tax),
    KTEO("🚗", R.string.category_kteo),
    ROAD_TAX("📄", R.string.category_road_tax),
    FINES("⚠️", R.string.category_fines),
    OTHER("💰", R.string.category_other)
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

