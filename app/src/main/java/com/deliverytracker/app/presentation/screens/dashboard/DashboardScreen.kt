package com.deliverytracker.app.presentation.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat

/**
 * 🏠 Dashboard Screen - Google Pay Style
 * Clean white, big hero number, rounded cards με soft shadows.
 */
@Composable
fun DashboardScreen(
    onNavigateToShifts: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // ============ HERO SECTION - Big Number ============
        GPay_HeroCard(
            amount = uiState.todayNetIncome,
            label = stringResource(R.string.hero_today_earnings),
            progress = uiState.dailyProgress,
            goal = uiState.dailyGoal,
            decimalFormat = decimalFormat
        )
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ============ ΣΗΜΕΡΙΝΑ STATS ============
        Column(
            modifier = Modifier.padding(horizontal = Spacing.lg)
        ) {
            Text(
                text = stringResource(R.string.dashboard_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                GPay_StatCard(
                    value = uiState.todayOrders.toString(),
                    label = stringResource(R.string.dashboard_orders),
                    emoji = "📦",
                    modifier = Modifier.weight(1f)
                )
                GPay_StatCard(
                    value = String.format("%.1f", uiState.todayHours),
                    label = stringResource(R.string.dashboard_hours),
                    emoji = "⏱️",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ============ ΜΗΝΙΑΙΑ STATS ============
        Column(
            modifier = Modifier.padding(horizontal = Spacing.lg)
        ) {
            Text(
                text = stringResource(R.string.dashboard_this_month),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            GPay_MonthCard(
                netIncome = uiState.monthNetIncome,
                orders = uiState.monthOrders,
                shifts = uiState.monthShifts,
                goal = uiState.monthlyGoal,
                progress = uiState.monthlyProgress,
                decimalFormat = decimalFormat
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}

/**
 * 🎯 Hero Card - Big center number με progress ring
 */
@Composable
private fun GPay_HeroCard(
    amount: Double,
    label: String,
    progress: Float,
    goal: Double?,
    decimalFormat: DecimalFormat
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = Spacing.xxl, horizontal = Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Big Amount
            Text(
                text = "${decimalFormat.format(amount)}€",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            
            // Goal Progress (if exists)
            if (goal != null && goal > 0) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = "${(progress * 100).toInt()}% του στόχου (${decimalFormat.format(goal)}€)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 📊 Simple Stat Card - Clean white με soft shadow
 */
@Composable
private fun GPay_StatCard(
    value: String,
    label: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(Spacing.radiusLg),
                ambientColor = GPay_ShadowColor,
                spotColor = GPay_ShadowColor
            ),
        shape = RoundedCornerShape(Spacing.radiusLg),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Value
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 📅 Month Summary Card
 */
@Composable
private fun GPay_MonthCard(
    netIncome: Double,
    orders: Int,
    shifts: Int,
    goal: Double?,
    progress: Float,
    decimalFormat: DecimalFormat
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(Spacing.radiusLg),
                ambientColor = GPay_ShadowColor,
                spotColor = GPay_ShadowColor
            ),
        shape = RoundedCornerShape(Spacing.radiusLg),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg)
        ) {
            // Κύρια τιμή
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.dashboard_net),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${decimalFormat.format(netIncome)}€",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (netIncome >= 0) GPay_Success 
                               else MaterialTheme.colorScheme.error
                    )
                }
                
                // Circular progress if goal
                if (goal != null && goal > 0) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = GPay_SuccessLight,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = GPay_Success
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Secondary stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GPay_MiniStat(
                    value = orders.toString(),
                    label = stringResource(R.string.dashboard_orders)
                )
                GPay_MiniStat(
                    value = shifts.toString(),
                    label = stringResource(R.string.dashboard_shifts)
                )
            }
        }
    }
}

/**
 * Mini stat για μέσα σε cards
 */
@Composable
private fun GPay_MiniStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
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
