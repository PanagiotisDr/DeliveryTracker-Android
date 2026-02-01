package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color
import com.deliverytracker.app.domain.model.ExpenseCategory


/**
 * 🎨 DeliveryTracker Premium Design System - Colors
 * 
 * A sophisticated dark-first color palette designed for:
 * - OLED optimization (true blacks)
 * - Comfortable viewing (reduced intensity for dark mode)
 * - Semantic meaning for financial data
 * - Accessibility compliance (WCAG AA)
 * 
 * Version 2.1 - Refined dark mode with softer colors
 * 
 * @author DeliveryTracker Team
 * @version 2.1.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// BRAND COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Primary brand color - Teal
 * 
 * Dark mode uses toned-down variants for eye comfort
 * Light mode uses more vibrant versions
 */
object BrandColors {
    // Dark mode friendly (reduced intensity)
    val Primary = Color(0xFF4DD9C0)           // Soft Teal - easier on eyes
    val PrimaryBright = Color(0xFF00FFD1)     // Original bright - use sparingly
    val PrimaryVariant = Color(0xFF3DBFAB)    // Pressed/hover state
    val PrimaryMuted = Color(0xFF2A8F7F)      // Backgrounds, containers
    val PrimarySubtle = Color(0xFF1A3D38)     // Very subtle tint
    
    // Light mode versions
    val PrimaryLight = Color(0xFF00BFA5)      // For light theme
    val PrimaryLightVariant = Color(0xFF009688)
    
    val Secondary = Color(0xFF5CC8E5)         // Soft Cyan
    val SecondaryMuted = Color(0xFF3A8A9E)
    
    val Tertiary = Color(0xFFB39DDB)          // Soft Purple
}

// ══════════════════════════════════════════════════════════════════
// SEMANTIC COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Semantic colors for financial and status indicators
 * Toned down for dark mode comfort
 */
object SemanticColors {
    // Success - Earnings, profit (softer green)
    val Success = Color(0xFF66D98F)           // Soft green (was #4ADE80)
    val SuccessBright = Color(0xFF4ADE80)     // Bright - for emphasis only
    val SuccessVariant = Color(0xFF4CAF7A)
    val SuccessMuted = Color(0xFF1B4332)      // Container
    val OnSuccess = Color(0xFF052E16)
    
    // Warning - Approaching limits (softer amber)
    val Warning = Color(0xFFE5B84A)           // Soft amber (was #FBBF24)
    val WarningBright = Color(0xFFFBBF24)
    val WarningVariant = Color(0xFFD4A13A)
    val WarningMuted = Color(0xFF5C4A1A)
    val OnWarning = Color(0xFF422006)
    
    // Error - Losses, expenses (softer red)
    val Error = Color(0xFFE57373)             // Soft coral (was #F87171)
    val ErrorBright = Color(0xFFF87171)
    val ErrorVariant = Color(0xFFD45757)
    val ErrorMuted = Color(0xFF5C2020)
    val OnError = Color(0xFF450A0A)
    
    // Info - Neutral information (softer blue)
    val Info = Color(0xFF7EB3F5)              // Soft sky blue
    val InfoBright = Color(0xFF60A5FA)
    val InfoVariant = Color(0xFF5A9AE5)
    val InfoMuted = Color(0xFF1A3350)
    val OnInfo = Color(0xFF0C1929)
}

// ══════════════════════════════════════════════════════════════════
// DARK THEME SURFACES
// ══════════════════════════════════════════════════════════════════

/**
 * Surface colors for dark theme
 * Pure black base for OLED, gentle elevation steps
 */
object DarkSurfaces {
    // Background layers (pure black base)
    val Background = Color(0xFF000000)        // Pure black - OLED optimized
    val Surface = Color(0xFF0D0D0D)           // Very subtle lift
    val SurfaceContainer = Color(0xFF141414)  // Card backgrounds
    val SurfaceContainerHigh = Color(0xFF1C1C1C)  // Elevated cards
    val SurfaceContainerHighest = Color(0xFF262626) // Sheets, dialogs
    
    // Overlay surfaces
    val SurfaceVariant = Color(0xFF2A2A2A)
    val SurfaceTint = Color(0xFF4DD9C0)       // Brand tint for elevation
    
    // Interactive surfaces
    val SurfaceHover = Color(0xFF1A1A1A)
    val SurfacePressed = Color(0xFF2D2D2D)
    val SurfaceSelected = Color(0xFF1A3D38)   // Brand tinted selection
}

/**
 * Text colors for dark theme
 * Following Material 3 emphasis with WCAG AA compliance
 * 
 * Key change: Primary is now 87% white, not 100%
 * This reduces eye strain while maintaining readability
 */
object DarkText {
    val Primary = Color(0xFFDEDEDE)           // 87% - High emphasis (softer)
    val Secondary = Color(0xFFA0A0A0)         // 63% - Medium emphasis
    val Tertiary = Color(0xFF707070)          // 44% - Low emphasis
    val Disabled = Color(0xFF4A4A4A)          // 29% - Disabled
    val OnPrimary = Color(0xFF000000)         // Text on primary color
    val OnPrimaryContainer = Color(0xFFDEDEDE) // Text on primary container
}

/**
 * Border and divider colors - subtle for dark mode
 */
object DarkBorders {
    val Default = Color(0xFF2A2A2A)           // Default border
    val Subtle = Color(0xFF1C1C1C)            // Subtle divider
    val Strong = Color(0xFF3D3D3D)            // Strong border
    val Focus = Color(0xFF4DD9C0)             // Focus ring (brand)
    val Glass = Color(0x14FFFFFF)             // 8% white for glass effect
}

// ══════════════════════════════════════════════════════════════════
// LIGHT THEME SURFACES
// ══════════════════════════════════════════════════════════════════

/**
 * Surface colors for light theme
 */
object LightSurfaces {
    val Background = Color(0xFFF8F9FA)        // Very light gray
    val Surface = Color(0xFFFFFFFF)           // Pure white
    val SurfaceContainer = Color(0xFFF0F2F5)
    val SurfaceContainerHigh = Color(0xFFE8EBED)
    val SurfaceContainerHighest = Color(0xFFDFE2E5)
    val SurfaceVariant = Color(0xFFE5E7EB)
    val SurfaceTint = Color(0xFF00BFA5)
}

/**
 * Text colors for light theme
 */
object LightText {
    val Primary = Color(0xFF1A1A1A)           // Near black
    val Secondary = Color(0xFF525252)
    val Tertiary = Color(0xFF737373)
    val Disabled = Color(0xFFA3A3A3)
    val OnPrimary = Color(0xFFFFFFFF)
}

/**
 * Border colors for light theme
 */
object LightBorders {
    val Default = Color(0xFFD4D4D4)
    val Subtle = Color(0xFFE5E5E5)
    val Strong = Color(0xFFB0B0B0)
    val Focus = Color(0xFF00BFA5)
}

// ══════════════════════════════════════════════════════════════════
// EXPENSE CATEGORY COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Expense category colors - slightly muted for dark mode
 */
object CategoryColors {
    val Fuel = Color(0xFFE59A40)              // Soft orange
    val Maintenance = Color(0xFF9B7ED4)       // Soft purple
    val Insurance = Color(0xFF4CAF7A)         // Soft emerald
    val Tax = Color(0xFFE55C5C)               // Soft red
    val Equipment = Color(0xFF5A9AE5)         // Soft blue
    val Phone = Color(0xFF4DB6C8)             // Soft cyan
    val RoadTax = Color(0xFFE58A4D)           // Soft orange variant
    val KTEO = Color(0xFFD4A83A)              // Soft yellow
    val Fines = Color(0xFFCC4444)             // Dark red
    val Other = Color(0xFF7A7A7A)             // Neutral gray
}

// ══════════════════════════════════════════════════════════════════
// GRADIENT DEFINITIONS
// ══════════════════════════════════════════════════════════════════

/**
 * Gradients - softer for dark mode comfort
 */
object Gradients {
    // Soft earnings gradient (primary use)
    val Earnings = listOf(
        Color(0xFF4DD9C0),
        Color(0xFF5CC8E5)
    )
    
    // Vibrant gradient - use sparingly for emphasis
    val EarningsVibrant = listOf(
        Color(0xFF3DBFAB),
        Color(0xFF4DBAD4),
        Color(0xFF4AA8C2)
    )
    
    // High contrast for hero moments only
    val EarningsHero = listOf(
        Color(0xFF00FFD1),
        Color(0xFF00E5FF)
    )
    
    // Expense gradient
    val Expense = listOf(
        Color(0xFFE57373),
        Color(0xFFE55C5C)
    )
    
    // Neutral gradient for charts
    val Neutral = listOf(
        Color(0xFF4A4A4A),
        Color(0xFF3A3A3A)
    )
    
    // Success gradient
    val Success = listOf(
        Color(0xFF66D98F),
        Color(0xFF4CAF7A)
    )
    
    // Warning gradient
    val Warning = listOf(
        Color(0xFFE5B84A),
        Color(0xFFD4A13A)
    )
    
    // Progress gradients (for rings/bars)
    val ProgressComplete = listOf(
        Color(0xFF66D98F),
        Color(0xFF4CAF7A)
    )
    
    val ProgressHigh = listOf(
        Color(0xFF66D98F),
        Color(0xFF4CAF7A)
    )
    
    val ProgressMedium = listOf(
        Color(0xFFE5B84A),
        Color(0xFFD4A13A)
    )
    
    val ProgressLow = listOf(
        Color(0xFFE57373),
        Color(0xFFE55C5C)
    )
    
    // Shimmer effect gradient
    val Shimmer = listOf(
        Color(0xFF1C1C1C),
        Color(0xFF2D2D2D),
        Color(0xFF1C1C1C)
    )
    
    // Dark overlay (for sheets)
    val DarkOverlay = listOf(
        Color(0xCC000000),
        Color(0x99000000)
    )
    
    // Glass effect
    val Glass = listOf(
        Color(0x1A4DD9C0),
        Color(0x0D4DD9C0)
    )
}

// ══════════════════════════════════════════════════════════════════
// EFFECT COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Colors for overlays, scrims, and effects
 */
object EffectColors {
    val Scrim = Color(0x99000000)              // 60% black overlay
    val ScrimLight = Color(0x4D000000)         // 30% black overlay
    val Overlay = Color(0xCC000000)            // 80% black overlay
    val Highlight = Color(0x1AFFFFFF)          // 10% white highlight
    val Shimmer = Color(0x33FFFFFF)            // Shimmer highlight
    val GlowSubtle = Color(0x264DD9C0)         // 15% brand glow
    val GlowMedium = Color(0x4D4DD9C0)         // 30% brand glow
    val GlowStrong = Color(0x804DD9C0)         // 50% brand glow
}

/**
 * Progress/Goal indicator colors
 */
object ProgressColors {
    val High = SemanticColors.Success          // 80%+ of goal
    val Medium = SemanticColors.Warning        // 50-80% of goal
    val Low = SemanticColors.Error             // <50% of goal
    val GoalSuccess = SemanticColors.Success   // Goal achieved
    val Track = DarkBorders.Subtle             // Background track
}

// Legacy aliases for progress
val ProgressComplete = SemanticColors.Success
val ProgressHigh = ProgressColors.High
val ProgressMedium = ProgressColors.Medium
val ProgressLow = ProgressColors.Low
val GoalSuccess = ProgressColors.GoalSuccess
val GoalWarning = SemanticColors.Warning
val GoalDanger = SemanticColors.Error
val Shimmer = EffectColors.Shimmer

// Hero gradient aliases
val HeroGradientStart = BrandColors.Primary
val HeroGradientMid = BrandColors.Secondary
val HeroGradientEnd = BrandColors.PrimaryMuted

// Glow effects
val GlowSubtle = BrandColors.Primary.copy(alpha = 0.15f)
val GlowMedium = BrandColors.Primary.copy(alpha = 0.3f)
val GlowStrong = BrandColors.Primary.copy(alpha = 0.5f)

// Helper function for progress gradient
fun getProgressGradient(progress: Float): List<Color> = when {
    progress >= 1f -> listOf(SemanticColors.Success, SemanticColors.SuccessVariant)
    progress >= 0.75f -> listOf(SemanticColors.Success, SemanticColors.SuccessVariant)
    progress >= 0.5f -> listOf(SemanticColors.Warning, SemanticColors.WarningVariant)
    else -> listOf(SemanticColors.Error, SemanticColors.ErrorVariant)
}

// ══════════════════════════════════════════════════════════════════
// BACKWARD COMPATIBILITY ALIASES
// ══════════════════════════════════════════════════════════════════

/**
 * Legacy color aliases for backward compatibility
 * Will be deprecated in future versions
 */

// Command Center aliases
val CC_Primary = BrandColors.Primary
val CC_PrimaryVariant = BrandColors.PrimaryVariant
val CC_Background = DarkSurfaces.Background
val CC_Surface = DarkSurfaces.Surface
val CC_WidgetBg = DarkSurfaces.SurfaceContainer
val CC_SurfaceElevated = DarkSurfaces.SurfaceContainerHigh
val CC_SheetBackground = DarkSurfaces.SurfaceContainerHighest
val CC_SheetHandle = DarkBorders.Strong
val CC_TextPrimary = DarkText.Primary
val CC_TextSecondary = DarkText.Secondary
val CC_Success = SemanticColors.Success
val CC_Error = SemanticColors.Error
val CC_OnPrimary = DarkText.OnPrimary

// Legacy gradients
val CC_GradientEarnings = Gradients.Earnings
val CC_GradientNeutral = Gradients.Neutral

// GPay style aliases (deprecated)
val GPay_ShadowColor = Color(0x1A000000)
val GPay_Success = SemanticColors.Success
val GPay_SuccessLight = SemanticColors.SuccessMuted
val GPay_Error = SemanticColors.Error
val GPay_ErrorLight = SemanticColors.ErrorMuted
val GPay_Warning = SemanticColors.Warning
val GPay_WarningLight = SemanticColors.WarningMuted

// ══════════════════════════════════════════════════════════════════
// UTILITY EXTENSIONS
// ══════════════════════════════════════════════════════════════════

/**
 * Get appropriate text color for a background
 */
fun Color.contrastText(): Color {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luminance > 0.5) Color.Black else Color.White
}

/**
 * Dim a color for disabled states
 */
fun Color.disabled(): Color = copy(alpha = 0.38f)

/**
 * Create a subtle tint for surfaces
 */
fun Color.asSurfaceTint(alpha: Float = 0.08f): Color = copy(alpha = alpha)

// ══════════════════════════════════════════════════════════════════
// EXPENSE CATEGORY EXTENSION
// ══════════════════════════════════════════════════════════════════



/**
 * Get color for expense category
 */
fun ExpenseCategory.toColor(): Color = when (this) {
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
