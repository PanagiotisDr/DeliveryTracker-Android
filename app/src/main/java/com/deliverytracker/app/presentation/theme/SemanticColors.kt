package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * 🎯 Semantic Colors - Σημασιολογικά χρώματα
 *
 * Success, Warning, Error, Info
 * Ανεξάρτητα από brand palette
 *
 * @version 4.0.0 - Warm Premium Redesign
 */
object SemanticColors {
    // Success - Green tones
    val Success = Color(0xFF81C784)         // Green 300
    val SuccessBright = Color(0xFF66BB6A)   // Green 400
    val SuccessVariant = Color(0xFF4CAF50)  // Green 500
    val SuccessMuted = Color(0xFF1B3D20)
    val OnSuccess = Color(0xFF0D1F10)
    
    // Warning - Amber tones (brand aligned)
    val Warning = Color(0xFFFFD54F)         // Amber 300
    val WarningBright = Color(0xFFFFCA28)   // Amber 400
    val WarningVariant = Color(0xFFFFC107)  // Amber 500
    val WarningMuted = Color(0xFF3D3015)
    val OnWarning = Color(0xFF1F1A0A)
    
    // Error - Red tones
    val Error = Color(0xFFE57373)           // Red 300
    val ErrorBright = Color(0xFFEF5350)     // Red 400
    val ErrorVariant = Color(0xFFF44336)    // Red 500
    val ErrorMuted = Color(0xFF3D1515)
    val OnError = Color(0xFF1F0A0A)
    
    // Info - Light Blue tones
    val Info = Color(0xFF4FC3F7)            // Light Blue 300
    val InfoBright = Color(0xFF29B6F6)      // Light Blue 400
    val InfoVariant = Color(0xFF03A9F4)     // Light Blue 500
    val InfoMuted = Color(0xFF152840)
    val OnInfo = Color(0xFF0A1420)
}
