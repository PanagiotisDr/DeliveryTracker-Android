package com.deliverytracker.app.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Ορισμός όλων των routes της εφαρμογής.
 *
 * Navigation Compose 2.9+ Type-Safe Routes:
 * - Κάθε route είναι @Serializable data object ή data class
 * - Δεν χρειάζονται string routes — ο compiler εγγυάται type safety
 * - Τα arguments περνούν ως properties (π.χ. ShiftForm.shiftId)
 * - Δεν χρειάζονται navArgument() ή createRoute()
 */

// ═══════════════════════════════════════════════════
// Auth Routes — Screens εγγραφής/σύνδεσης
// ═══════════════════════════════════════════════════

@Serializable
data object Login

@Serializable
data object Register

@Serializable
data object PinLogin

@Serializable
data object PinSetup

// ═══════════════════════════════════════════════════
// Main Routes — Κύριες οθόνες
// ═══════════════════════════════════════════════════

@Serializable
data object Dashboard

@Serializable
data object ShiftList

/** 
 * Φόρμα βάρδιας — αν shiftId == null → νέα, αλλιώς → επεξεργασία 
 */
@Serializable
data class ShiftForm(val shiftId: String? = null)

@Serializable
data object ExpenseList

/** 
 * Φόρμα εξόδου — αν expenseId == null → νέο, αλλιώς → επεξεργασία 
 */
@Serializable
data class ExpenseForm(val expenseId: String? = null)

@Serializable
data object Statistics

@Serializable
data object Settings

@Serializable
data object RecycleBin

@Serializable
data object Export
