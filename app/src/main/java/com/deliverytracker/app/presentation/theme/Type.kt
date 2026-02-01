package com.deliverytracker.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.deliverytracker.app.R

/**
 * 🔤 DeliveryTracker Premium Design System - Typography
 * 
 * Professional typography scale optimized for:
 * - Financial data display (tabular figures)
 * - Readability in various lighting conditions
 * - Accessibility compliance
 * - Greek language support
 * 
 * Font: Inter - Modern, clean, excellent number rendering
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// FONT FAMILIES
// ══════════════════════════════════════════════════════════════════

/**
 * Inter font family - Primary typeface
 * Features: Excellent Greek support, tabular figures, variable weight
 * 
 * Note: If fonts are not added to res/font/, fallback to system default
 */
val InterFontFamily = try {
    FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_bold, FontWeight.Bold)
    )
} catch (e: Exception) {
    FontFamily.Default
}

/**
 * Monospace font for numbers - ensures aligned digits
 */
val NumberFontFamily = FontFamily.Default // Will use system monospace

// ══════════════════════════════════════════════════════════════════
// TYPOGRAPHY SCALE
// ══════════════════════════════════════════════════════════════════

/**
 * Material 3 Typography implementation
 * 
 * Scale follows Material Design 3 guidelines:
 * - Display: Hero numbers, large headlines
 * - Headline: Section headers
 * - Title: Card titles, list items
 * - Body: Main content
 * - Label: Buttons, captions
 */
val AppTypography = Typography(
    // ────────────────────────────────────────────────────────────────
    // DISPLAY - For hero numbers and large promotional text
    // ────────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // ────────────────────────────────────────────────────────────────
    // HEADLINE - For section headers and important text
    // ────────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // ────────────────────────────────────────────────────────────────
    // TITLE - For card titles and list items
    // ────────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // ────────────────────────────────────────────────────────────────
    // BODY - For main content and paragraphs
    // ────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // ────────────────────────────────────────────────────────────────
    // LABEL - For buttons, chips, and captions
    // ────────────────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ══════════════════════════════════════════════════════════════════
// CUSTOM TEXT STYLES
// ══════════════════════════════════════════════════════════════════

/**
 * Custom text styles for specific use cases
 */
object CustomTextStyles {
    /**
     * Hero number style - For big earnings display
     * Extra large, bold, tight letter spacing
     */
    val HeroNumber = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1).sp
    )
    
    /**
     * Large number - For card primary values
     */
    val LargeNumber = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    )
    
    /**
     * Medium number - For secondary values
     */
    val MediumNumber = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Small number - For stats and labels
     */
    val SmallNumber = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Currency suffix - For € symbol after numbers
     */
    val CurrencySuffix = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Percentage - For progress indicators
     */
    val Percentage = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Chart label - For axis labels and legends
     */
    val ChartLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Navigation label - For bottom nav items
     */
    val NavigationLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Badge - For notification badges and chips
     */
    val Badge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp
    )
}
