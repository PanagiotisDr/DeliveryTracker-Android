package com.deliverytracker.app.presentation.screens.commandcenter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.BigActionButton
import com.deliverytracker.app.presentation.components.cards.CompactShiftCard
import com.deliverytracker.app.presentation.components.cards.SmartSuggestionCard
import com.deliverytracker.app.presentation.components.cards.SwipeableShiftCard
import com.deliverytracker.app.presentation.components.widgets.GlanceableWidget
import com.deliverytracker.app.presentation.components.widgets.HeroMetricWidget
import com.deliverytracker.app.presentation.components.widgets.QuickActionWidget
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat

/**
 * 🎮 Command Center Screen - Main Dashboard
 * 
 * Όλα τα σημαντικά data σε μία ματιά.
 * Bottom sheet για λεπτομέρειες.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterScreen(
    onNavigateToAddShift: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToShiftDetail: (String) -> Unit,
    viewModel: CommandCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showSheet by remember { mutableStateOf(false) }
    var currentSheet by remember { mutableStateOf(SheetType.SHIFTS) }
    
    Scaffold(
        containerColor = CC_Background,
        floatingActionButton = {
            // Glowing FAB
            FloatingActionButton(
                onClick = onNavigateToAddShift,
                containerColor = CC_Primary,
                contentColor = CC_OnPrimary,
                shape = RoundedCornerShape(Spacing.radiusLg)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.btn_add)
                )
            }
        }
    ) { paddingValues ->
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // ============ HEADER ============
            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Greeting
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CC_TextSecondary
                )
                Text(
                    text = uiState.userName.ifEmpty { "Διανομέας" },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = CC_TextPrimary
                )
            }
            
            // ============ HERO METRIC ============
            item {
                HeroMetricWidget(
                    value = uiState.todayEarnings,
                    label = stringResource(R.string.stats_today),
                    progress = (uiState.todayEarnings / uiState.dailyGoal).toFloat(),
                    goalValue = uiState.dailyGoal
                )
            }
            
            // ============ 🆕 NEW ORDER BUTTON ============
            item {
                BigActionButton(
                    title = stringResource(R.string.btn_new_order),
                    subtitle = stringResource(R.string.btn_new_order_subtitle),
                    onClick = onNavigateToAddShift
                )
            }
            
            // ============ WIDGET GRID ============
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // €/hour widget
                    GlanceableWidget(
                        value = "${decimalFormat.format(uiState.avgPerHour)}€",
                        label = stringResource(R.string.stats_per_hour),
                        emoji = "⏱️",
                        valueColor = CC_Primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Weekly trend widget
                    GlanceableWidget(
                        value = "${if (uiState.weeklyTrend >= 0) "+" else ""}${uiState.weeklyTrend.toInt()}%",
                        label = stringResource(R.string.stats_trend),
                        emoji = if (uiState.weeklyTrend >= 0) "📈" else "📉",
                        valueColor = if (uiState.weeklyTrend >= 0) CC_Success else CC_Error,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Quick action widget
                    QuickActionWidget(
                        label = stringResource(R.string.nav_expenses),
                        emoji = "💳",
                        onClick = onNavigateToAddExpense,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // ============ SMART SUGGESTION ============
            item {
                if (uiState.smartSuggestion != null) {
                    SmartSuggestionCard(
                        emoji = uiState.smartSuggestion!!.emoji,
                        title = uiState.smartSuggestion!!.title,
                        subtitle = uiState.smartSuggestion!!.subtitle,
                        onClick = { uiState.smartSuggestion!!.action() }
                    )
                }
            }
            
            // ============ RECENT SHIFTS HEADER ============
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_shifts),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CC_TextPrimary
                    )
                    TextButton(onClick = {
                        currentSheet = SheetType.SHIFTS
                        showSheet = true
                    }) {
                        Text(
                            text = stringResource(R.string.btn_see_all),
                            color = CC_Primary
                        )
                    }
                }
            }
            
            // ============ HORIZONTAL SHIFTS LIST ============
            item {
                if (uiState.recentShifts.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                color = CC_WidgetBg,
                                shape = RoundedCornerShape(Spacing.widgetRadius)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📭",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = stringResource(R.string.empty_shifts),
                                style = MaterialTheme.typography.bodyMedium,
                                color = CC_TextSecondary
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(uiState.recentShifts.take(5)) { shift ->
                            CompactShiftCard(
                                shift = shift,
                                onClick = { onNavigateToShiftDetail(shift.id) }
                            )
                        }
                    }
                }
            }
            
            // ============ TODAY'S SHIFTS ============
            item {
                Text(
                    text = stringResource(R.string.stats_today),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CC_TextPrimary,
                    modifier = Modifier.padding(top = Spacing.md)
                )
            }
            
            // Today's shift cards
            items(uiState.todayShifts) { shift ->
                SwipeableShiftCard(
                    shift = shift,
                    onEdit = { onNavigateToShiftDetail(shift.id) },
                    onDelete = { viewModel.deleteShift(shift.id) }
                )
            }
            
            // Empty state for today
            if (uiState.todayShifts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xxl),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "☀️",
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = stringResource(R.string.no_shifts_today),
                                style = MaterialTheme.typography.bodyLarge,
                                color = CC_TextSecondary
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            FilledTonalButton(
                                onClick = onNavigateToAddShift,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = CC_Primary.copy(alpha = 0.15f),
                                    contentColor = CC_Primary
                                )
                            ) {
                                Text(stringResource(R.string.btn_start_shift))
                            }
                        }
                    }
                }
            }
            
            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(Spacing.huge))
            }
        }
        
        // ============ BOTTOM SHEET ============
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = CC_SheetBackground,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = Spacing.md)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = CC_SheetHandle,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                when (currentSheet) {
                    SheetType.SHIFTS -> ShiftListSheetContent(
                        shifts = uiState.allShifts,
                        onShiftClick = { shift ->
                            showSheet = false
                            onNavigateToShiftDetail(shift.id)
                        },
                        onDeleteShift = { viewModel.deleteShift(it) }
                    )
                    SheetType.EXPENSES -> ExpenseListSheetContent(
                        onDismiss = { showSheet = false }
                    )
                    SheetType.STATISTICS -> StatisticsSheetContent(
                        onDismiss = { showSheet = false }
                    )
                }
            }
        }
    }
}

// Sheet types enum
enum class SheetType {
    SHIFTS, EXPENSES, STATISTICS
}

// Helper: Get greeting based on time
@Composable
private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> stringResource(R.string.greeting_morning)
        hour < 18 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_evening)
    }
}

// ============ SHEET CONTENT COMPOSABLES ============

@Composable
private fun ShiftListSheetContent(
    shifts: List<com.deliverytracker.app.domain.model.Shift>,
    onShiftClick: (com.deliverytracker.app.domain.model.Shift) -> Unit,
    onDeleteShift: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item {
            Text(
                text = stringResource(R.string.nav_shifts),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CC_TextPrimary,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
        }
        
        items(shifts) { shift ->
            SwipeableShiftCard(
                shift = shift,
                onEdit = { onShiftClick(shift) },
                onDelete = { onDeleteShift(shift.id) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun ExpenseListSheetContent(
    onDismiss: () -> Unit
) {
    // Placeholder - θα υλοποιηθεί αργότερα
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Expenses Sheet - Coming Soon",
            color = CC_TextSecondary
        )
    }
}

@Composable
private fun StatisticsSheetContent(
    onDismiss: () -> Unit
) {
    // Placeholder - θα υλοποιηθεί αργότερα
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Statistics Sheet - Coming Soon",
            color = CC_TextSecondary
        )
    }
}
