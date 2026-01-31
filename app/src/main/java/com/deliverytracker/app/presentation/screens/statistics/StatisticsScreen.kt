package com.deliverytracker.app.presentation.screens.statistics

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Οθόνη Στατιστικών.
 * Χρησιμοποιεί stringResource για localization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_statistics)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Filter Chips
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatsPeriod.entries.forEachIndexed { index, period ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = StatsPeriod.entries.size
                            ),
                            onClick = { viewModel.selectPeriod(period) },
                            selected = uiState.selectedPeriod == period
                        ) {
                            Text(period.label)
                        }
                    }
                }
                
                // ============ ΕΣΟΔΑ ============
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.stats_income),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        StatRow(stringResource(R.string.stats_gross), "${decimalFormat.format(uiState.grossIncome)}€")
                        StatRow(stringResource(R.string.stats_tips), "${decimalFormat.format(uiState.tips)}€")
                        StatRow(stringResource(R.string.stats_bonus), "${decimalFormat.format(uiState.bonus)}€")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatRow(
                            label = stringResource(R.string.stats_net), 
                            value = "${decimalFormat.format(uiState.netIncome)}€",
                            isHighlighted = true
                        )
                    }
                }
                
                // ============ ΕΞΟΔΑ ============
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.stats_expenses),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (uiState.expensesByCategory.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_expenses_data),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            uiState.expensesByCategory.forEach { (category, amount) ->
                                StatRow(category, "-${decimalFormat.format(amount)}€")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            StatRow(
                                label = stringResource(R.string.stats_total_expenses),
                                value = "-${decimalFormat.format(uiState.totalExpenses)}€",
                                isHighlighted = true,
                                isNegative = true
                            )
                        }
                    }
                }
                
                // ============ ΚΑΘΑΡΟ ΚΕΡΔΟΣ ============
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.netIncome - uiState.totalExpenses > 0)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.stats_net_profit),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${decimalFormat.format(uiState.netIncome - uiState.totalExpenses)}€",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // ============ ΑΡΙΘΜΟΙ ============
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.stats_numbers),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NumberCard("📋", uiState.totalShifts.toString(), stringResource(R.string.stats_shifts))
                            NumberCard("📦", uiState.totalOrders.toString(), stringResource(R.string.stats_orders))
                            NumberCard("⏱", String.format("%.1f", uiState.totalHours), stringResource(R.string.stats_hours))
                            NumberCard("🛣️", String.format("%.0f", uiState.totalKilometers), stringResource(R.string.stats_km))
                        }
                    }
                }
                
                // ============ ΜΕΣΟΙ ΟΡΟΙ ============
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.stats_averages),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        StatRow(stringResource(R.string.stats_per_hour), "${decimalFormat.format(uiState.avgIncomePerHour)}€")
                        StatRow(stringResource(R.string.stats_per_order), "${decimalFormat.format(uiState.avgIncomePerOrder)}€")
                        StatRow(stringResource(R.string.stats_per_km), "${decimalFormat.format(uiState.avgIncomePerKm)}€")
                        StatRow(stringResource(R.string.stats_orders_per_shift), String.format("%.1f", uiState.avgOrdersPerShift))
                    }
                }
                
                // ============ BEST DAY ============
                if (uiState.bestDayDate != null && uiState.bestDayIncome > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.stats_best_day),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateFormat.format(Date(uiState.bestDayDate!!)),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${decimalFormat.format(uiState.bestDayIncome)}€",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Row με label και value.
 */
@Composable
private fun StatRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false,
    isNegative: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Card με αριθμό.
 */
@Composable
private fun NumberCard(
    emoji: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, style = MaterialTheme.typography.titleLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
