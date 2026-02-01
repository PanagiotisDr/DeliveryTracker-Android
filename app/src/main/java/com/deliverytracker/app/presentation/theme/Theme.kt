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
 * 🎨 DeliveryTracker Premium Theme
 * 
 * Material 3 theme configuration with:
 * - Dynamic color support (Android 12+)
 * - Dark-first design optimized for OLED
 * - Custom color scheme with semantic colors
 * - Edge-to-edge support
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// COLOR SCHEMES
// ══════════════════════════════════════════════════════════════════

/**
 * Dark color scheme - Primary design target
 * Optimized for OLED screens with true blacks
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = BrandColors.Primary,
    onPrimary = DarkText.OnPrimary,
    primaryContainer = BrandColors.PrimarySubtle,
    onPrimaryContainer = BrandColors.Primary,
    
    // Secondary colors
    secondary = BrandColors.Secondary,
    onSecondary = DarkText.OnPrimary,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = BrandColors.Secondary,
    
    // Tertiary colors
    tertiary = BrandColors.Tertiary,
    onTertiary = DarkText.OnPrimary,
    tertiaryContainer = Color(0xFF3D2D6B),
    onTertiaryContainer = BrandColors.Tertiary,
    
    // Error colors
    error = SemanticColors.Error,
    onError = DarkText.OnPrimary,
    errorContainer = SemanticColors.ErrorMuted,
    onErrorContainer = SemanticColors.Error,
    
    // Background and surface
    background = DarkSurfaces.Background,
    onBackground = DarkText.Primary,
    surface = DarkSurfaces.SurfaceContainer,
    onSurface = DarkText.Primary,
    
    // Surface variants
    surfaceVariant = DarkSurfaces.SurfaceVariant,
    onSurfaceVariant = DarkText.Secondary,
    surfaceTint = DarkSurfaces.SurfaceTint,
    
    // Inverse colors
    inverseSurface = LightSurfaces.Surface,
    inverseOnSurface = LightText.Primary,
    inversePrimary = BrandColors.PrimaryMuted,
    
    // Outlines
    outline = DarkBorders.Default,
    outlineVariant = DarkBorders.Subtle,
    
    // Scrim
    scrim = EffectColors.Scrim
)

/**
 * Light color scheme - Alternative theme
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = BrandColors.PrimaryMuted,
    onPrimary = LightText.OnPrimary,
    primaryContainer = Color(0xFFB2F5EA),
    onPrimaryContainer = Color(0xFF00201D),
    
    // Secondary colors
    secondary = BrandColors.SecondaryMuted,
    onSecondary = LightText.OnPrimary,
    secondaryContainer = Color(0xFFB3E5FC),
    onSecondaryContainer = Color(0xFF001F2A),
    
    // Tertiary colors
    tertiary = Color(0xFF7B5CB0),
    onTertiary = LightText.OnPrimary,
    tertiaryContainer = Color(0xFFE8DEF8),
    onTertiaryContainer = Color(0xFF1D1D2C),
    
    // Error colors
    error = SemanticColors.ErrorVariant,
    onError = LightText.OnPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Background and surface
    background = LightSurfaces.Background,
    onBackground = LightText.Primary,
    surface = LightSurfaces.Surface,
    onSurface = LightText.Primary,
    
    // Surface variants
    surfaceVariant = LightSurfaces.SurfaceVariant,
    onSurfaceVariant = LightText.Secondary,
    surfaceTint = LightSurfaces.SurfaceTint,
    
    // Inverse colors
    inverseSurface = DarkSurfaces.Surface,
    inverseOnSurface = DarkText.Primary,
    inversePrimary = BrandColors.Primary,
    
    // Outlines
    outline = LightBorders.Default,
    outlineVariant = LightBorders.Subtle,
    
    // Scrim
    scrim = EffectColors.Scrim
)

// ══════════════════════════════════════════════════════════════════
// EXTENDED COLORS
// ══════════════════════════════════════════════════════════════════

/**
 * Extended color set for semantic colors not in Material 3
 */
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
    val onInfoContainer: Color
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
    onInfoContainer = SemanticColors.Info
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
    onInfoContainer = Color(0xFF0C1929)
)

/**
 * CompositionLocal for extended colors
 */
val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

// ══════════════════════════════════════════════════════════════════
// THEME COMPOSABLE
// ══════════════════════════════════════════════════════════════════

/**
 * Main theme composable for DeliveryTracker
 * 
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+)
 * @param content Content to apply theme to
 */
@Composable
fun DeliveryTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to preserve brand colors
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
    
    // Configure status bar and navigation bar colors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Edge-to-edge: transparent bars
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Configure system bar icons
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

/**
 * Access extended colors from MaterialTheme
 * 
 * Usage:
 * ```
 * val success = MaterialTheme.extendedColors.success
 * ```
 */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

/**
 * Quick access to semantic success color
 */
val ColorScheme.success: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.success

/**
 * Quick access to semantic warning color
 */
val ColorScheme.warning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.warning

/**
 * Quick access to semantic info color
 */
val ColorScheme.info: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current.info
