package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shimmer loading effect για premium loading states.
 * Χρησιμοποιείται όταν περιμένουμε data από το Firebase.
 */

// ============ Shimmer Colors ============

/**
 * Δημιουργεί τα χρώματα για το shimmer effect.
 * Χρησιμοποιεί surface colors για consistency με το theme.
 */
@Composable
private fun shimmerColors(): List<Color> {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    
    return listOf(
        baseColor,
        highlightColor,
        baseColor
    )
}

// ============ Shimmer Effect ============

/**
 * Modifier που προσθέτει shimmer animation σε οποιοδήποτε element.
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val colors = shimmerColors()
    
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(translateAnim - 500f, 0f),
        end = Offset(translateAnim, 0f)
    )
    
    return this.background(brush)
}

// ============ Shimmer Placeholder Components ============

/**
 * Shimmer box placeholder.
 * Χρησιμοποιείται για γενικά placeholder areas.
 */
@Composable
fun ShimmerBox(
    width: Dp = 100.dp,
    height: Dp = 20.dp,
    cornerRadius: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}

/**
 * Shimmer text line placeholder.
 * Χρησιμοποιείται για loading text.
 */
@Composable
fun ShimmerText(
    width: Dp = 150.dp,
    height: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        width = width,
        height = height,
        cornerRadius = 4.dp,
        modifier = modifier
    )
}

/**
 * Shimmer card placeholder.
 * Χρησιμοποιείται για loading cards.
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .shimmerEffect()
    )
}

/**
 * Shimmer hero dashboard placeholder.
 * Χρησιμοποιείται όταν φορτώνει το dashboard.
 */
@Composable
fun ShimmerHeroDashboard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            .padding(24.dp)
    ) {
        // Title shimmer
        ShimmerText(width = 120.dp, height = 14.dp)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Big number shimmer
        ShimmerBox(width = 180.dp, height = 48.dp, cornerRadius = 8.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress bar shimmer
        ShimmerBox(
            width = Dp.Unspecified,
            height = 8.dp,
            cornerRadius = 4.dp,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Secondary stats shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column {
                    ShimmerText(width = 40.dp, height = 24.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerText(width = 60.dp, height = 12.dp)
                }
            }
        }
    }
}

/**
 * Shimmer stat card placeholder.
 * Χρησιμοποιείται για loading στατιστικών.
 */
@Composable
fun ShimmerStatCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        ShimmerText(width = 100.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column {
                    ShimmerText(width = 48.dp, height = 28.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerText(width = 48.dp, height = 12.dp)
                }
            }
        }
    }
}
