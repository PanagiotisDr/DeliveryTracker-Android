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
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Glowing FAB
            FloatingActionButton(
                onClick = onNavigateToAddShift,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.userName.ifEmpty { stringResource(R.string.placeholder_rider) },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
                        emoji = Emojis.TIME,
                        valueColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Weekly trend widget
                    GlanceableWidget(
                        value = "${if (uiState.weeklyTrend >= 0) "+" else ""}${uiState.weeklyTrend.toInt()}%",
                        label = stringResource(R.string.stats_trend),
                        emoji = if (uiState.weeklyTrend >= 0) AppEmojis.TREND_UP else AppEmojis.TREND_DOWN,
                        valueColor = if (uiState.weeklyTrend >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Quick action widget
                    QuickActionWidget(
                        label = stringResource(R.string.nav_expenses),
                        emoji = AppEmojis.CREDIT_CARD,
                        onClick = onNavigateToAddExpense,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // ============ SMART SUGGESTION ============
            item {
                uiState.smartSuggestion?.let { suggestion ->
                    // Χρήση stringResource με formatArgs για localized strings
                    val subtitle = if (suggestion.formatArgs.isNotEmpty()) {
                        stringResource(suggestion.subtitleResId, *suggestion.formatArgs.toTypedArray())
                    } else {
                        stringResource(suggestion.subtitleResId)
                    }
                    SmartSuggestionCard(
                        emoji = suggestion.emoji,
                        title = stringResource(suggestion.titleResId),
                        subtitle = subtitle,
                        onClick = { suggestion.action() }
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        currentSheet = SheetType.SHIFTS
                        showSheet = true
                    }) {
                        Text(
                            text = stringResource(R.string.btn_see_all),
                            color = MaterialTheme.colorScheme.primary
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
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(Spacing.widgetRadius)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = AppEmojis.EMPTY_MAILBOX,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = stringResource(R.string.empty_shifts),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.onSurface,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            FilledTonalButton(
                                onClick = onNavigateToAddShift,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    contentColor = MaterialTheme.colorScheme.primary
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
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = Spacing.md)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outline,
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
                        expenses = uiState.allExpenses,
                        onDismiss = { showSheet = false }
                    )
                    SheetType.STATISTICS -> StatisticsSheetContent(
                        shifts = uiState.allShifts,
                        expenses = uiState.allExpenses,
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
                color = MaterialTheme.colorScheme.onSurface,
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
    expenses: List<com.deliverytracker.app.domain.model.Expense>,
    onDismiss: () -> Unit
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item {
            Text(
                text = stringResource(R.string.sheet_expenses_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
        }
        
        if (expenses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.sheet_no_expenses),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Εμφάνιση τελευταίων 20 εξόδων
            items(expenses.take(20)) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(Spacing.radiusMd)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji κατηγορίας
                        Box(
                            modifier = Modifier
                                .size(Dimensions.iconHuge)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(Spacing.radiusSm)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = expense.category.emoji,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(Spacing.md))
                        
                        // Πληροφορίες εξόδου
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(expense.category.displayNameResId),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dateFormat.format(java.util.Date(expense.date)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Ποσό (κόκκινο = έξοδο)
                        Text(
                            text = "-${decimalFormat.format(expense.amount)}€",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SemanticColors.Error
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun StatisticsSheetContent(
    shifts: List<com.deliverytracker.app.domain.model.Shift>,
    expenses: List<com.deliverytracker.app.domain.model.Expense>,
    onDismiss: () -> Unit
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    // Υπολογισμός στατιστικών
    val totalShifts = shifts.size
    val totalOrders = shifts.sumOf { it.ordersCount }
    val totalHours = shifts.sumOf { it.hoursWorked }
    val grossIncome = shifts.sumOf { it.netIncome }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = grossIncome - totalExpenses
    val avgPerHour = if (totalHours > 0) grossIncome / totalHours else 0.0
    val avgPerOrder = if (totalOrders > 0) grossIncome / totalOrders else 0.0
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item {
            Text(
                text = stringResource(R.string.sheet_statistics_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
        }
        
        if (shifts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.sheet_no_shifts),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Κύρια στατιστικά σε grid 2x2
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    StatCard(
                        label = stringResource(R.string.sheet_total_shifts),
                        value = totalShifts.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.sheet_total_orders),
                        value = totalOrders.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    StatCard(
                        label = stringResource(R.string.sheet_total_hours),
                        value = "${decimalFormat.format(totalHours)}h",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.sheet_avg_per_hour),
                        value = "${decimalFormat.format(avgPerHour)}€",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Οικονομική σύνοψη
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    shape = RoundedCornerShape(Spacing.radiusLg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        // Ακαθάριστα έσοδα
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dashboard_gross_income),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${decimalFormat.format(grossIncome)}€",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SemanticColors.Success
                            )
                        }
                        
                        // Σύνολο εξόδων
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dashboard_total_expenses),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "-${decimalFormat.format(totalExpenses)}€",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SemanticColors.Error
                            )
                        }
                        
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        
                        // Καθαρό κέρδος
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dashboard_net_profit),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${decimalFormat.format(netProfit)}€",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (netProfit >= 0) SemanticColors.Success else SemanticColors.Error
                            )
                        }
                        
                        // Μέσος όρος ανά παραγγελία
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.sheet_avg_per_order),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${decimalFormat.format(avgPerOrder)}€",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

/**
 * Μικρό card για ένα στατιστικό στοιχείο.
 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(Spacing.radiusMd)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
