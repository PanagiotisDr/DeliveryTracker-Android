package com.deliverytracker.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 📐 DeliveryTracker Premium Design System - Spacing & Dimensions
 * 
 * Consistent spacing scale based on 4dp base unit.
 * Ensures visual harmony and alignment across the app.
 * 
 * Scale: 4, 8, 12, 16, 20, 24, 32, 40, 48, 64, 80, 96
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// SPACING TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Spacing scale for margins, paddings, and gaps
 */
object Spacing {
    /** Extra extra small: 4dp - Tight spacing */
    val xxs: Dp = 4.dp
    
    /** Extra small: 8dp - Compact elements */
    val xs: Dp = 8.dp
    
    /** Small: 12dp - Related elements */
    val sm: Dp = 12.dp
    
    /** Medium: 16dp - Default spacing */
    val md: Dp = 16.dp
    
    /** Large: 20dp - Grouped sections */
    val lg: Dp = 20.dp
    
    /** Extra large: 24dp - Section breaks */
    val xl: Dp = 24.dp
    
    /** Extra extra large: 32dp - Major sections */
    val xxl: Dp = 32.dp
    
    /** Huge: 40dp - Hero sections */
    val huge: Dp = 40.dp
    
    /** Massive: 48dp - Screen edges */
    val massive: Dp = 48.dp
    
    /** Giant: 64dp - Large gaps */
    val giant: Dp = 64.dp
    
    // ────────────────────────────────────────────────────────────────
    // SEMANTIC ALIASES
    // ────────────────────────────────────────────────────────────────
    
    /** Screen horizontal padding */
    val screenHorizontal: Dp = md
    
    /** Screen vertical padding */
    val screenVertical: Dp = lg
    
    /** Card internal padding */
    val cardPadding: Dp = md
    
    /** Card corner radius */
    val cardRadius: Dp = 16.dp
    
    /** List item vertical padding */
    val listItemVertical: Dp = sm
    
    /** Space between cards */
    val cardGap: Dp = sm
    
    /** Space between sections */
    val sectionGap: Dp = xl
    
    /** Screen padding (alias for horizontal) */
    val screenPadding: Dp = md
    
    /** Widget internal padding */
    val widgetPadding: Dp = md
    
    /** Widget corner radius */
    val widgetRadius: Dp = 16.dp
    
    /** Bottom navigation height */
    val bottomNavHeight: Dp = 80.dp
    
    /** FAB offset from bottom */
    val fabOffset: Dp = 16.dp
    
    // ────────────────────────────────────────────────────────────────
    // CORNER RADIUS
    // ────────────────────────────────────────────────────────────────
    
    /** No radius */
    val radiusNone: Dp = 0.dp
    
    /** Small radius: 4dp - Chips, small buttons */
    val radiusXs: Dp = 4.dp
    
    /** Small radius: 8dp - Small cards */
    val radiusSm: Dp = 8.dp
    
    /** Medium radius: 12dp - Default cards */
    val radiusMd: Dp = 12.dp
    
    /** Large radius: 16dp - Large cards */
    val radiusLg: Dp = 16.dp
    
    /** Extra large radius: 20dp - Hero cards */
    val radiusXl: Dp = 20.dp
    
    /** Extra extra large radius: 24dp - Sheets */
    val radiusXxl: Dp = 24.dp
    
    /** Full radius: 9999dp - Pills, circles */
    val radiusFull: Dp = 9999.dp
}

// ══════════════════════════════════════════════════════════════════
// SHAPE TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Pre-defined shapes for common use cases
 */
object Shapes {
    /** No rounding */
    val None = RoundedCornerShape(Spacing.radiusNone)
    
    /** Extra small rounding - Chips */
    val ExtraSmall = RoundedCornerShape(Spacing.radiusXs)
    
    /** Small rounding - Compact cards */
    val Small = RoundedCornerShape(Spacing.radiusSm)
    
    /** Medium rounding - Default cards */
    val Medium = RoundedCornerShape(Spacing.radiusMd)
    
    /** Large rounding - Primary cards */
    val Large = RoundedCornerShape(Spacing.radiusLg)
    
    /** Extra large rounding - Hero sections */
    val ExtraLarge = RoundedCornerShape(Spacing.radiusXl)
    
    /** Extra extra large - Bottom sheets */
    val ExtraExtraLarge = RoundedCornerShape(Spacing.radiusXxl)
    
    /** Full rounding - Pills, badges */
    val Full = RoundedCornerShape(Spacing.radiusFull)
    
    /** Top only rounding - Sheets that slide up */
    val TopLarge = RoundedCornerShape(
        topStart = Spacing.radiusXl,
        topEnd = Spacing.radiusXl,
        bottomStart = Spacing.radiusNone,
        bottomEnd = Spacing.radiusNone
    )
    
    /** Bottom only rounding - Headers */
    val BottomLarge = RoundedCornerShape(
        topStart = Spacing.radiusNone,
        topEnd = Spacing.radiusNone,
        bottomStart = Spacing.radiusXl,
        bottomEnd = Spacing.radiusXl
    )
}

// ══════════════════════════════════════════════════════════════════
// DIMENSION TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Fixed dimensions for specific UI elements
 */
object Dimensions {
    // ────────────────────────────────────────────────────────────────
    // ICONS
    // ────────────────────────────────────────────────────────────────
    
    /** Extra small icon: 16dp */
    val iconXs: Dp = 16.dp
    
    /** Small icon: 20dp */
    val iconSm: Dp = 20.dp
    
    /** Medium icon: 24dp - Default */
    val iconMd: Dp = 24.dp
    
    /** Large icon: 32dp */
    val iconLg: Dp = 32.dp
    
    /** Extra large icon: 40dp */
    val iconXl: Dp = 40.dp
    
    /** Huge icon: 48dp - Hero icons */
    val iconHuge: Dp = 48.dp
    
    // ────────────────────────────────────────────────────────────────
    // TOUCH TARGETS
    // ────────────────────────────────────────────────────────────────
    
    /** Minimum touch target: 48dp (accessibility requirement) */
    val touchTargetMin: Dp = 48.dp
    
    /** Comfortable touch target: 56dp */
    val touchTargetComfortable: Dp = 56.dp
    
    // ────────────────────────────────────────────────────────────────
    // BUTTONS
    // ────────────────────────────────────────────────────────────────
    
    /** Small button height: 36dp */
    val buttonHeightSm: Dp = 36.dp
    
    /** Medium button height: 44dp */
    val buttonHeightMd: Dp = 44.dp
    
    /** Large button height: 52dp */
    val buttonHeightLg: Dp = 52.dp
    
    /** FAB size: 56dp */
    val fabSize: Dp = 56.dp
    
    /** Extended FAB height: 56dp */
    val fabExtendedHeight: Dp = 56.dp
    
    // ────────────────────────────────────────────────────────────────
    // CARDS
    // ────────────────────────────────────────────────────────────────
    
    /** Small card min height: 80dp */
    val cardHeightSm: Dp = 80.dp
    
    /** Medium card min height: 120dp */
    val cardHeightMd: Dp = 120.dp
    
    /** Hero card height: 200dp */
    val heroCardHeight: Dp = 200.dp
    
    // ────────────────────────────────────────────────────────────────
    // PROGRESS INDICATORS
    // ────────────────────────────────────────────────────────────────
    
    /** Thin progress bar: 4dp */
    val progressHeightThin: Dp = 4.dp
    
    /** Default progress bar: 8dp */
    val progressHeightDefault: Dp = 8.dp
    
    /** Thick progress bar: 12dp */
    val progressHeightThick: Dp = 12.dp
    
    /** Small circular progress: 32dp */
    val progressCircularSm: Dp = 32.dp
    
    /** Medium circular progress: 48dp */
    val progressCircularMd: Dp = 48.dp
    
    /** Large circular progress: 64dp */
    val progressCircularLg: Dp = 64.dp
    
    /** Hero circular progress: 120dp */
    val progressCircularHero: Dp = 120.dp
    
    // ────────────────────────────────────────────────────────────────
    // BORDERS
    // ────────────────────────────────────────────────────────────────
    
    /** Hairline border: 0.5dp */
    val borderHairline: Dp = 0.5.dp
    
    /** Thin border: 1dp */
    val borderThin: Dp = 1.dp
    
    /** Medium border: 2dp */
    val borderMedium: Dp = 2.dp
    
    /** Thick border: 3dp */
    val borderThick: Dp = 3.dp
    
    // ────────────────────────────────────────────────────────────────
    // ELEVATION
    // ────────────────────────────────────────────────────────────────
    
    /** No elevation */
    val elevationNone: Dp = 0.dp
    
    /** Subtle elevation: 1dp */
    val elevationXs: Dp = 1.dp
    
    /** Low elevation: 2dp */
    val elevationSm: Dp = 2.dp
    
    /** Medium elevation: 4dp */
    val elevationMd: Dp = 4.dp
    
    /** High elevation: 8dp */
    val elevationLg: Dp = 8.dp
    
    /** Very high elevation: 16dp */
    val elevationXl: Dp = 16.dp
    
    // ────────────────────────────────────────────────────────────────
    // NAVIGATION
    // ────────────────────────────────────────────────────────────────
    
    /** Bottom nav bar height: 80dp */
    val bottomNavHeight: Dp = 80.dp
    
    /** Top app bar height: 64dp */
    val topAppBarHeight: Dp = 64.dp
    
    /** Navigation rail width: 80dp */
    val navRailWidth: Dp = 80.dp
}

// ══════════════════════════════════════════════════════════════════
// Z-INDEX TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Z-index values for layering
 * Using Float for Compose zIndex modifier
 */
object ZIndex {
    /** Base content */
    const val base: Float = 0f
    
    /** Dropdown menus */
    const val dropdown: Float = 10f
    
    /** Sticky headers */
    const val sticky: Float = 20f
    
    /** FAB */
    const val fab: Float = 30f
    
    /** Bottom sheets */
    const val sheet: Float = 40f
    
    /** Navigation */
    const val navigation: Float = 50f
    
    /** Modals/Dialogs */
    const val modal: Float = 60f
    
    /** Tooltips */
    const val tooltip: Float = 70f
    
    /** Toast/Snackbar */
    const val toast: Float = 80f
    
    /** Maximum - Loading overlay */
    const val max: Float = 100f
}
