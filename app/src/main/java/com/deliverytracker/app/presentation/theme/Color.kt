package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color
import com.deliverytracker.app.domain.model.ExpenseCategory

/**
 * 🎨 DeliveryTracker Design System - Colors
 * 
 * Based on Material Design 3 Dark Theme Guidelines:
 * - Background: #121212 (NOT pure black)
 * - Text: 87% white for high emphasis
 * - Desaturated accent colors (tonal 200-400)
 * - Elevation = lighter surfaces
 * 
 * References:
 * - https://m3.material.io/styles/color
 * - https://uxplanet.org/8-tips-for-dark-theme-design
 * - https://blog.prototypr.io/dark-theme-material-design
 * 
 * @author DeliveryTracker Team
 * @version 3.0.0 - Material 3 Compliant
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// BRAND COLORS - Teal Palette
// ══════════════════════════════════════════════════════════════════

/**
 * Brand color palette with proper tonal values
 * Light theme uses 600 tonal, Dark theme uses 200 tonal
 */
object BrandColors {
    // Teal palette (from Material color tool)
    val Teal50 = Color(0xFFE0F7F4)      // Lightest
    val Teal100 = Color(0xFFB3EBE3)
    val Teal200 = Color(0xFF80DFD2)     // ← Dark theme primary
    val Teal300 = Color(0xFF4DD3C0)
    val Teal400 = Color(0xFF26C9B3)
    val Teal500 = Color(0xFF00BFA5)     // ← Light theme primary
    val Teal600 = Color(0xFF00A896)
    val Teal700 = Color(0xFF009184)
    val Teal800 = Color(0xFF007A73)
    val Teal900 = Color(0xFF005652)     // Darkest
    
    // Dark theme variants (desaturated)
    val Primary = Teal200                 // Main accent - soft, readable
    val PrimaryVariant = Teal300          // Hover/pressed
    val PrimaryMuted = Teal700            // Backgrounds
    val PrimarySubtle = Color(0xFF0D2F2B) // Very subtle tint
    
    // Light theme variants (saturated)
    val PrimaryLight = Teal500
    val PrimaryLightVariant = Teal600
    
    // Secondary - Cyan (desaturated for dark)
    val Secondary = Color(0xFF81D4FA)     // Light blue 200
    val SecondaryVariant = Color(0xFF4FC3F7)
    val SecondaryMuted = Color(0xFF0277BD)
    
    // Tertiary - Purple (desaturated)
    val Tertiary = Color(0xFFCE93D8)      // Purple 200
    val TertiaryVariant = Color(0xFFBA68C8)
}

// ══════════════════════════════════════════════════════════════════
// SEMANTIC COLORS - Desaturated for Dark Theme
// ══════════════════════════════════════════════════════════════════

/**
 * Semantic colors following Material guidelines
 * Using 200-300 tonal values for dark theme readability
 */
object SemanticColors {
    // Success - Green (tonal 200-300 for dark)
    val Success = Color(0xFFA5D6A7)       // Green 200 - soft
    val SuccessBright = Color(0xFF81C784) // Green 300
    val SuccessVariant = Color(0xFF66BB6A)
    val SuccessMuted = Color(0xFF1B3D20)  // Dark container
    val OnSuccess = Color(0xFF0D1F10)
    
    // Warning - Amber (tonal 200-300)
    val Warning = Color(0xFFFFE082)       // Amber 200 - soft
    val WarningBright = Color(0xFFFFD54F) // Amber 300
    val WarningVariant = Color(0xFFFFCA28)
    val WarningMuted = Color(0xFF3D3015)
    val OnWarning = Color(0xFF1F1A0A)
    
    // Error - Red (tonal 200-300)
    val Error = Color(0xFFEF9A9A)         // Red 200 - soft
    val ErrorBright = Color(0xFFE57373)   // Red 300
    val ErrorVariant = Color(0xFFEF5350)
    val ErrorMuted = Color(0xFF3D1515)
    val OnError = Color(0xFF1F0A0A)
    
    // Info - Blue (tonal 200-300)
    val Info = Color(0xFF90CAF9)          // Blue 200 - soft
    val InfoBright = Color(0xFF64B5F6)    // Blue 300
    val InfoVariant = Color(0xFF42A5F5)
    val InfoMuted = Color(0xFF152840)
    val OnInfo = Color(0xFF0A1420)
}

// ══════════════════════════════════════════════════════════════════
// DARK THEME SURFACES - Material Elevation System
// ══════════════════════════════════════════════════════════════════

/**
 * Dark theme surface colors following Material elevation
 * 
 * Base: #121212 (Material recommended, NOT #000000)
 * Each elevation level adds white overlay:
 * - 0dp: #121212 (0%)
 * - 1dp: #1D1D1D (5%)
 * - 2dp: #222222 (7%)
 * - 3dp: #252525 (8%)
 * - 4dp: #292929 (9%)
 * - 6dp: #2C2C2C (11%)
 * - 8dp: #2E2E2E (12%)
 * - 12dp: #333333 (14%)
 * - 16dp: #363636 (15%)
 * - 24dp: #383838 (16%)
 */
object DarkSurfaces {
    // Base color - Material recommended
    val Background = Color(0xFF121212)    // NOT pure black!
    
    // Elevation levels
    val Surface = Color(0xFF121212)       // 0dp - base
    val SurfaceElevation1 = Color(0xFF1D1D1D)  // 1dp
    val SurfaceElevation2 = Color(0xFF222222)  // 2dp
    val SurfaceElevation3 = Color(0xFF252525)  // 3dp
    val SurfaceElevation4 = Color(0xFF292929)  // 4dp (cards)
    val SurfaceElevation6 = Color(0xFF2C2C2C)  // 6dp
    val SurfaceElevation8 = Color(0xFF2E2E2E)  // 8dp (dialogs)
    val SurfaceElevation12 = Color(0xFF333333) // 12dp
    val SurfaceElevation16 = Color(0xFF363636) // 16dp (nav drawer)
    val SurfaceElevation24 = Color(0xFF383838) // 24dp (max)
    
    // Convenient aliases
    val SurfaceContainer = SurfaceElevation4        // Cards, widgets
    val SurfaceContainerHigh = SurfaceElevation8    // Elevated cards
    val SurfaceContainerHighest = SurfaceElevation16 // Sheets
    
    // Interactive states
    val SurfaceVariant = Color(0xFF2A2A2A)
    val SurfaceHover = Color(0xFF2A2A2A)
    val SurfacePressed = Color(0xFF333333)
    val SurfaceSelected = Color(0xFF1A3D38)   // Brand tinted
    
    // For elevation tinting
    val SurfaceTint = BrandColors.Primary
}

/**
 * Dark theme text colors
 * Following Material emphasis levels for readability
 */
object DarkText {
    val Primary = Color(0xFFDEDEDE)       // 87% white - high emphasis
    val Secondary = Color(0xFF9E9E9E)     // 62% white - medium emphasis  
    val Tertiary = Color(0xFF757575)      // 47% white - low emphasis
    val Disabled = Color(0xFF616161)      // 38% white - disabled
    val OnPrimary = Color(0xFF000000)     // Text on primary color
    val OnPrimaryContainer = Color(0xFFDEDEDE)
}

/**
 * Dark theme borders and dividers
 */
object DarkBorders {
    val Default = Color(0xFF2E2E2E)       // Visible but subtle
    val Subtle = Color(0xFF232323)        // Very subtle
    val Strong = Color(0xFF404040)        // Emphasized
    val Focus = BrandColors.Primary       // Focus ring
    val Glass = Color(0x1EDEDEDE)         // 12% white for glass effect
}

// ══════════════════════════════════════════════════════════════════
// LIGHT THEME SURFACES
// ══════════════════════════════════════════════════════════════════

object LightSurfaces {
    val Background = Color(0xFFFAFAFA)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceContainer = Color(0xFFF5F5F5)
    val SurfaceContainerHigh = Color(0xFFEEEEEE)
    val SurfaceContainerHighest = Color(0xFFE0E0E0)
    val SurfaceVariant = Color(0xFFE8E8E8)
    val SurfaceTint = BrandColors.PrimaryLight
}

object LightText {
    val Primary = Color(0xFF212121)       // Near black
    val Secondary = Color(0xFF616161)
    val Tertiary = Color(0xFF9E9E9E)
    val Disabled = Color(0xFFBDBDBD)
    val OnPrimary = Color(0xFFFFFFFF)
}

object LightBorders {
    val Default = Color(0xFFE0E0E0)
    val Subtle = Color(0xFFEEEEEE)
    val Strong = Color(0xFFBDBDBD)
    val Focus = BrandColors.PrimaryLight
}

// ══════════════════════════════════════════════════════════════════
// EXPENSE CATEGORY COLORS - Desaturated
// ══════════════════════════════════════════════════════════════════

object CategoryColors {
    val Fuel = Color(0xFFFFCC80)          // Orange 200
    val Maintenance = Color(0xFFB39DDB)   // Purple 200
    val Insurance = Color(0xFFA5D6A7)     // Green 200
    val Tax = Color(0xFFEF9A9A)           // Red 200
    val Equipment = Color(0xFF90CAF9)     // Blue 200
    val Phone = Color(0xFF80DEEA)         // Cyan 200
    val RoadTax = Color(0xFFFFAB91)       // Deep Orange 200
    val KTEO = Color(0xFFFFE082)          // Amber 200
    val Fines = Color(0xFFE57373)         // Red 300
    val Other = Color(0xFFB0BEC5)         // Blue Grey 200
}

// ══════════════════════════════════════════════════════════════════
// GRADIENTS - Softer for Dark Theme
// ══════════════════════════════════════════════════════════════════

object Gradients {
    // Primary earnings gradient (soft)
    val Earnings = listOf(
        BrandColors.Teal200,
        BrandColors.Secondary
    )
    
    // Hero gradient - slightly more vibrant
    val EarningsVibrant = listOf(
        BrandColors.Teal300,
        BrandColors.Teal200,
        BrandColors.Secondary
    )
    
    // Progress gradients
    val ProgressComplete = listOf(
        SemanticColors.Success,
        SemanticColors.SuccessBright
    )
    
    val ProgressHigh = listOf(
        SemanticColors.Success,
        SemanticColors.SuccessBright
    )
    
    val ProgressMedium = listOf(
        SemanticColors.Warning,
        SemanticColors.WarningBright
    )
    
    val ProgressLow = listOf(
        SemanticColors.Error,
        SemanticColors.ErrorBright
    )
    
    // Other gradients
    val Expense = listOf(
        SemanticColors.Error,
        SemanticColors.ErrorBright
    )
    
    val Success = listOf(
        SemanticColors.Success,
        SemanticColors.SuccessBright
    )
    
    val Warning = listOf(
        SemanticColors.Warning,
        SemanticColors.WarningBright
    )
    
    val Neutral = listOf(
        Color(0xFF424242),
        Color(0xFF303030)
    )
    
    val Shimmer = listOf(
        DarkSurfaces.SurfaceElevation4,
        DarkSurfaces.SurfaceElevation8,
        DarkSurfaces.SurfaceElevation4
    )
    
    val DarkOverlay = listOf(
        Color(0xCC121212),
        Color(0x99121212)
    )
    
    val Glass = listOf(
        Color(0x1A80DFD2),
        Color(0x0D80DFD2)
    )
}

// ══════════════════════════════════════════════════════════════════
// EFFECT COLORS
// ══════════════════════════════════════════════════════════════════

object EffectColors {
    val Scrim = Color(0x99121212)          // 60% dark overlay
    val ScrimLight = Color(0x4D121212)
    val Overlay = Color(0xCC121212)        // 80% dark overlay
    val Highlight = Color(0x1EDEDEDE)      // 12% white highlight
    val Shimmer = Color(0x33DEDEDE)
    val GlowSubtle = Color(0x2680DFD2)     // 15% brand
    val GlowMedium = Color(0x4D80DFD2)     // 30% brand
    val GlowStrong = Color(0x8080DFD2)     // 50% brand
}

object ProgressColors {
    val High = SemanticColors.Success
    val Medium = SemanticColors.Warning
    val Low = SemanticColors.Error
    val GoalSuccess = SemanticColors.Success
    val Track = DarkBorders.Subtle
}

// ══════════════════════════════════════════════════════════════════
// BACKWARD COMPATIBILITY ALIASES
// ══════════════════════════════════════════════════════════════════

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
val CC_GradientEarnings = Gradients.Earnings
val CC_GradientNeutral = Gradients.Neutral

// GPay style aliases
val GPay_ShadowColor = Color(0x1A000000)
val GPay_Success = SemanticColors.Success
val GPay_SuccessLight = SemanticColors.SuccessMuted
val GPay_Error = SemanticColors.Error
val GPay_ErrorLight = SemanticColors.ErrorMuted
val GPay_Warning = SemanticColors.Warning
val GPay_WarningLight = SemanticColors.WarningMuted

// Progress aliases
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
val GlowSubtle = EffectColors.GlowSubtle
val GlowMedium = EffectColors.GlowMedium
val GlowStrong = EffectColors.GlowStrong

// Helper function for progress gradient
fun getProgressGradient(progress: Float): List<Color> = when {
    progress >= 1f -> Gradients.ProgressComplete
    progress >= 0.75f -> Gradients.ProgressHigh
    progress >= 0.5f -> Gradients.ProgressMedium
    else -> Gradients.ProgressLow
}

// Category extension
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

// Utility functions
fun Color.contrastText(): Color {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luminance > 0.5) Color.Black else Color.White
}

fun Color.disabled(): Color = copy(alpha = 0.38f)
fun Color.asSurfaceTint(alpha: Float = 0.08f): Color = copy(alpha = alpha)
