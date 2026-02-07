package com.deliverytracker.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 🎨 DeliveryTracker Premium Theme - Warm Edition
 * 
 * Material 3 theme με:
 * - Deep Orange/Gold brand colors
 * - Glassmorphism effects
 * - Warm-tinted surfaces
 * - Light + Dark mode
 * 
 * @version 4.0.0 - Warm Premium Redesign
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME - Warm Premium
// ══════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    // Primary - Warm Amber
    primary = BrandColors.Primary,              // #FFB74D
    onPrimary = DarkText.OnPrimary,             // Black
    primaryContainer = BrandColors.PrimarySubtle,
    onPrimaryContainer = BrandColors.Orange200,
    
    // Secondary - Gold/Amber
    secondary = BrandColors.Secondary,          // #FFC107
    onSecondary = DarkText.OnPrimary,
    secondaryContainer = BrandColors.SecondarySubtle,
    onSecondaryContainer = BrandColors.Gold200,
    
    // Tertiary - Green (profit)
    tertiary = BrandColors.Tertiary,
    onTertiary = DarkText.OnPrimary,
    tertiaryContainer = Color(0xFF1B3D1B),
    onTertiaryContainer = BrandColors.Tertiary,
    
    // Error
    error = SemanticColors.Error,
    onError = DarkText.OnPrimary,
    errorContainer = SemanticColors.ErrorMuted,
    onErrorContainer = SemanticColors.Error,
    
    // Background & Surfaces - Warm tinted
    background = DarkSurfaces.Background,       // #141210
    onBackground = DarkText.Primary,
    surface = DarkSurfaces.Surface,
    onSurface = DarkText.Primary,
    
    // Surface containers
    surfaceContainerLowest = DarkSurfaces.Background,
    surfaceContainerLow = DarkSurfaces.SurfaceElevation1,
    surfaceContainer = DarkSurfaces.SurfaceElevation4,
    surfaceContainerHigh = DarkSurfaces.SurfaceElevation8,
    surfaceContainerHighest = DarkSurfaces.SurfaceElevation16,
    
    // Surface variants
    surfaceVariant = DarkSurfaces.SurfaceVariant,
    onSurfaceVariant = DarkText.Secondary,
    surfaceTint = DarkSurfaces.SurfaceTint,
    
    // Inverse
    inverseSurface = LightSurfaces.Surface,
    inverseOnSurface = LightText.Primary,
    inversePrimary = BrandColors.PrimaryMuted,
    
    // Outlines
    outline = DarkBorders.Default,
    outlineVariant = DarkBorders.Subtle,
    
    // Scrim
    scrim = Color(0x99141210)
)

// ══════════════════════════════════════════════════════════════════
// LIGHT COLOR SCHEME - Warm Cream
// ══════════════════════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    // Primary - Warm Amber (darker for contrast)
    primary = BrandColors.PrimaryLight,         // #F57C00
    onPrimary = LightText.OnPrimary,
    primaryContainer = BrandColors.Orange100,
    onPrimaryContainer = BrandColors.Orange900,
    
    // Secondary - Gold/Amber
    secondary = BrandColors.SecondaryMuted,     // #FF8F00
    onSecondary = LightText.OnPrimary,
    secondaryContainer = BrandColors.Gold100,
    onSecondaryContainer = BrandColors.Gold900,
    
    // Tertiary - Green
    tertiary = BrandColors.TertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B3D1B),
    
    // Error
    error = SemanticColors.ErrorVariant,
    onError = LightText.OnPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Background & Surfaces - Warm cream
    background = LightSurfaces.Background,      // #FFFBF5
    onBackground = LightText.Primary,
    surface = LightSurfaces.Surface,
    onSurface = LightText.Primary,
    
    // Surface containers
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFCF8),
    surfaceContainer = LightSurfaces.SurfaceContainer,
    surfaceContainerHigh = LightSurfaces.SurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaces.SurfaceContainerHighest,
    
    // Surface variants
    surfaceVariant = LightSurfaces.SurfaceVariant,
    onSurfaceVariant = LightText.Secondary,
    surfaceTint = LightSurfaces.SurfaceTint,
    
    // Inverse
    inverseSurface = DarkSurfaces.Surface,
    inverseOnSurface = DarkText.Primary,
    inversePrimary = BrandColors.Orange200,
    
    // Outlines
    outline = LightBorders.Default,
    outlineVariant = LightBorders.Subtle,
    
    // Scrim
    scrim = Color(0x99141210)
)

// ══════════════════════════════════════════════════════════════════
// EXTENDED COLORS
// ══════════════════════════════════════════════════════════════════

data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    // Glassmorphism
    val glassBackground: Color,
    val glassBorder: Color,
    val glassHighlight: Color,
    // Earnings (gold)
    val earnings: Color,
    val earningsGlow: Color
)

private val DarkExtendedColors = ExtendedColors(
    success = SemanticColors.Success,
    onSuccess = SemanticColors.OnSuccess,
    successContainer = SemanticColors.SuccessMuted,
    onSuccessContainer = SemanticColors.Success,
    warning = SemanticColors.Warning,
    onWarning = SemanticColors.OnWarning,
    warningContainer = SemanticColors.WarningMuted,
    onWarningContainer = SemanticColors.Warning,
    info = SemanticColors.Info,
    onInfo = SemanticColors.OnInfo,
    infoContainer = SemanticColors.InfoMuted,
    onInfoContainer = SemanticColors.Info,
    // Glassmorphism
    glassBackground = Color(0x1AFFFFFF),      // 10% white
    glassBorder = Color(0x33FFFFFF),          // 20% white
    glassHighlight = Color(0x0DFFFFFF),       // 5% white
    // Earnings
    earnings = BrandColors.Secondary,
    earningsGlow = Color(0x4DFFC107)          // 30% gold
)

private val LightExtendedColors = ExtendedColors(
    success = SemanticColors.SuccessVariant,
    onSuccess = Color.White,
    successContainer = Color(0xFFD1FAE5),
    onSuccessContainer = Color(0xFF052E16),
    warning = SemanticColors.WarningVariant,
    onWarning = Color.White,
    warningContainer = Color(0xFFFEF3C7),
    onWarningContainer = Color(0xFF422006),
    info = SemanticColors.InfoVariant,
    onInfo = Color.White,
    infoContainer = Color(0xFFDBEAFE),
    onInfoContainer = Color(0xFF0C1929),
    // Glassmorphism (light mode)
    glassBackground = Color(0x80FFFFFF),       // 50% white
    glassBorder = Color(0x26000000),            // 15% black
    glassHighlight = Color(0x0D000000),
    // Earnings
    earnings = BrandColors.SecondaryMuted,
    earningsGlow = Color(0x26FFC107)
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

// ══════════════════════════════════════════════════════════════════
// THEME COMPOSABLE
// ══════════════════════════════════════════════════════════════════

@Composable
fun DeliveryTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    // Status bar & navigation bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Edge-to-edge
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Icon colors based on theme
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = MaterialTheme.shapes.copy(
                small = Shapes.Small,
                medium = Shapes.Medium,
                large = Shapes.Large,
                extraLarge = Shapes.ExtraLarge
            ),
            content = content
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// EXTENSION PROPERTIES
// ══════════════════════════════════════════════════════════════════

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

val ColorScheme.success: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.success

val ColorScheme.warning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.warning

val ColorScheme.info: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.info

val ColorScheme.earnings: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.earnings

// Glassmorphism helpers
val ColorScheme.glassBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.glassBackground

val ColorScheme.glassBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.glassBorder
