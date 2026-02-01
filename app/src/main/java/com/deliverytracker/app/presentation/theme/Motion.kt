package com.deliverytracker.app.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

/**
 * 🎬 DeliveryTracker Premium Design System - Motion & Animations
 * 
 * Consistent animation specs following Material Motion guidelines.
 * Provides smooth, professional transitions and micro-interactions.
 * 
 * Principles:
 * - Quick and responsive (not sluggish)
 * - Natural easing (decelerate for entering, accelerate for exiting)
 * - Purposeful (every animation has meaning)
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 * @since 2026-02
 */

// ══════════════════════════════════════════════════════════════════
// DURATION TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Duration values for animations (in milliseconds)
 */
object Duration {
    /** Instant: 0ms - No animation */
    const val instant: Int = 0
    
    /** Extra short: 100ms - Micro-interactions */
    const val extraShort: Int = 100
    
    /** Short: 150ms - Quick feedback */
    const val short: Int = 150
    
    /** Medium short: 200ms - State changes */
    const val mediumShort: Int = 200
    
    /** Medium: 300ms - Default transitions */
    const val medium: Int = 300
    
    /** Medium long: 400ms - Complex transitions */
    const val mediumLong: Int = 400
    
    /** Long: 500ms - Large elements */
    const val long: Int = 500
    
    /** Extra long: 700ms - Hero animations */
    const val extraLong: Int = 700
    
    /** Stagger delay: 50ms - For list items */
    const val stagger: Int = 50
}

// ══════════════════════════════════════════════════════════════════
// EASING TOKENS
// ══════════════════════════════════════════════════════════════════

/**
 * Easing curves for natural motion
 */
object Easing {
    /** Standard easing - General purpose */
    val standard: androidx.compose.animation.core.Easing = FastOutSlowInEasing
    
    /** Decelerate - Elements entering screen */
    val decelerate: androidx.compose.animation.core.Easing = LinearOutSlowInEasing
    
    /** Accelerate - Elements leaving screen */
    val accelerate: androidx.compose.animation.core.Easing = FastOutLinearInEasing
    
    /** Linear - Constant speed (use sparingly) */
    val linear: androidx.compose.animation.core.Easing = LinearEasing
    
    /** Emphasized - Extra expressiveness */
    val emphasized: androidx.compose.animation.core.Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    
    /** Emphasized decelerate - Entering with emphasis */
    val emphasizedDecelerate: androidx.compose.animation.core.Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    
    /** Emphasized accelerate - Exiting with emphasis */
    val emphasizedAccelerate: androidx.compose.animation.core.Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    
    /** Bounce - Playful, use for celebrations */
    val bounce: androidx.compose.animation.core.Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
}

// ══════════════════════════════════════════════════════════════════
// ANIMATION SPECS
// ══════════════════════════════════════════════════════════════════

/**
 * Pre-configured animation specs for common use cases
 */
object AnimationSpecs {
    // ────────────────────────────────────────────────────────────────
    // TWEEN SPECS
    // ────────────────────────────────────────────────────────────────
    
    /** Quick micro-interaction */
    fun <T> microInteraction(): TweenSpec<T> = tween(
        durationMillis = Duration.extraShort,
        easing = Easing.standard
    )
    
    /** Default tween */
    fun <T> default(): TweenSpec<T> = tween(
        durationMillis = Duration.medium,
        easing = Easing.standard
    )
    
    /** Enter screen */
    fun <T> enter(): TweenSpec<T> = tween(
        durationMillis = Duration.mediumLong,
        easing = Easing.emphasizedDecelerate
    )
    
    /** Exit screen */
    fun <T> exit(): TweenSpec<T> = tween(
        durationMillis = Duration.mediumShort,
        easing = Easing.emphasizedAccelerate
    )
    
    /** Fade animation */
    fun <T> fade(): TweenSpec<T> = tween(
        durationMillis = Duration.short,
        easing = Easing.linear
    )
    
    /** Scale animation */
    fun <T> scale(): TweenSpec<T> = tween(
        durationMillis = Duration.mediumShort,
        easing = Easing.emphasized
    )
    
    /** Hero/large element */
    fun <T> hero(): TweenSpec<T> = tween(
        durationMillis = Duration.extraLong,
        easing = Easing.emphasized
    )
    
    // ────────────────────────────────────────────────────────────────
    // SPRING SPECS
    // ────────────────────────────────────────────────────────────────
    
    /** Default spring - Responsive */
    fun <T> springDefault(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /** Quick spring - Snappy */
    fun <T> springQuick(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
    
    /** Bouncy spring - Playful */
    fun <T> springBouncy(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    /** Gentle spring - Slow and elegant */
    fun <T> springGentle(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    // ────────────────────────────────────────────────────────────────
    // INFINITE SPECS
    // ────────────────────────────────────────────────────────────────
    
    /** Shimmer animation */
    val shimmer: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 1200,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart
    )
    
    /** Pulse animation */
    val pulse: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 1500,
            easing = Easing.standard
        ),
        repeatMode = RepeatMode.Reverse
    )
    
    /** Glow animation */
    val glow: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 2000,
            easing = Easing.standard
        ),
        repeatMode = RepeatMode.Reverse
    )
    
    /** Breathing animation - subtle size change */
    val breathing: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 3000,
            easing = Easing.standard
        ),
        repeatMode = RepeatMode.Reverse
    )
}

// ══════════════════════════════════════════════════════════════════
// TRANSITION HELPERS
// ══════════════════════════════════════════════════════════════════

/**
 * Animated counter that smoothly transitions between values
 */
@Composable
fun animatedFloat(
    targetValue: Float,
    label: String = "float",
    animationSpec: AnimationSpec<Float> = AnimationSpecs.default()
): Float {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = label
    )
    return animatedValue
}

/**
 * Animated integer counter
 */
@Composable
fun animatedInt(
    targetValue: Int,
    label: String = "int",
    animationSpec: AnimationSpec<Int> = AnimationSpecs.default()
): Int {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = label
    )
    return animatedValue
}

/**
 * Animated color transition
 */
@Composable
fun animatedColor(
    targetColor: Color,
    label: String = "color",
    animationSpec: AnimationSpec<Color> = AnimationSpecs.default()
): Color {
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = label
    )
    return animatedColor
}

// ══════════════════════════════════════════════════════════════════
// STAGGER ANIMATION HELPERS
// ══════════════════════════════════════════════════════════════════

/**
 * Calculate stagger delay for list items
 * @param index Item index in list
 * @param baseDelay Base delay between items
 * @param maxDelay Maximum total delay
 */
fun staggerDelay(
    index: Int,
    baseDelay: Int = Duration.stagger,
    maxDelay: Int = 500
): Int {
    return (index * baseDelay).coerceAtMost(maxDelay)
}

// ══════════════════════════════════════════════════════════════════
// HAPTIC FEEDBACK CONSTANTS
// ══════════════════════════════════════════════════════════════════

/**
 * Haptic feedback types for different interactions
 */
object HapticType {
    /** Light tap - Button press */
    const val LIGHT = "light"
    
    /** Medium tap - Selection change */
    const val MEDIUM = "medium"
    
    /** Heavy tap - Important action */
    const val HEAVY = "heavy"
    
    /** Success - Action completed */
    const val SUCCESS = "success"
    
    /** Error - Action failed */
    const val ERROR = "error"
    
    /** Warning - Approaching limit */
    const val WARNING = "warning"
}

// ══════════════════════════════════════════════════════════════════
// LEGACY ALIASES
// ══════════════════════════════════════════════════════════════════

/**
 * Legacy Motion object for backward compatibility
 */
object Motion {
    /** Standard duration */
    const val standard: Int = Duration.medium
    
    /** Emphasis/hero animation duration */
    const val emphasis: Int = Duration.mediumLong
    
    /** Quick/micro-interaction duration */
    const val quick: Int = Duration.short
    
    /** Legacy duration constants */
    const val DURATION_SHORT: Int = Duration.short
    const val DURATION_MEDIUM: Int = Duration.medium
    const val DURATION_LONG: Int = Duration.long
    
    /** Counter animation tween */
    fun <T> counterTween(): TweenSpec<T> = tween(
        durationMillis = Duration.mediumLong,
        easing = FastOutSlowInEasing
    )
    
    /** Entrance tween for cards */
    fun <T> entranceTween(delayMs: Int = 0): TweenSpec<T> = tween(
        durationMillis = Duration.medium,
        delayMillis = delayMs,
        easing = FastOutSlowInEasing
    )
}
