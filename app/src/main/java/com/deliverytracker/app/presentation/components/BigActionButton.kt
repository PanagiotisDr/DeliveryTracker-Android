package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.presentation.theme.*

/**
 * 🔘 Big Action Button - Premium Component
 * 
 * A large, prominent call-to-action button with:
 * - Gradient background
 * - Press animation (scale)
 * - Glow effect
 * - Icon + Title + Subtitle
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 */
@Composable
fun BigActionButton(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.Add,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>? = null,
    enabled: Boolean = true
) {
    // Χρήση theme-aware gradient αν δεν παρέχεται
    val effectiveGradient = gradientColors ?: listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Scale animation when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    // Glow pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            // Outer glow
            .shadow(
                elevation = Dimensions.elevationXl,
                shape = Shapes.Large,
                ambientColor = effectiveGradient.first().copy(alpha = glowAlpha),
                spotColor = effectiveGradient.first().copy(alpha = glowAlpha)
            )
            .clip(Shapes.Large)
            .background(
                brush = Brush.linearGradient(
                    colors = if (enabled) effectiveGradient 
                            else listOf(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.outlineVariant)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                enabled = enabled,
                onClick = onClick
            )
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(Dimensions.iconXl)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = Shapes.Medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimensions.iconLg)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.lg))
            
            // Text content
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(Dimensions.iconMd)
            )
        }
    }
}

/**
 * Compact version of BigActionButton
 */
@Composable
fun CompactActionButton(
    title: String,
    icon: ImageVector = Icons.Default.Add,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "compactButtonScale"
    )
    
    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = Dimensions.elevationLg,
                shape = Shapes.Full,
                ambientColor = backgroundColor.copy(alpha = 0.4f),
                spotColor = backgroundColor.copy(alpha = 0.4f)
            ),
        shape = Shapes.Full,
        color = backgroundColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.xl,
                vertical = Spacing.md
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Dimensions.iconMd)
            )
            
            Spacer(modifier = Modifier.width(Spacing.sm))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
