package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.presentation.theme.*

/**
 * 🪟 GlassCard - Glassmorphism effect card component
 * 
 * Premium glass-effect card with:
 * - Subtle blur background
 * - Gradient border
 * - Optional glow effect
 * - Customizable transparency
 * 
 * Best used on dark backgrounds for maximum effect.
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 * @since 2026-02
 */

/**
 * Glass morphism card with blur and border effects
 * 
 * @param modifier Modifier for the card
 * @param backgroundColor Base color of the glass (with alpha)
 * @param borderColor Color of the subtle border
 * @param borderWidth Width of the border
 * @param cornerRadius Corner radius of the card
 * @param glowEnabled Whether to show subtle glow effect
 * @param glowColor Color of the glow
 * @param content Content inside the card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
    borderWidth: Dp = Dimensions.borderThin,
    cornerRadius: Dp = Spacing.radiusLg,
    glowEnabled: Boolean = false,
    glowColor: Color = Color(0x26FF5722),      // 15% orange glow
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .then(
                if (glowEnabled) {
                    Modifier.drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    glowColor,
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.maxDimension
                            ),
                            radius = size.maxDimension * 0.7f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = shape
                )
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Glass card with gradient border effect
 * 
 * @param modifier Modifier for the card
 * @param gradientColors Colors for the gradient border
 * @param backgroundColor Base color of the glass
 * @param cornerRadius Corner radius
 * @param content Content inside the card
 */
@Composable
fun GradientBorderGlassCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f),
    cornerRadius: Dp = Spacing.radiusLg,
    borderWidth: Dp = Dimensions.borderMedium,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    // Χρήση theme-aware gradient colors αν δεν παρέχονται
    val effectiveGradient = gradientColors ?: listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(effectiveGradient),
                shape = shape
            )
            .padding(borderWidth)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius - borderWidth))
                .background(backgroundColor)
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Animated glow card with pulsing effect
 * 
 * @param modifier Modifier for the card
 * @param glowColor Color of the animated glow
 * @param backgroundColor Base color
 * @param cornerRadius Corner radius
 * @param content Content inside the card
 */
@Composable
fun AnimatedGlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
    cornerRadius: Dp = Spacing.radiusLg,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = AnimationSpecs.glow,
        label = "glowAlpha"
    )
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .drawBehind {
                // Outer glow
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.maxDimension * 0.8f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        cornerRadius.toPx(),
                        cornerRadius.toPx()
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(
                    width = Dimensions.borderThin,
                    color = glowColor.copy(alpha = glowAlpha * 2),
                    shape = shape
                )
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Hero card with gradient background for main dashboard displays
 * 
 * @param modifier Modifier for the card
 * @param gradientColors Gradient colors for background
 * @param cornerRadius Corner radius (0 for full-width hero)
 * @param content Content inside the hero
 */
@Composable
fun HeroGradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>? = null,
    cornerRadius: Dp = Spacing.radiusNone,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Spacing.lg,
        vertical = Spacing.xxl
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    // Χρήση theme-aware gradient colors αν δεν παρέχονται
    val effectiveGradient = gradientColors ?: listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )
    val shape = if (cornerRadius > 0.dp) {
        RoundedCornerShape(cornerRadius)
    } else {
        RectangleShape
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = effectiveGradient,
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

/**
 * Simple elevated card with shadow
 * 
 * @param modifier Modifier for the card
 * @param backgroundColor Card background color
 * @param elevation Shadow elevation
 * @param cornerRadius Corner radius
 * @param content Content inside the card
 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = Dimensions.elevationSm,
    cornerRadius: Dp = Spacing.radiusLg,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        shadowElevation = elevation,
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Card with left accent border for lists
 * 
 * @param modifier Modifier for the card
 * @param accentColor Color of the left border accent
 * @param backgroundColor Card background
 * @param cornerRadius Corner radius
 * @param content Content inside the card
 */
@Composable
fun AccentBorderCard(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    cornerRadius: Dp = Spacing.radiusMd,
    accentWidth: Dp = Dimensions.accentWidth,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
    ) {
        // Accent border
        Box(
            modifier = Modifier
                .width(accentWidth)
                .fillMaxHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.6f)
                        )
                    )
                )
        )
        
        // Content
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(contentPadding),
            content = content
        )
    }
}
