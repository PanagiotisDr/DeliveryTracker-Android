package com.deliverytracker.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light color scheme για το DeliveryTracker.
 * Χρησιμοποιείται όταν το θέμα είναι φωτεινό.
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    
    secondary = Secondary,
    onSecondary = SurfaceLight,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    
    tertiary = Tertiary,
    onTertiary = SurfaceLight,
    tertiaryContainer = TertiaryLight,
    
    background = BackgroundLight,
    onBackground = SurfaceDark,
    
    surface = SurfaceLight,
    onSurface = SurfaceDark,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = SurfaceDark
)

/**
 * Dark color scheme για το DeliveryTracker.
 * Χρησιμοποιείται όταν το θέμα είναι σκοτεινό.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = PrimaryDark,
    primaryContainer = Primary,
    onPrimaryContainer = PrimaryLight,
    
    secondary = SecondaryLight,
    onSecondary = SecondaryDark,
    secondaryContainer = Secondary,
    onSecondaryContainer = SecondaryLight,
    
    tertiary = TertiaryLight,
    onTertiary = PrimaryDark,
    tertiaryContainer = Tertiary,
    
    background = BackgroundDark,
    onBackground = SurfaceLight,
    
    surface = SurfaceDark,
    onSurface = SurfaceLight,
    surfaceVariant = BackgroundDark,
    onSurfaceVariant = SurfaceLight
)

/**
 * Το κύριο Theme του DeliveryTracker.
 * Υποστηρίζει Light, Dark και Dynamic colors (Android 12+).
 * 
 * @param darkTheme Αν το θέμα είναι σκοτεινό (default: ακολουθεί το σύστημα)
 * @param dynamicColor Αν θα χρησιμοποιηθούν dynamic colors από το wallpaper (Android 12+)
 */
@Composable
fun DeliveryTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic colors διαθέσιμα σε Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Ρύθμιση status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
