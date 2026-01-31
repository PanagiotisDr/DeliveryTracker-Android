package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Χρωματική παλέτα για το DeliveryTracker.
 * Ακολουθεί Material Design 3 guidelines.
 */

// Primary - Μπλε τόνοι
val Primary = Color(0xFF1976D2)
val PrimaryLight = Color(0xFF90CAF9)
val PrimaryDark = Color(0xFF0D47A1)

// Secondary - Teal τόνοι
val Secondary = Color(0xFF00897B)
val SecondaryLight = Color(0xFF80CBC4)
val SecondaryDark = Color(0xFF004D40)

// Tertiary - Πορτοκαλί τόνοι για accents
val Tertiary = Color(0xFFFF9800)
val TertiaryLight = Color(0xFFFFB74D)

// Background & Surface
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// Goal Progress Colors - Χρώματα για progress bars
val GoalSuccess = Color(0xFF4CAF50)        // 🟢 ≥100%
val GoalSuccessLight = Color(0xFF81C784)
val GoalWarning = Color(0xFFFF9800)        // 🟠 50-99%
val GoalWarningLight = Color(0xFFFFB74D)
val GoalDanger = Color(0xFFF44336)         // 🔴 <50%
val GoalDangerLight = Color(0xFFE57373)

// Expense Category Colors
val ExpenseFuel = Color(0xFFFF5722)        // Καύσιμα - Πορτοκαλί
val ExpenseMaintenance = Color(0xFF795548) // Συντήρηση - Καφέ
val ExpenseInsurance = Color(0xFF2196F3)   // Ασφάλεια - Μπλε
val ExpenseMobile = Color(0xFF9C27B0)      // Κινητό - Μωβ
val ExpenseEquipment = Color(0xFF607D8B)   // Εξοπλισμός - Γκρι
val ExpenseOther = Color(0xFF9E9E9E)       // Άλλο - Ουδέτερο
