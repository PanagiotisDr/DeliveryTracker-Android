package com.deliverytracker.app.presentation.screens.shifts.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ════════════════════════════════════════════════════════════════════════════
// SHIFT CARD COMPONENTS
// Εξαγωγή card components από ShiftListScreen για modularity.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Premium Shift Card με glass effect.
 * Εμφανίζει ημερομηνία, ώρες, στατιστικά και action buttons.
 */
@Composable
fun PremiumShiftCard(
    shift: Shift,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale("el", "GR")) }
    
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.dialog_delete_shift_title),
            message = stringResource(R.string.dialog_delete_shift_message),
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    
    // Accent color βάσει εσόδων
    val accentColor = when {
        shift.netIncome >= 100 -> SemanticColors.Success
        shift.netIncome >= 50 -> SemanticColors.Warning
        else -> MaterialTheme.colorScheme.primary
    }
    
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = accentColor.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        // Header: Date + Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormat.format(Date(shift.date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                @Suppress("DEPRECATION")
                Text(
                    text = shift.formattedWorkTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(Dimensions.touchTargetMin)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.btn_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimensions.iconSm)
                    )
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(Dimensions.touchTargetMin)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = SemanticColors.Error,
                        modifier = Modifier.size(Dimensions.iconSm)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = Dimensions.borderHairline
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Stats Grid — 2×2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CardStat(
                value = "%.2f€".format(shift.netIncome),
                label = stringResource(R.string.dashboard_net),
                valueColor = if (shift.netIncome >= 0) SemanticColors.Success 
                            else SemanticColors.Error
            )
            CardStat(
                value = "%.1fh".format(shift.hoursWorked),
                label = stringResource(R.string.dashboard_hours),
                valueColor = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CardStat(
                value = shift.ordersCount.toString(),
                label = stringResource(R.string.dashboard_orders),
                valueColor = MaterialTheme.colorScheme.onSurface
            )
            CardStat(
                value = "%.2f€".format(shift.incomePerHour),
                label = stringResource(R.string.stats_per_hour),
                valueColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Stat widget μέσα σε cards.
 * Εμφανίζει τιμή (String) και label.
 */
@Composable
fun CardStat(
    value: String,
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = value,
            style = CustomTextStyles.SmallNumber,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
