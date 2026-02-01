package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.deliverytracker.app.presentation.theme.*

/**
 * ✨ ShimmerLoading - Skeleton loading with shimmer effect
 * 
 * Premium loading placeholders that show content structure
 * while data is being fetched. Creates professional feel.
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 * @since 2026-02
 */

/**
 * Creates a shimmer animation brush for loading states
 */
@Composable
fun rememberShimmerBrush(
    shimmerColors: List<Color> = Gradients.Shimmer,
    animationDuration: Int = 1200
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - 500f, translateAnimation - 500f),
        end = Offset(translateAnimation, translateAnimation)
    )
}

/**
 * Basic shimmer box placeholder
 * 
 * @param modifier Modifier for the box
 * @param cornerRadius Corner radius of the placeholder
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Spacing.radiusMd
) {
    val shimmerBrush = rememberShimmerBrush()
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush)
    )
}

/**
 * Shimmer placeholder for text lines
 * 
 * @param modifier Modifier
 * @param lines Number of text lines to show
 * @param lineHeight Height of each line
 * @param lineSpacing Spacing between lines
 */
@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    lines: Int = 3,
    lineHeight: Dp = Spacing.md,
    lineSpacing: Dp = Spacing.sm,
    lastLineWidth: Float = 0.7f
) {
    val shimmerBrush = rememberShimmerBrush()
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(lineSpacing)
    ) {
        repeat(lines) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (index == lines - 1) lastLineWidth else 1f)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(Spacing.radiusXs))
                    .background(shimmerBrush)
            )
        }
    }
}

/**
 * Shimmer placeholder for a card
 * 
 * @param modifier Modifier
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = Dimensions.cardHeightSm
) {
    val shimmerBrush = rememberShimmerBrush()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(Spacing.radiusLg))
            .background(DarkSurfaces.SurfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(shimmerBrush)
        )
    }
}

/**
 * Shimmer placeholder for list items (shift/expense cards)
 */
@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberShimmerBrush()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.radiusMd))
            .background(DarkSurfaces.SurfaceContainer)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Leading icon placeholder
        Box(
            modifier = Modifier
                .size(Dimensions.iconHuge)
                .clip(RoundedCornerShape(Spacing.radiusSm))
                .background(shimmerBrush)
        )
        
        // Content placeholder
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(Spacing.md)
                    .clip(RoundedCornerShape(Spacing.radiusXs))
                    .background(shimmerBrush)
            )
            
            // Subtitle
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(Spacing.sm)
                    .clip(RoundedCornerShape(Spacing.radiusXs))
                    .background(shimmerBrush)
            )
        }
        
        // Trailing value placeholder
        Box(
            modifier = Modifier
                .width(Spacing.huge)
                .height(Spacing.lg)
                .clip(RoundedCornerShape(Spacing.radiusXs))
                .background(shimmerBrush)
        )
    }
}

/**
 * Shimmer placeholder for dashboard stats
 */
@Composable
fun ShimmerDashboardStats(
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberShimmerBrush()
    
    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Hero card shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.heroCardHeight)
                .clip(RoundedCornerShape(Spacing.radiusLg))
                .background(DarkSurfaces.SurfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.xl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                // Label
                Box(
                    modifier = Modifier
                        .width(Spacing.giant)
                        .height(Spacing.sm)
                        .clip(RoundedCornerShape(Spacing.radiusXs))
                        .background(shimmerBrush)
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Big number
                Box(
                    modifier = Modifier
                        .width(Spacing.giant * 2)
                        .height(Spacing.xxl)
                        .clip(RoundedCornerShape(Spacing.radiusSm))
                        .background(shimmerBrush)
                )
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(Spacing.xs)
                        .clip(RoundedCornerShape(Spacing.radiusFull))
                        .background(shimmerBrush)
                )
            }
        }
        
        // Stats row shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimensions.cardHeightSm)
                        .clip(RoundedCornerShape(Spacing.radiusLg))
                        .background(shimmerBrush)
                )
            }
        }
        
        // Month card shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.cardHeightMd)
                .clip(RoundedCornerShape(Spacing.radiusLg))
                .background(shimmerBrush)
        )
    }
}

/**
 * Shimmer for a list of items
 * 
 * @param itemCount Number of shimmer items to show
 */
@Composable
fun ShimmerList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        repeat(itemCount) {
            ShimmerListItem()
        }
    }
}
