package com.deliverytracker.app.presentation.screens.statistics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.screens.statistics.components.*
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📊 Statistics Screen - Premium Redesign 2026
 * 
 * Features:
 * - Glass morphism cards
 * - Animated counters
 * - Gradient highlights
 * - Premium typography
 * 
 * Τα UI components εξάχθηκαν στο components/StatisticsComponents.kt
 * 
 * @author DeliveryTracker Team
 * @version 2.1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("el", "GR")) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.nav_statistics),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = Dimensions.progressHeightDefault
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {

                
                // ════════════════════════════════════════════════════════════
                // PERIOD FILTER CHIPS
                // ════════════════════════════════════════════════════════════
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatsPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = uiState.selectedPeriod == period,
                            onClick = { viewModel.selectPeriod(period) },
                            label = { Text(stringResource(period.labelRes)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // ════════════════════════════════════════════════════════════
                // NET PROFIT HERO CARD
                // ════════════════════════════════════════════════════════════
                val profit = uiState.netIncome - uiState.totalExpenses
                val isProfit = profit >= 0
                
                ProfitHeroCard(
                    profit = profit,
                    isPositive = isProfit
                )
                
                // ════════════════════════════════════════════════════════════
                // INCOME BREAKDOWN
                // ════════════════════════════════════════════════════════════
                StatsSummaryCard(
                    title = stringResource(R.string.stats_income),
                    emoji = AppEmojis.MONEY_BAG
                ) {
                    StatRow(
                        label = stringResource(R.string.stats_gross),
                        value = uiState.grossIncome
                    )
                    StatRow(
                        label = stringResource(R.string.stats_tips),
                        value = uiState.tips
                    )
                    StatRow(
                        label = stringResource(R.string.stats_bonus),
                        value = uiState.bonus
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    StatRow(
                        label = stringResource(R.string.stats_net),
                        value = uiState.netIncome,
                        valueColor = SemanticColors.Success,
                        isBold = true
                    )
                }
                
                // ════════════════════════════════════════════════════════════
                // KEY NUMBERS
                // ════════════════════════════════════════════════════════════
                StatsSummaryCard(
                    title = stringResource(R.string.stats_numbers),
                    emoji = AppEmojis.CHART
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumberStat(
                            value = uiState.totalShifts,
                            label = stringResource(R.string.stats_shifts)
                        )
                        NumberStat(
                            value = uiState.totalOrders,
                            label = stringResource(R.string.stats_orders)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumberStatDecimal(
                            value = uiState.totalHours,
                            label = stringResource(R.string.stats_hours),
                            suffix = "h"
                        )
                        NumberStatDecimal(
                            value = uiState.totalKilometers,
                            label = stringResource(R.string.stats_km),
                            suffix = ""
                        )
                    }
                }
                
                // ════════════════════════════════════════════════════════════
                // AVERAGES
                // ════════════════════════════════════════════════════════════
                StatsSummaryCard(
                    title = stringResource(R.string.stats_averages),
                    emoji = AppEmojis.TREND_UP
                ) {
                    StatRow(
                        label = stringResource(R.string.stats_per_hour),
                        value = uiState.avgIncomePerHour,
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                    StatRow(
                        label = stringResource(R.string.stats_per_order),
                        value = uiState.avgIncomePerOrder
                    )
                    StatRow(
                        label = stringResource(R.string.stats_per_km),
                        value = uiState.avgIncomePerKm
                    )
                    StatRow(
                        label = stringResource(R.string.stats_orders_per_shift),
                        value = uiState.avgOrdersPerShift,
                        isCurrency = false
                    )
                }
                
                // ════════════════════════════════════════════════════════════
                // BEST DAY
                // ════════════════════════════════════════════════════════════
                uiState.bestDayDate?.let { bestDate ->
                    BestDayCard(
                        income = uiState.bestDayIncome,
                        date = dateFormat.format(Date(bestDate))
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}
