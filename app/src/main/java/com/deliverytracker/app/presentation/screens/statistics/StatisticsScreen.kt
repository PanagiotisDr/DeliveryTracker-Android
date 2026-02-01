package com.deliverytracker.app.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📊 Statistics Screen - Google Pay Style
 * Clean cards με summary data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("el", "GR")) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.nav_statistics),
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                // ============ ΕΣΟΔΑ ============
                GPay_SummaryCard(
                    title = stringResource(R.string.stats_income),
                    emoji = "💰"
                ) {
                    GPay_StatRow(stringResource(R.string.stats_gross), "${decimalFormat.format(uiState.grossIncome)}€")
                    GPay_StatRow(stringResource(R.string.stats_tips), "${decimalFormat.format(uiState.tips)}€")
                    GPay_StatRow(stringResource(R.string.stats_bonus), "${decimalFormat.format(uiState.bonus)}€")
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    GPay_StatRow(
                        label = stringResource(R.string.stats_net),
                        value = "${decimalFormat.format(uiState.netIncome)}€",
                        valueColor = GPay_Success,
                        isBold = true
                    )
                }
                
                // ============ ΚΑΘΑΡΟ ΚΕΡΔΟΣ ============
                val profit = uiState.netIncome - uiState.totalExpenses
                val isProfit = profit >= 0
                
                GPay_HighlightCard(
                    title = stringResource(R.string.stats_net_profit),
                    value = "${decimalFormat.format(profit)}€",
                    emoji = if (isProfit) "📈" else "📉",
                    backgroundColor = if (isProfit) GPay_SuccessLight else GPay_ErrorLight,
                    valueColor = if (isProfit) GPay_Success else GPay_Error
                )
                
                // ============ ΑΡΙΘΜΟΙ ============
                GPay_SummaryCard(
                    title = stringResource(R.string.stats_numbers),
                    emoji = "📊"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GPay_NumberStat(uiState.totalShifts.toString(), stringResource(R.string.stats_shifts))
                        GPay_NumberStat(uiState.totalOrders.toString(), stringResource(R.string.stats_orders))
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GPay_NumberStat(String.format("%.1fh", uiState.totalHours), stringResource(R.string.stats_hours))
                        GPay_NumberStat(String.format("%.0f", uiState.totalKilometers), stringResource(R.string.stats_km))
                    }
                }
                
                // ============ ΜΕΣΟΙ ΟΡΟΙ ============
                GPay_SummaryCard(
                    title = stringResource(R.string.stats_averages),
                    emoji = "📈"
                ) {
                    GPay_StatRow(stringResource(R.string.stats_per_hour), "${decimalFormat.format(uiState.avgIncomePerHour)}€")
                    GPay_StatRow(stringResource(R.string.stats_per_order), "${decimalFormat.format(uiState.avgIncomePerOrder)}€")
                    GPay_StatRow(stringResource(R.string.stats_per_km), "${decimalFormat.format(uiState.avgIncomePerKm)}€")
                    GPay_StatRow(stringResource(R.string.stats_orders_per_shift), String.format("%.1f", uiState.avgOrdersPerShift))
                }
                
                // ============ ΚΑΛΥΤΕΡΗ ΜΕΡΑ ============
                if (uiState.bestDayDate != null) {
                    GPay_HighlightCard(
                        title = stringResource(R.string.stats_best_day),
                        value = "${decimalFormat.format(uiState.bestDayIncome)}€",
                        subtitle = dateFormat.format(Date(uiState.bestDayDate!!)),
                        emoji = "🏆",
                        backgroundColor = GPay_WarningLight,
                        valueColor = GPay_Warning
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

/**
 * Summary Card wrapper με τίτλο
 */
@Composable
private fun GPay_SummaryCard(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(Spacing.radiusLg),
                ambientColor = GPay_ShadowColor,
                spotColor = GPay_ShadowColor
            ),
        shape = RoundedCornerShape(Spacing.radiusLg),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            content()
        }
    }
}

/**
 * Highlight Card για σημαντικά νούμερα
 */
@Composable
private fun GPay_HighlightCard(
    title: String,
    value: String,
    emoji: String,
    backgroundColor: Color,
    valueColor: Color,
    subtitle: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(Spacing.radiusLg),
                ambientColor = GPay_ShadowColor,
                spotColor = GPay_ShadowColor
            ),
        shape = RoundedCornerShape(Spacing.radiusLg),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = valueColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Stat row με label και value
 */
@Composable
private fun GPay_StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium 
                   else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

/**
 * Number stat για grid display
 */
@Composable
private fun GPay_NumberStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
