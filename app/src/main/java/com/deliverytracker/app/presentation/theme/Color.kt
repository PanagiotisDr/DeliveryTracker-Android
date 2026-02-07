package com.deliverytracker.app.presentation.theme

import com.deliverytracker.app.domain.model.ExpenseCategory

/**
 * 🎨 DeliveryTracker Design System - Color Index
 *
 * Αυτό το αρχείο κρατά μόνο τη helper function toColor() και
 * re-export references. Τα χρώματα ορίζονται σε ξεχωριστά αρχεία:
 *
 * - BrandColors.kt  → Primary/Secondary palette
 * - SemanticColors.kt → Success/Warning/Error/Info
 * - GradientColors.kt → Surfaces, Text, Borders, Categories, Gradients
 *
 * @version 5.0.0 - Modular Color System
 */

// ══════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ══════════════════════════════════════════════════════════════════

/**
 * Μετατρέπει ExpenseCategory σε χρώμα.
 * Χρησιμοποιείται στα expense cards/chips.
 */
fun ExpenseCategory.toColor() = when (this) {
    ExpenseCategory.FUEL -> CategoryColors.Fuel
    ExpenseCategory.MAINTENANCE -> CategoryColors.Maintenance
    ExpenseCategory.INSURANCE -> CategoryColors.Insurance
    ExpenseCategory.TAX -> CategoryColors.Tax
    ExpenseCategory.EQUIPMENT -> CategoryColors.Equipment
    ExpenseCategory.PHONE -> CategoryColors.Phone
    ExpenseCategory.ROAD_TAX -> CategoryColors.RoadTax
    ExpenseCategory.KTEO -> CategoryColors.KTEO
    ExpenseCategory.FINES -> CategoryColors.Fines
    ExpenseCategory.OTHER -> CategoryColors.Other
}
