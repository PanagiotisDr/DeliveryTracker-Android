package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.deliverytracker.app.presentation.theme.*

/**
 * 🔵 ProgressRing - Circular progress indicator with gradient
 * 
 * Premium circular progress component featuring:
 * - Smooth animation
 * - Gradient fill based on progress level
 * - Configurable size and stroke width
 * - Optional center content
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 * @since 2026-02
 */

/**
 * Animated circular progress ring with gradient
 * 
 * @param progress Progress value from 0 to 1
 * @param modifier Modifier for the composable
 * @param size Size of the ring
 * @param strokeWidth Width of the progress stroke
 * @param trackColor Color of the background track
 * @param gradientColors Colors for the progress gradient (auto-selected if null)
 * @param startAngle Starting angle for the progress arc
 * @param animationSpec Animation specification
 * @param centerContent Optional content to display in center
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.progressCircularMd,
    strokeWidth: Dp = Dimensions.progressHeightThick,
    trackColor: Color = DarkBorders.Default,
    gradientColors: List<Color>? = null,
    startAngle: Float = -90f,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.springDefault(),
    centerContent: @Composable (BoxScope.() -> Unit)? = null
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    
    // Animate progress
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = animationSpec,
        label = "progress"
    )
    
    // Select gradient based on progress level
    val effectiveGradient = gradientColors ?: getProgressGradient(clampedProgress)
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = this.size.minDimension
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(diameter - strokePx, diameter - strokePx)
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            
            // Draw track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round
                )
            )
            
            // Draw progress with gradient
            if (animatedProgress > 0) {
                val sweepAngle = animatedProgress * 360f
                
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = effectiveGradient + effectiveGradient.first(),
                        center = Offset(this.size.width / 2, this.size.height / 2)
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = strokePx,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
        
        // Center content
        centerContent?.invoke(this)
    }
}

/**
 * Progress ring with percentage display in center
 * 
 * @param progress Progress value from 0 to 1
 * @param modifier Modifier for the composable
 * @param size Size of the ring
 * @param strokeWidth Width of the progress stroke
 * @param showPercentage Whether to show percentage in center
 */
@Composable
fun ProgressRingWithLabel(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.progressCircularLg,
    strokeWidth: Dp = Dimensions.progressHeightThick,
    showPercentage: Boolean = true,
    label: String? = null
) {
    val percentage = (progress.coerceIn(0f, 1f) * 100).toInt()
    
    ProgressRing(
        progress = progress,
        modifier = modifier,
        size = size,
        strokeWidth = strokeWidth
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showPercentage) {
                AnimatedIntCounter(
                    targetValue = percentage,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    suffix = "%"
                )
            }
            
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Animated spinning progress ring for loading states
 * 
 * @param modifier Modifier for the composable
 * @param size Size of the ring
 * @param strokeWidth Width of the progress stroke
 * @param color Progress color
 */
@Composable
fun SpinningProgressRing(
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.progressCircularMd,
    strokeWidth: Dp = Dimensions.progressHeightDefault,
    color: Color = BrandColors.Primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier.size(size)) {
        val diameter = this.size.minDimension
        val strokePx = strokeWidth.toPx()
        val arcSize = Size(diameter - strokePx, diameter - strokePx)
        val topLeft = Offset(strokePx / 2, strokePx / 2)
        
        rotate(rotation) {
            // Draw spinning arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0f),
                        color.copy(alpha = 0.3f),
                        color
                    )
                ),
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

/**
 * Goal progress ring with icon/emoji in center
 * 
 * @param progress Progress value from 0 to 1
 * @param goalReached Whether the goal has been reached
 * @param modifier Modifier for the composable
 * @param size Size of the ring
 * @param emoji Emoji to display in center
 */
@Composable
fun GoalProgressRing(
    progress: Float,
    goalReached: Boolean = progress >= 1f,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.progressCircularHero,
    emoji: String = if (goalReached) "🎯" else "💪"
) {
    val gradientColors = when {
        goalReached -> Gradients.ProgressComplete
        progress >= 0.75f -> Gradients.ProgressHigh
        progress >= 0.5f -> Gradients.ProgressMedium
        else -> Gradients.ProgressLow
    }
    
    ProgressRing(
        progress = progress,
        modifier = modifier,
        size = size,
        strokeWidth = Dimensions.progressHeightThick * 1.5f,
        gradientColors = gradientColors
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            AnimatedPercentage(
                targetValue = progress,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
