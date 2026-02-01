package com.deliverytracker.app.presentation.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 📋 Swipeable Shift Card - Command Center
 * 
 * Swipe left για delete, tap για details.
 */
@Composable
fun SwipeableShiftCard(
    shift: Shift,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        label = "offsetX"
    )
    
    val deleteThreshold = -150f
    val isDeleting = offsetX < deleteThreshold
    
    // Background color animation
    val backgroundColor by animateColorAsState(
        targetValue = if (isDeleting) CC_Error else CC_Surface,
        label = "bgColor"
    )
    
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("EEE, dd MMM", Locale("el", "GR")) }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Delete background
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(Spacing.cardRadius))
                .background(CC_Error),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Spacing.xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.btn_delete),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Card content
        Surface(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                onDelete()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        // Only allow swipe left
                        offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                    }
                },
            shape = RoundedCornerShape(Spacing.cardRadius),
            color = CC_Surface,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.cardPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dateFormat.format(Date(shift.date)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CC_TextPrimary
                    )
                    Text(
                        text = shift.formattedWorkTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = CC_TextSecondary
                    )
                }
                
                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Orders
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = shift.ordersCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CC_TextPrimary
                        )
                        Text(
                            text = "📦",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Hours
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f", shift.hoursWorked),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CC_TextPrimary
                        )
                        Text(
                            text = "⏱️",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Earnings
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${decimalFormat.format(shift.netIncome)}€",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (shift.netIncome >= 0) CC_Success else CC_Error
                        )
                    }
                }
                
                // Edit button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.padding(start = Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.btn_edit),
                        tint = CC_TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * 🔮 Smart Suggestion Card - Command Center
 * 
 * AI-driven suggestion για actions.
 */
@Composable
fun SmartSuggestionCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.cardRadius),
        color = CC_SurfaceElevated,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(Spacing.radiusMd))
                    .background(CC_Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.lg))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CC_TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CC_TextSecondary
                )
            }
            
            // Arrow
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = CC_Primary
            )
        }
    }
}

/**
 * 📊 Compact Shift Card - για horizontal list
 */
@Composable
fun CompactShiftCard(
    shift: Shift,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }
    
    Surface(
        onClick = onClick,
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(Spacing.widgetRadius),
        color = CC_WidgetBg,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(Spacing.widgetPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateFormat.format(Date(shift.date)),
                style = MaterialTheme.typography.labelMedium,
                color = CC_TextSecondary
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = "${decimalFormat.format(shift.netIncome)}€",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (shift.netIncome >= 0) CC_Success else CC_Error
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "📦${shift.ordersCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CC_TextSecondary
                )
                Text(
                    text = "⏱️${String.format("%.1f", shift.hoursWorked)}h",
                    style = MaterialTheme.typography.bodySmall,
                    color = CC_TextSecondary
                )
            }
        }
    }
}
