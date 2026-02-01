package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * 🎨 DeliveryTracker Premium Design System - Colors
 * 
 * A sophisticated dark-first color palette designed for:
 * - OLED optimization (true blacks)
 * - High contrast for glanceable UI
 * - Semantic meaning for financial data
 * - Accessibility compliance (WCAG AA)
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// BRAND COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Primary brand color - Electric Teal
 * Used for: CTAs, links, active states, earnings indicators
 */
object BrandColors {
    val Primary = Color(0xFF00FFD1)           // Electric Teal - Main brand
    val PrimaryVariant = Color(0xFF00E5BC)    // Slightly darker for pressed states
    val PrimaryMuted = Color(0xFF00BFA5)      // Muted for backgrounds
    val PrimarySubtle = Color(0xFF003D33)     // Very subtle for tinted surfaces
    
    val Secondary = Color(0xFF00D9F5)         // Cyan - Secondary accent
    val SecondaryMuted = Color(0xFF0097A7)    // Muted cyan
    
    val Tertiary = Color(0xFFBB86FC)          // Purple - Tertiary accent
}

// ══════════════════════════════════════════════════════════════════
// SEMANTIC COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Semantic colors for financial and status indicators
 */
object SemanticColors {
    // Success - Earnings, profit, goals achieved
    val Success = Color(0xFF4ADE80)           // Bright green
    val SuccessVariant = Color(0xFF22C55E)    // Darker green
    val SuccessMuted = Color(0xFF166534)      // Container/background
    val OnSuccess = Color(0xFF052E16)         // Text on success
    
    // Warning - Approaching limits, moderate status
    val Warning = Color(0xFFFBBF24)           // Amber
    val WarningVariant = Color(0xFFF59E0B)    // Darker amber
    val WarningMuted = Color(0xFF713F12)      // Container
    val OnWarning = Color(0xFF422006)         // Text on warning
    
    // Error - Losses, expenses, critical
    val Error = Color(0xFFF87171)             // Coral red
    val ErrorVariant = Color(0xFFEF4444)      // Darker red
    val ErrorMuted = Color(0xFF7F1D1D)        // Container
    val OnError = Color(0xFF450A0A)           // Text on error
    
    // Info - Neutral information
    val Info = Color(0xFF60A5FA)              // Sky blue
    val InfoVariant = Color(0xFF3B82F6)       // Darker blue
    val InfoMuted = Color(0xFF1E3A5F)         // Container
    val OnInfo = Color(0xFF0C1929)            // Text on info
}

// ══════════════════════════════════════════════════════════════════
// DARK THEME SURFACES
// ══════════════════════════════════════════════════════════════════

/**
 * Surface colors for dark theme
 * Layered elevation system following Material 3 guidelines
 */
object DarkSurfaces {
    // Background layers (darkest to lighter)
    val Background = Color(0xFF000000)        // Pure black - OLED optimized
    val Surface = Color(0xFF0A0A0A)           // Slightly elevated
    val SurfaceContainer = Color(0xFF121212)  // Card backgrounds
    val SurfaceContainerHigh = Color(0xFF1A1A1A)  // Elevated cards
    val SurfaceContainerHighest = Color(0xFF242424) // Sheets, dialogs
    
    // Overlay surfaces
    val SurfaceVariant = Color(0xFF2A2A2A)    // Alternative surface
    val SurfaceTint = Color(0xFF00FFD1)       // For elevation tinting
    
    // Interactive surfaces
    val SurfaceHover = Color(0xFF1F1F1F)      // Hover state
    val SurfacePressed = Color(0xFF2D2D2D)   // Pressed state
    val SurfaceSelected = Color(0xFF003D33)  // Selected with brand tint
}

/**
 * Text colors for dark theme
 * Following Material 3 emphasis levels
 */
object DarkText {
    val Primary = Color(0xFFFFFFFF)           // 100% - High emphasis
    val Secondary = Color(0xFFB3B3B3)         // 70% - Medium emphasis
    val Tertiary = Color(0xFF808080)          // 50% - Low emphasis
    val Disabled = Color(0xFF4D4D4D)          // 30% - Disabled
    val OnPrimary = Color(0xFF000000)         // Text on primary color
}

/**
 * Border and divider colors
 */
object DarkBorders {
    val Default = Color(0xFF2D2D2D)           // Default border
    val Subtle = Color(0xFF1F1F1F)            // Subtle divider
    val Strong = Color(0xFF404040)            // Strong border
    val Focus = Color(0xFF00FFD1)             // Focus ring
    val Glass = Color(0x1AFFFFFF)             // Glass border (10% white)
}

// ══════════════════════════════════════════════════════════════════
// LIGHT THEME SURFACES
// ══════════════════════════════════════════════════════════════════

/**
 * Surface colors for light theme
 */
object LightSurfaces {
    val Background = Color(0xFFFAFAFA)        // Off-white
    val Surface = Color(0xFFFFFFFF)           // Pure white
    val SurfaceContainer = Color(0xFFF5F5F5)  // Card backgrounds
    val SurfaceContainerHigh = Color(0xFFEEEEEE)
    val SurfaceContainerHighest = Color(0xFFE0E0E0)
    val SurfaceVariant = Color(0xFFE8E8E8)
    val SurfaceTint = Color(0xFF00BFA5)
}

/**
 * Text colors for light theme
 */
object LightText {
    val Primary = Color(0xFF000000)           // High emphasis
    val Secondary = Color(0xFF4D4D4D)         // Medium emphasis
    val Tertiary = Color(0xFF808080)          // Low emphasis
    val Disabled = Color(0xFFB3B3B3)          // Disabled
    val OnPrimary = Color(0xFFFFFFFF)         // Text on primary
}

/**
 * Border colors for light theme
 */
object LightBorders {
    val Default = Color(0xFFE0E0E0)
    val Subtle = Color(0xFFEEEEEE)
    val Strong = Color(0xFFBDBDBD)
    val Focus = Color(0xFF00BFA5)
}

// ══════════════════════════════════════════════════════════════════
// EXPENSE CATEGORY COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Consistent colors for expense categories
 * Used in charts, cards, and icons
 */
object CategoryColors {
    val Fuel = Color(0xFFFF9500)              // Orange - Gas/petrol
    val Maintenance = Color(0xFF8B5CF6)       // Purple - Service
    val Insurance = Color(0xFF10B981)         // Emerald - Coverage
    val Tax = Color(0xFFEF4444)               // Red - Government
    val Equipment = Color(0xFF3B82F6)         // Blue - Tools
    val Phone = Color(0xFF06B6D4)             // Cyan - Communication
    val RoadTax = Color(0xFFF97316)           // Orange variant
    val KTEO = Color(0xFFEAB308)              // Yellow - Inspection
    val Fines = Color(0xFFDC2626)             // Dark red - Penalties
    val Other = Color(0xFF6B7280)             // Gray - Misc
}

// ══════════════════════════════════════════════════════════════════
// GRADIENT DEFINITIONS
// ══════════════════════════════════════════════════════════════════

/**
 * Gradient color lists for various UI elements
 */
object Gradients {
    // Hero/earnings gradients
    val Earnings = listOf(
        Color(0xFF00FFD1),
        Color(0xFF00D9F5)
    )
    
    val EarningsVibrant = listOf(
        Color(0xFF00FFD1),
        Color(0xFF00E5FF),
        Color(0xFF00B8D4)
    )
    
    // Expense gradients
    val Expenses = listOf(
        Color(0xFFF87171),
        Color(0xFFFB923C)
    )
    
    // Neutral gradients
    val Surface = listOf(
        Color(0xFF121212),
        Color(0xFF1A1A1A)
    )
    
    val Glass = listOf(
        Color(0x1A00FFD1),
        Color(0x0D00FFD1)
    )
    
    // Progress gradients based on percentage
    val ProgressLow = listOf(
        Color(0xFFEF4444),
        Color(0xFFF87171)
    )
    
    val ProgressMedium = listOf(
        Color(0xFFF59E0B),
        Color(0xFFFBBF24)
    )
    
    val ProgressHigh = listOf(
        Color(0xFF22C55E),
        Color(0xFF4ADE80)
    )
    
    val ProgressComplete = listOf(
        Color(0xFF00FFD1),
        Color(0xFF00E5FF)
    )
    
    // Shimmer effect
    val Shimmer = listOf(
        Color(0xFF1A1A1A),
        Color(0xFF2D2D2D),
        Color(0xFF1A1A1A)
    )
}

// ══════════════════════════════════════════════════════════════════
// EFFECT COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Colors for special effects like glow, shadows
 */
object EffectColors {
    val Glow = Color(0xFF00FFD1).copy(alpha = 0.3f)
    val GlowSubtle = Color(0xFF00FFD1).copy(alpha = 0.15f)
    val Shadow = Color(0xFF000000).copy(alpha = 0.5f)
    val ShadowSubtle = Color(0xFF000000).copy(alpha = 0.25f)
    val Scrim = Color(0xFF000000).copy(alpha = 0.7f)
    val ScrimLight = Color(0xFF000000).copy(alpha = 0.4f)
}

// ══════════════════════════════════════════════════════════════════
// EXTENSION FUNCTIONS
// ══════════════════════════════════════════════════════════════════

/**
 * Get gradient colors based on progress percentage
 */
fun getProgressGradient(progress: Float): List<Color> = when {
    progress >= 1f -> Gradients.ProgressComplete
    progress >= 0.75f -> Gradients.ProgressHigh
    progress >= 0.5f -> Gradients.ProgressMedium
    else -> Gradients.ProgressLow
}

/**
 * Get color for expense category
 */
fun com.deliverytracker.app.domain.model.ExpenseCategory.toColor(): Color = when (this) {
    com.deliverytracker.app.domain.model.ExpenseCategory.FUEL -> CategoryColors.Fuel
    com.deliverytracker.app.domain.model.ExpenseCategory.MAINTENANCE -> CategoryColors.Maintenance
    com.deliverytracker.app.domain.model.ExpenseCategory.INSURANCE -> CategoryColors.Insurance
    com.deliverytracker.app.domain.model.ExpenseCategory.TAX -> CategoryColors.Tax
    com.deliverytracker.app.domain.model.ExpenseCategory.EQUIPMENT -> CategoryColors.Equipment
    com.deliverytracker.app.domain.model.ExpenseCategory.PHONE -> CategoryColors.Phone
    com.deliverytracker.app.domain.model.ExpenseCategory.KTEO -> CategoryColors.KTEO
    com.deliverytracker.app.domain.model.ExpenseCategory.ROAD_TAX -> CategoryColors.RoadTax
    com.deliverytracker.app.domain.model.ExpenseCategory.FINES -> CategoryColors.Fines
    com.deliverytracker.app.domain.model.ExpenseCategory.OTHER -> CategoryColors.Other
}

// ══════════════════════════════════════════════════════════════════
// LEGACY ALIASES (for backward compatibility)
// ══════════════════════════════════════════════════════════════════

// These will be deprecated in future versions
@Deprecated("Use SemanticColors.Success", ReplaceWith("SemanticColors.Success"))
val GPay_Success = SemanticColors.Success
@Deprecated("Use SemanticColors.SuccessMuted", ReplaceWith("SemanticColors.SuccessMuted"))
val GPay_SuccessLight = SemanticColors.SuccessMuted
@Deprecated("Use EffectColors.Shadow", ReplaceWith("EffectColors.Shadow"))
val GPay_ShadowColor = EffectColors.Shadow
@Deprecated("Use SemanticColors.Error", ReplaceWith("SemanticColors.Error"))
val GPay_Error = SemanticColors.Error
@Deprecated("Use SemanticColors.ErrorMuted", ReplaceWith("SemanticColors.ErrorMuted"))
val GPay_ErrorLight = SemanticColors.ErrorMuted
@Deprecated("Use SemanticColors.Warning", ReplaceWith("SemanticColors.Warning"))
val GPay_Warning = SemanticColors.Warning
@Deprecated("Use SemanticColors.WarningMuted", ReplaceWith("SemanticColors.WarningMuted"))
val GPay_WarningLight = SemanticColors.WarningMuted

// Legacy hero gradient aliases
val HeroGradientStart = BrandColors.Primary
val HeroGradientMid = Color(0xFF00E5BC)
val HeroGradientEnd = BrandColors.Secondary
val SurfaceVariantDark = DarkSurfaces.SurfaceVariant
val SurfaceDark = DarkSurfaces.Surface
val GlassBorder = DarkBorders.Glass
val GoalSuccess = SemanticColors.Success
val GoalWarning = SemanticColors.Warning
val GoalDanger = SemanticColors.Error

// Command Center aliases (CC_ prefix)
val CC_Primary = BrandColors.Primary
val CC_OnPrimary = DarkText.OnPrimary
val CC_Success = SemanticColors.Success
val CC_Warning = SemanticColors.Warning
val CC_Error = SemanticColors.Error
val CC_Background = DarkSurfaces.Background
val CC_Surface = DarkSurfaces.SurfaceContainer
val CC_SurfaceElevated = DarkSurfaces.SurfaceContainerHigh
val CC_WidgetBg = DarkSurfaces.SurfaceContainerHigh
val CC_SheetBackground = DarkSurfaces.SurfaceContainer
val CC_SheetHandle = DarkBorders.Default
val CC_TextPrimary = DarkText.Primary
val CC_TextSecondary = DarkText.Secondary
val CC_GradientNeutral = Gradients.Surface
val CC_GradientEarnings = Gradients.Earnings

// Legacy radius alias
val cardRadius = 16 // dp as Int for backward compatibility
