package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * 🔶 Brand Colors - Κεντρική παλέτα χρωμάτων της εφαρμογής
 *
 * Warm Amber/Orange palette — Premium, ήπια, επαγγελματική αίσθηση.
 * Αποφεύγουμε πολύ saturated κόκκινα/πορτοκαλί.
 *
 * @version 5.0.0 - Warm Amber Redesign
 */
object BrandColors {
    // Warm Amber palette - Primary
    val Orange50 = Color(0xFFFFF3E0)
    val Orange100 = Color(0xFFFFE0B2)
    val Orange200 = Color(0xFFFFCC80)       // Soft για dark theme
    val Orange300 = Color(0xFFFFB74D)
    val Orange400 = Color(0xFFFFA726)
    val Orange500 = Color(0xFFFF9800)       // Main brand color — Warm Amber
    val Orange600 = Color(0xFFFB8C00)
    val Orange700 = Color(0xFFF57C00)
    val Orange800 = Color(0xFFEF6C00)
    val Orange900 = Color(0xFFE65100)
    
    // Amber/Gold palette - Secondary (earnings, success)
    val Gold50 = Color(0xFFFFF8E1)
    val Gold100 = Color(0xFFFFECB3)
    val Gold200 = Color(0xFFFFE082)         // Soft για dark theme
    val Gold300 = Color(0xFFFFD54F)
    val Gold400 = Color(0xFFFFCA28)
    val Gold500 = Color(0xFFFFC107)         // Amber/Gold main
    val Gold600 = Color(0xFFFFB300)
    val Gold700 = Color(0xFFFFA000)
    val Gold800 = Color(0xFFFF8F00)
    val Gold900 = Color(0xFFFF6F00)
    
    // ────────────────────────────────────────────────────────
    // DARK THEME ASSIGNMENTS
    // ────────────────────────────────────────────────────────
    
    // Dark theme - Ζεστό amber, premium αίσθηση
    val Primary = Orange300                  // #FFB74D - Warm Amber (soft)
    val PrimaryVariant = Orange400           // Hover/pressed
    val PrimaryMuted = Orange900             // Dark containers
    val PrimarySubtle = Color(0xFF2D2010)    // Πολύ subtle warm tint
    
    // ────────────────────────────────────────────────────────
    // LIGHT THEME ASSIGNMENTS
    // ────────────────────────────────────────────────────────
    
    // Light theme - Πιο σκούρο για contrast
    val PrimaryLight = Orange700             // #F57C00 - Καλό contrast
    val PrimaryLightVariant = Orange800
    
    // ────────────────────────────────────────────────────────
    // SECONDARY - Gold/Amber (earnings, money)
    // ────────────────────────────────────────────────────────
    
    val Secondary = Gold500                  // #FFC107 - Amber
    val SecondaryVariant = Gold400
    val SecondaryMuted = Gold800
    val SecondarySubtle = Color(0xFF332915)
    
    // ────────────────────────────────────────────────────────
    // TERTIARY - Green (profit, positive)
    // ────────────────────────────────────────────────────────
    
    val Tertiary = Color(0xFF66BB6A)         // Green 400
    val TertiaryVariant = Color(0xFF81C784)
    val TertiaryLight = Color(0xFF4CAF50)
}
