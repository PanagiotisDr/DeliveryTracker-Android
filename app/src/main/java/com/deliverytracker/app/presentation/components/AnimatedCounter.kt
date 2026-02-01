package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.deliverytracker.app.presentation.theme.AnimationSpecs
import com.deliverytracker.app.presentation.theme.Duration
import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * 🔢 AnimatedCounter - Numbers that animate to target value
 * 
 * A premium animated counter component that smoothly transitions
 * between values, creating a professional "counting up" effect.
 * 
 * Features:
 * - Smooth spring animation
 * - Configurable decimal places
 * - Currency suffix support
 * - Color animation based on value
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 * @since 2026-02
 */

/**
 * Animated counter for decimal values (e.g., money)
 * 
 * @param targetValue The value to animate to
 * @param modifier Modifier for the composable
 * @param style Text style to apply
 * @param color Text color
 * @param prefix Text to show before the number (e.g., "€")
 * @param suffix Text to show after the number
 * @param decimalPlaces Number of decimal places to show
 * @param animationSpec Animation specification
 */
@Composable
fun AnimatedCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    prefix: String = "",
    suffix: String = "",
    decimalPlaces: Int = 2,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.springDefault()
) {
    // Animate the value
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = animationSpec,
        label = "counter"
    )
    
    // Format the number
    val pattern = buildString {
        append("#,##0")
        if (decimalPlaces > 0) {
            append(".")
            repeat(decimalPlaces) { append("0") }
        }
    }
    val formatter = remember(pattern) { DecimalFormat(pattern) }
    val formattedValue = formatter.format(animatedValue.toDouble())
    
    Text(
        text = "$prefix$formattedValue$suffix",
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Animated counter for integer values (e.g., order count)
 * 
 * @param targetValue The value to animate to
 * @param modifier Modifier for the composable
 * @param style Text style to apply
 * @param color Text color
 * @param prefix Text to show before the number
 * @param suffix Text to show after the number
 * @param animationSpec Animation specification
 */
@Composable
fun AnimatedIntCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    prefix: String = "",
    suffix: String = "",
    animationSpec: AnimationSpec<Float> = AnimationSpecs.springDefault()
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = animationSpec,
        label = "intCounter"
    )
    
    Text(
        text = "$prefix${animatedValue.roundToInt()}$suffix",
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Animated currency display with separate styling for value and symbol
 * 
 * @param targetValue The value to animate to
 * @param modifier Modifier for the composable
 * @param valueStyle Text style for the number
 * @param symbolStyle Text style for the currency symbol
 * @param color Text color
 * @param currencySymbol Currency symbol (default: €)
 * @param symbolPosition Position of currency symbol
 */
@Composable
fun AnimatedCurrency(
    targetValue: Double,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = LocalTextStyle.current,
    symbolStyle: TextStyle = valueStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = valueStyle.fontSize * 0.7f
    ),
    color: Color = Color.Unspecified,
    currencySymbol: String = "€",
    symbolPosition: CurrencySymbolPosition = CurrencySymbolPosition.SUFFIX
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = AnimationSpecs.springDefault(),
        label = "currency"
    )
    
    val formatter = remember { DecimalFormat("#,##0.00") }
    val formattedValue = formatter.format(animatedValue.toDouble())
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        if (symbolPosition == CurrencySymbolPosition.PREFIX) {
            Text(
                text = currencySymbol,
                style = symbolStyle,
                color = color
            )
        }
        
        Text(
            text = formattedValue,
            style = valueStyle,
            color = color
        )
        
        if (symbolPosition == CurrencySymbolPosition.SUFFIX) {
            Text(
                text = currencySymbol,
                style = symbolStyle,
                color = color
            )
        }
    }
}

/**
 * Animated percentage display
 * 
 * @param targetValue The percentage value (0-1 or 0-100 based on asDecimal)
 * @param modifier Modifier for the composable
 * @param style Text style to apply
 * @param color Text color
 * @param asDecimal If true, treats value as 0-1, otherwise 0-100
 * @param decimalPlaces Number of decimal places
 */
@Composable
fun AnimatedPercentage(
    targetValue: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    asDecimal: Boolean = true,
    decimalPlaces: Int = 0
) {
    val percentage = if (asDecimal) targetValue * 100 else targetValue
    
    val animatedValue by animateFloatAsState(
        targetValue = percentage,
        animationSpec = AnimationSpecs.springDefault(),
        label = "percentage"
    )
    
    val pattern = buildString {
        append("0")
        if (decimalPlaces > 0) {
            append(".")
            repeat(decimalPlaces) { append("0") }
        }
    }
    val formatter = remember(pattern) { DecimalFormat(pattern) }
    
    Text(
        text = "${formatter.format(animatedValue.toDouble())}%",
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Animated timer display (hours:minutes or minutes:seconds)
 * 
 * @param targetMinutes Total minutes to display
 * @param modifier Modifier for the composable
 * @param style Text style to apply
 * @param color Text color
 * @param showHours If true, shows H:MM format, otherwise M:SS
 */
@Composable
fun AnimatedDuration(
    targetMinutes: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    showHours: Boolean = true
) {
    val animatedMinutes by animateIntAsState(
        targetValue = targetMinutes,
        animationSpec = tween(
            durationMillis = Duration.medium,
            easing = FastOutSlowInEasing
        ),
        label = "duration"
    )
    
    val displayText = if (showHours) {
        val hours = animatedMinutes / 60
        val mins = animatedMinutes % 60
        "${hours}h ${mins}m"
    } else {
        val mins = animatedMinutes
        val secs = 0 // For display purposes
        String.format("%d:%02d", mins, secs)
    }
    
    Text(
        text = displayText,
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Currency symbol position
 */
enum class CurrencySymbolPosition {
    PREFIX,
    SUFFIX
}
