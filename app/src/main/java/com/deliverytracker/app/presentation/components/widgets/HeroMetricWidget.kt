package com.deliverytracker.app.presentation.components.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat

/**
 * 🎯 Hero Metric Widget - Command Center
 * 
 * Το μεγάλο νούμερο στην κορυφή της οθόνης.
 * Animated counter με gradient background.
 */
@Composable
fun HeroMetricWidget(
    value: Double,
    label: String,
    progress: Float = 0f,
    goalValue: Double? = null,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    // Animated number
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heroValue"
    )
    
    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = Motion.emphasis, easing = FastOutSlowInEasing),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.cardRadius))
            .background(
                brush = Brush.linearGradient(CC_GradientEarnings)
            )
            .padding(Spacing.cardPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Big Number
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = decimalFormat.format(animatedValue.toDouble()),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = "€",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Progress bar (if goal exists)
            if (goalValue != null && goalValue > 0) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Goal text
                val percentage = (animatedProgress * 100).toInt()
                Text(
                    text = "$percentage% από ${decimalFormat.format(goalValue)}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 📊 Glanceable Widget Base - Command Center
 * 
 * Base component για μικρά widgets.
 */
@Composable
fun GlanceableWidget(
    value: String,
    label: String,
    emoji: String? = null,
    valueColor: Color = CC_TextPrimary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(Spacing.widgetRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(CC_WidgetBg)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(Spacing.widgetPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji (optional)
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
            
            // Value
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            
            // Label
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CC_TextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * ⚡ Quick Action Widget - Command Center
 * 
 * Widget με action button.
 */
@Composable
fun QuickActionWidget(
    label: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(Spacing.widgetRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(CC_GradientNeutral)
            )
            .clickable(onClick = onClick)
            .padding(Spacing.widgetPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji in circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(CC_Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = CC_TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
