package com.deliverytracker.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * 🌈 Gradient Colors - Gradient definitions, surfaces & glass effects
 *
 * Περιέχει:
 * - DarkSurfaces (Dark theme surface levels + glassmorphism)
 * - LightSurfaces (Light theme surfaces)
 * - CategoryColors (Expense category colors)
 * - GradientAliases (για hero sections και UI components)
 * - Text & Border colors
 *
 * @version 4.0.0 - Warm Premium Redesign
 */

// ══════════════════════════════════════════════════════════════════
// DARK THEME SURFACES - Premium Dark with Warm Tint
// ══════════════════════════════════════════════════════════════════

object DarkSurfaces {
    // Base - Slightly warm dark (not pure gray)
    val Background = Color(0xFF141210)
    
    // Elevation levels με subtle warm tint
    val Surface = Color(0xFF141210)         // 0dp - base
    val SurfaceElevation1 = Color(0xFF1C1916)
    val SurfaceElevation2 = Color(0xFF211E1A)
    val SurfaceElevation3 = Color(0xFF25211D)
    val SurfaceElevation4 = Color(0xFF292521)   // Cards
    val SurfaceElevation6 = Color(0xFF2C2824)
    val SurfaceElevation8 = Color(0xFF302B27)   // Dialogs
    val SurfaceElevation12 = Color(0xFF35302B)
    val SurfaceElevation16 = Color(0xFF38332E)  // Navigation
    val SurfaceElevation24 = Color(0xFF3B3631)
    
    // Aliases
    val SurfaceContainer = SurfaceElevation4
    val SurfaceContainerHigh = SurfaceElevation8
    val SurfaceContainerHighest = SurfaceElevation16
    
    // Interactive states
    val SurfaceVariant = Color(0xFF2A2520)
    val SurfaceHover = Color(0xFF302B26)
    val SurfacePressed = Color(0xFF353025)
    val SurfaceSelected = Color(0xFF3D2815)     // Orange tinted selection
    
    // Glassmorphism
    val GlassBackground = Color(0x1AFFFFFF)     // 10% white
    val GlassBorder = Color(0x33FFFFFF)         // 20% white
    val GlassHighlight = Color(0x0DFFFFFF)      // 5% white
    
    val SurfaceTint = BrandColors.Primary
}

// ══════════════════════════════════════════════════════════════════
// LIGHT THEME SURFACES - Warm Cream Tones
// ══════════════════════════════════════════════════════════════════

object LightSurfaces {
    val Background = Color(0xFFFFFBF5)      // Warm off-white
    val Surface = Color(0xFFFFFFFF)
    val SurfaceContainer = Color(0xFFFFF8F0)
    val SurfaceContainerHigh = Color(0xFFFFF3E8)
    val SurfaceContainerHighest = Color(0xFFFFEEE0)
    val SurfaceVariant = Color(0xFFFFF5ED)
    
    // Glassmorphism for light mode
    val GlassBackground = Color(0x80FFFFFF)     // 50% white
    val GlassBorder = Color(0x40000000)         // 25% black
    
    val SurfaceTint = BrandColors.PrimaryLight
}

// ══════════════════════════════════════════════════════════════════
// TEXT COLORS
// ══════════════════════════════════════════════════════════════════

object DarkText {
    val Primary = Color(0xFFF5F0EB)         // 96% warm white
    val Secondary = Color(0xFFB0A89F)       // 68% warm
    val Tertiary = Color(0xFF7A736B)        // 48% warm
    val Disabled = Color(0xFF5A544D)        // 35% warm
    val OnPrimary = Color(0xFF000000)
    val OnPrimaryContainer = Color(0xFFF5F0EB)
}

object LightText {
    val Primary = Color(0xFF1A1512)         // Near black, warm
    val Secondary = Color(0xFF5C554D)
    val Tertiary = Color(0xFF8A8279)
    val Disabled = Color(0xFFB5AEA5)
    val OnPrimary = Color(0xFFFFFFFF)
}

// ══════════════════════════════════════════════════════════════════
// BORDERS
// ══════════════════════════════════════════════════════════════════

object DarkBorders {
    val Default = Color(0xFF332E29)
    val Subtle = Color(0xFF252220)
    val Strong = Color(0xFF4A433C)
    val Focus = BrandColors.Primary
    val Glass = Color(0x1AFFFFFF)
}

object LightBorders {
    val Default = Color(0xFFE8E0D8)
    val Subtle = Color(0xFFF0EAE3)
    val Strong = Color(0xFFD0C8C0)
    val Focus = BrandColors.PrimaryLight
    val Glass = Color(0x33000000)
}

// ══════════════════════════════════════════════════════════════════
// EXPENSE CATEGORY COLORS
// ══════════════════════════════════════════════════════════════════

object CategoryColors {
    val Fuel = Color(0xFFFFB74D)            // Orange 300
    val Maintenance = Color(0xFFBA68C8)     // Purple 300
    val Insurance = Color(0xFF81C784)       // Green 300
    val Tax = Color(0xFFE57373)             // Red 300
    val Equipment = Color(0xFF64B5F6)       // Blue 300
    val Phone = Color(0xFF4DD0E1)           // Cyan 300
    val RoadTax = Color(0xFFFF8A65)         // Deep Orange 300
    val KTEO = Color(0xFFFFD54F)            // Amber 300
    val Fines = Color(0xFFEF5350)           // Red 400
    val Other = Color(0xFF90A4AE)           // Blue Grey 300
}

// ══════════════════════════════════════════════════════════════════
// GRADIENT ALIASES — Χρησιμοποιούνται σε Hero sections & components
// ══════════════════════════════════════════════════════════════════

val HeroGradientStart = BrandColors.Primary
val HeroGradientMid = BrandColors.Secondary
val HeroGradientEnd = BrandColors.PrimaryMuted

val GoalSuccess = SemanticColors.Success
val GoalWarning = SemanticColors.Warning
val GoalDanger = SemanticColors.Error
val Shimmer = Color(0x33FFFFFF)
