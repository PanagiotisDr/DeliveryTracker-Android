package com.deliverytracker.app.presentation.screens.shifts

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📋 Shift List Screen - Premium Redesign 2026
 * 
 * Features:
 * - Glass morphism cards
 * - Shimmer loading
 * - Animated list items
 * - Premium FAB
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddShift: () -> Unit,
    onNavigateToEditShift: (String) -> Unit,
    viewModel: ShiftListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.nav_shifts),
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText.Primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            tint = DarkText.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurfaces.Background
                )
            )
        },
        floatingActionButton = {
            QuickAddFAB(
                onClick = onNavigateToAddShift
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkSurfaces.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Loading state with shimmer
                uiState.isLoading -> {
                    ShimmerList(
                        modifier = Modifier.padding(Spacing.lg),
                        itemCount = 5
                    )
                }
                
                // Empty state
                uiState.shifts.isEmpty() -> {
                    EmptyState(
                        emoji = "📋",
                        title = stringResource(R.string.empty_shifts),
                        subtitle = stringResource(R.string.empty_shifts_hint),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Button(
                            onClick = onNavigateToAddShift,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.Primary,
                                contentColor = DarkText.OnPrimary
                            ),
                            shape = Shapes.Full
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.new_shift))
                        }
                    }
                }
                
                // Content
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        itemsIndexed(
                            items = uiState.shifts,
                            key = { _, shift -> shift.id }
                        ) { index, shift ->
                            // Staggered animation
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    )
                                ) + slideInVertically(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    ),
                                    initialOffsetY = { it / 4 }
                                )
                            ) {
                                PremiumShiftCard(
                                    shift = shift,
                                    onEditClick = { onNavigateToEditShift(shift.id) },
                                    onDeleteClick = { viewModel.deleteShift(shift.id) }
                                )
                            }
                        }
                        
                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(Spacing.massive))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Premium Shift Card with glass effect
 */
@Composable
private fun PremiumShiftCard(
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
    
    // Determine accent color based on earnings
    val accentColor = when {
        shift.netIncome >= 100 -> SemanticColors.Success
        shift.netIncome >= 50 -> SemanticColors.Warning
        else -> BrandColors.Primary
    }
    
    GlassCard(
        backgroundColor = DarkSurfaces.SurfaceContainer,
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
                    color = DarkText.Primary
                )
                Text(
                    text = shift.formattedWorkTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkText.Secondary
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
                        tint = DarkText.Secondary,
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
            color = DarkBorders.Subtle,
            thickness = Dimensions.borderHairline
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Stats Grid - 2x2
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
                valueColor = DarkText.Primary
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
                valueColor = DarkText.Primary
            )
            CardStat(
                value = "%.2f€".format(shift.incomePerHour),
                label = stringResource(R.string.stats_per_hour),
                valueColor = BrandColors.Primary
            )
        }
    }
}

/**
 * Stat item for inside cards
 */
@Composable
private fun CardStat(
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color = DarkText.Primary
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
            color = DarkText.Secondary
        )
    }
}
