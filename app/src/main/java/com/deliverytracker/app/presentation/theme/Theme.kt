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
 * - Dark-first design following Material guidelines
 * - Background #121212 (NOT pure black per M3 recommendations)
 * - Desaturated accent colors (tonal 200-300) for eye comfort
 * - Text at 87% white for reduced eye strain
 * - Custom color scheme with semantic colors
 * - Edge-to-edge support
 * 
 * References:
 * - https://m3.material.io/styles/color
 * - https://uxplanet.org/8-tips-for-dark-theme-design
 * 
 * @author DeliveryTracker Team
 * @version 3.0.0 - Material 3 Compliant
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// COLOR SCHEMES
// ══════════════════════════════════════════════════════════════════

/**
 * Dark color scheme - Primary design target
 * 
 * Following Material 3 dark theme guidelines:
 * - Background #121212 (recommended, not pure black)
 * - Desaturated primary (tonal 200) for reduced vibration
 * - Text at 87% white for readability
 * - Elevation = lighter surfaces
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors (desaturated teal 200)
    primary = BrandColors.Primary,              // #80DFD2 - soft teal
    onPrimary = DarkText.OnPrimary,             // Black text on primary
    primaryContainer = BrandColors.PrimarySubtle, // Dark teal container
    onPrimaryContainer = BrandColors.Primary,
    
    // Secondary colors (light blue 200)
    secondary = BrandColors.Secondary,          // #81D4FA - soft blue
    onSecondary = DarkText.OnPrimary,
    secondaryContainer = Color(0xFF0D3347),     // Dark blue container
    onSecondaryContainer = BrandColors.Secondary,
    
    // Tertiary colors (purple 200)
    tertiary = BrandColors.Tertiary,            // #CE93D8 - soft purple
    onTertiary = DarkText.OnPrimary,
    tertiaryContainer = Color(0xFF2D1F33),      // Dark purple container
    onTertiaryContainer = BrandColors.Tertiary,
    
    // Error colors (red 200)
    error = SemanticColors.Error,               // #EF9A9A - soft red
    onError = DarkText.OnPrimary,
    errorContainer = SemanticColors.ErrorMuted, // Dark red container
    onErrorContainer = SemanticColors.Error,
    
    // Background and surfaces (#121212 base)
    background = DarkSurfaces.Background,       // #121212 - Material recommended
    onBackground = DarkText.Primary,            // 87% white
    surface = DarkSurfaces.Surface,             // #121212 - base
    onSurface = DarkText.Primary,               // 87% white
    
    // Surface containers (elevated = lighter)
    surfaceContainerLowest = DarkSurfaces.Background,
    surfaceContainerLow = DarkSurfaces.SurfaceElevation1,
    surfaceContainer = DarkSurfaces.SurfaceElevation4,
    surfaceContainerHigh = DarkSurfaces.SurfaceElevation8,
    surfaceContainerHighest = DarkSurfaces.SurfaceElevation16,
    
    // Surface variants
    surfaceVariant = DarkSurfaces.SurfaceVariant,
    onSurfaceVariant = DarkText.Secondary,      // 60% white
    surfaceTint = DarkSurfaces.SurfaceTint,
    
    // Inverse colors (for snackbars, etc)
    inverseSurface = LightSurfaces.Surface,
    inverseOnSurface = LightText.Primary,
    inversePrimary = BrandColors.PrimaryMuted,
    
    // Outlines
    outline = DarkBorders.Default,              // #2E2E2E
    outlineVariant = DarkBorders.Subtle,        // #232323
    
    // Scrim (overlay)
    scrim = EffectColors.Scrim                  // 60% dark
)

/**
 * Light color scheme - Alternative theme
 * Uses more saturated colors (tonal 500-600) for light backgrounds
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors (saturated teal 500)
    primary = BrandColors.PrimaryLight,         // #00BFA5 - vibrant teal
    onPrimary = LightText.OnPrimary,            // White text
    primaryContainer = Color(0xFFB2F5EA),       // Light teal container
    onPrimaryContainer = Color(0xFF00201D),     // Dark teal text
    
    // Secondary colors (blue 600)
    secondary = BrandColors.SecondaryMuted,     // #0277BD
    onSecondary = LightText.OnPrimary,
    secondaryContainer = Color(0xFFB3E5FC),     // Light blue container
    onSecondaryContainer = Color(0xFF001F2A),
    
    // Tertiary colors (purple 500)
    tertiary = Color(0xFF7B5CB0),
    onTertiary = LightText.OnPrimary,
    tertiaryContainer = Color(0xFFE8DEF8),
    onTertiaryContainer = Color(0xFF1D1D2C),
    
    // Error colors (red 500)
    error = SemanticColors.ErrorVariant,        // #EF5350
    onError = LightText.OnPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Background and surfaces (near white)
    background = LightSurfaces.Background,      // #FAFAFA
    onBackground = LightText.Primary,           // Near black
    surface = LightSurfaces.Surface,            // #FFFFFF
    onSurface = LightText.Primary,
    
    // Surface containers
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = LightSurfaces.SurfaceContainer,
    surfaceContainerHigh = LightSurfaces.SurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaces.SurfaceContainerHighest,
    
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
