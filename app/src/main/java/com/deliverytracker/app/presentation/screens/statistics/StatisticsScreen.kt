package com.deliverytracker.app.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat
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
 * @author DeliveryTracker Team
 * @version 2.0.0
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
        containerColor = DarkSurfaces.Background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = BrandColors.Primary,
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
                    emoji = "💰"
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
                        color = DarkBorders.Subtle
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
                    emoji = "📊"
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
                    emoji = "📈"
                ) {
                    StatRow(
                        label = stringResource(R.string.stats_per_hour),
                        value = uiState.avgIncomePerHour,
                        valueColor = BrandColors.Primary
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
                if (uiState.bestDayDate != null) {
                    BestDayCard(
                        income = uiState.bestDayIncome,
                        date = dateFormat.format(Date(uiState.bestDayDate!!))
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PRIVATE COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Hero card for net profit
 */
@Composable
private fun ProfitHeroCard(
    profit: Double,
    isPositive: Boolean
) {
    val gradientColors = if (isPositive) {
        listOf(
            SemanticColors.Success.copy(alpha = 0.15f),
            SemanticColors.Success.copy(alpha = 0.05f)
        )
    } else {
        listOf(
            SemanticColors.Error.copy(alpha = 0.15f),
            SemanticColors.Error.copy(alpha = 0.05f)
        )
    }
    
    GlassCard(
        backgroundColor = DarkSurfaces.SurfaceContainer,
        borderColor = if (isPositive) SemanticColors.Success.copy(alpha = 0.3f)
                     else SemanticColors.Error.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.xl)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    shape = Shapes.Medium
                )
                .padding(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isPositive) "📈" else "📉",
                    style = MaterialTheme.typography.displaySmall
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = stringResource(R.string.stats_net_profit).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkText.Secondary,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                AnimatedCurrency(
                    targetValue = profit,
                    valueStyle = CustomTextStyles.HeroNumber.copy(
                        color = if (isPositive) SemanticColors.Success 
                               else SemanticColors.Error
                    ),
                    symbolStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = if (isPositive) SemanticColors.Success.copy(alpha = 0.8f)
                               else SemanticColors.Error.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

/**
 * Summary card with glass effect
 */
@Composable
private fun StatsSummaryCard(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        backgroundColor = DarkSurfaces.SurfaceContainer,
        borderColor = DarkBorders.Glass,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Text(
            text = "$emoji $title",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DarkText.Primary
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        content()
    }
}

/**
 * Stat row with animated value
 */
@Composable
private fun StatRow(
    label: String,
    value: Double,
    valueColor: Color = DarkText.Primary,
    isBold: Boolean = false,
    isCurrency: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText.Secondary
        )
        
        if (isCurrency) {
            AnimatedCurrency(
                targetValue = value,
                valueStyle = (if (isBold) CustomTextStyles.SmallNumber 
                             else MaterialTheme.typography.bodyMedium).copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    color = valueColor
                ),
                symbolStyle = MaterialTheme.typography.bodySmall.copy(
                    color = valueColor.copy(alpha = 0.8f)
                )
            )
        } else {
            AnimatedCounter(
                targetValue = value,
                style = if (isBold) CustomTextStyles.SmallNumber 
                       else MaterialTheme.typography.bodyMedium,
                color = valueColor,
                decimalPlaces = 1
            )
        }
    }
}

/**
 * Number stat for grid display
 */
@Composable
private fun NumberStat(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        AnimatedIntCounter(
            targetValue = value,
            style = CustomTextStyles.MediumNumber,
            color = DarkText.Primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkText.Secondary
        )
    }
}

/**
 * Decimal number stat for grid display
 */
@Composable
private fun NumberStatDecimal(
    value: Double,
    label: String,
    suffix: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedCounter(
                targetValue = value,
                style = CustomTextStyles.MediumNumber,
                color = DarkText.Primary,
                decimalPlaces = if (suffix == "h") 1 else 0
            )
            if (suffix.isNotEmpty()) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkText.Secondary
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkText.Secondary
        )
    }
}

/**
 * Best day highlight card
 */
@Composable
private fun BestDayCard(
    income: Double,
    date: String
) {
    GlassCard(
        backgroundColor = DarkSurfaces.SurfaceContainer,
        borderColor = SemanticColors.Warning.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SemanticColors.Warning.copy(alpha = 0.15f),
                            SemanticColors.Warning.copy(alpha = 0.05f)
                        )
                    ),
                    shape = Shapes.Medium
                )
                .padding(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆",
                    style = MaterialTheme.typography.displaySmall
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = stringResource(R.string.stats_best_day).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SemanticColors.Warning,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                AnimatedCurrency(
                    targetValue = income,
                    valueStyle = CustomTextStyles.LargeNumber.copy(
                        color = SemanticColors.Warning
                    ),
                    symbolStyle = MaterialTheme.typography.titleMedium.copy(
                        color = SemanticColors.Warning.copy(alpha = 0.8f)
                    )
                )
                
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = SemanticColors.Warning.copy(alpha = 0.7f)
                )
            }
        }
    }
}
