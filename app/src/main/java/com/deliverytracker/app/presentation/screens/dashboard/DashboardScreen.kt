package com.deliverytracker.app.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

/**
 * 🏠 Dashboard Screen - Premium Redesign 2026
 * 
 * Features:
 * - Gradient hero section with animated counter
 * - Glass morphism stat cards
 * - Progress ring for goals
 * - Shimmer loading states
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 */
@Composable
fun DashboardScreen(
    onNavigateToShifts: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToNewOrder: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurfaces.Background)
            .verticalScroll(scrollState)
    ) {
        // ════════════════════════════════════════════════════════════
        // HERO SECTION - Gradient background με animated counter
        // ════════════════════════════════════════════════════════════
        PremiumHeroSection(
            amount = uiState.todayNetIncome,
            label = stringResource(R.string.hero_today_earnings),
            progress = uiState.dailyProgress,
            goal = uiState.dailyGoal
        )
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ════════════════════════════════════════════════════════════
        // 🆕 NEW ORDER BUTTON - Big and prominent
        // ════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        ) {
            BigActionButton(
                title = stringResource(R.string.btn_new_order),
                subtitle = stringResource(R.string.btn_new_order_subtitle),
                onClick = onNavigateToNewOrder
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ════════════════════════════════════════════════════════════
        // TODAY'S STATS - Glass cards
        // ════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        ) {
            // Section header
            Text(
                text = stringResource(R.string.dashboard_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkText.Secondary
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Orders card
                GlassStatCard(
                    value = uiState.todayOrders,
                    label = stringResource(R.string.dashboard_orders),
                    emoji = "📦",
                    modifier = Modifier.weight(1f)
                )
                
                // Hours card
                GlassStatCard(
                    value = uiState.todayHours,
                    label = stringResource(R.string.dashboard_hours),
                    emoji = "⏱️",
                    isDecimal = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ════════════════════════════════════════════════════════════
        // MONTHLY SUMMARY - Card with progress ring
        // ════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        ) {
            Text(
                text = stringResource(R.string.dashboard_this_month),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkText.Secondary
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            MonthSummaryCard(
                netIncome = uiState.monthNetIncome,
                orders = uiState.monthOrders,
                shifts = uiState.monthShifts,
                goal = uiState.monthlyGoal,
                progress = uiState.monthlyProgress
            )
        }
        
        // Bottom padding for nav bar
        Spacer(modifier = Modifier.height(Spacing.massive))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PREMIUM HERO SECTION
// ════════════════════════════════════════════════════════════════════════════

/**
 * Hero section with gradient background and animated counter
 */
@Composable
private fun PremiumHeroSection(
    amount: Double,
    label: String,
    progress: Float,
    goal: Double?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = Gradients.EarningsVibrant
                )
            )
            .padding(
                horizontal = Spacing.lg,
                vertical = Spacing.xxl
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Animated amount - THE BIG NUMBER
            AnimatedCurrency(
                targetValue = amount,
                valueStyle = CustomTextStyles.HeroNumber.copy(
                    color = Color.White
                ),
                symbolStyle = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White
            )
            
            // Goal progress (if set)
            if (goal != null && goal > 0) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(Dimensions.progressHeightDefault)
                        .background(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = Shapes.Full
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(
                                color = Color.White,
                                shape = Shapes.Full
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                // Progress text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedPercentage(
                        targetValue = progress,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = " ${stringResource(R.string.dashboard_daily_goal).lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// GLASS STAT CARD
// ════════════════════════════════════════════════════════════════════════════

/**
 * Glass morphism stat card with animated value
 */
@Composable
private fun GlassStatCard(
    value: Number,
    label: String,
    emoji: String,
    modifier: Modifier = Modifier,
    isDecimal: Boolean = false
) {
    GlassCard(
        modifier = modifier,
        backgroundColor = DarkSurfaces.SurfaceContainer.copy(alpha = 0.8f),
        borderColor = DarkBorders.Glass,
        glowEnabled = false,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Animated value
            if (isDecimal) {
                AnimatedCounter(
                    targetValue = value.toDouble(),
                    style = CustomTextStyles.MediumNumber,
                    color = DarkText.Primary,
                    decimalPlaces = 1
                )
            } else {
                AnimatedIntCounter(
                    targetValue = value.toInt(),
                    style = CustomTextStyles.MediumNumber,
                    color = DarkText.Primary
                )
            }
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = DarkText.Secondary
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MONTH SUMMARY CARD
// ════════════════════════════════════════════════════════════════════════════

/**
 * Monthly summary with progress ring
 */
@Composable
private fun MonthSummaryCard(
    netIncome: Double,
    orders: Int,
    shifts: Int,
    goal: Double?,
    progress: Float
) {
    GlassCard(
        backgroundColor = DarkSurfaces.SurfaceContainer.copy(alpha = 0.9f),
        borderColor = DarkBorders.Glass,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        // Top row: Net income + Progress ring
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Net income
            Column {
                Text(
                    text = stringResource(R.string.dashboard_net),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkText.Secondary
                )
                
                AnimatedCurrency(
                    targetValue = netIncome,
                    valueStyle = CustomTextStyles.LargeNumber.copy(
                        color = if (netIncome >= 0) SemanticColors.Success 
                               else SemanticColors.Error
                    ),
                    symbolStyle = MaterialTheme.typography.titleMedium.copy(
                        color = if (netIncome >= 0) SemanticColors.Success.copy(alpha = 0.8f)
                               else SemanticColors.Error.copy(alpha = 0.8f)
                    )
                )
            }
            
            // Progress ring (if goal set)
            if (goal != null && goal > 0) {
                ProgressRingWithLabel(
                    progress = progress,
                    size = Dimensions.progressCircularMd,
                    strokeWidth = Dimensions.progressHeightDefault
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        HorizontalDivider(
            color = DarkBorders.Subtle,
            thickness = Dimensions.borderHairline
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        // Bottom row: Orders & Shifts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniStat(
                value = orders,
                label = stringResource(R.string.dashboard_orders)
            )
            
            MiniStat(
                value = shifts,
                label = stringResource(R.string.dashboard_shifts)
            )
        }
    }
}

/**
 * Mini stat for inside cards
 */
@Composable
private fun MiniStat(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedIntCounter(
            targetValue = value,
            style = CustomTextStyles.SmallNumber,
            color = DarkText.Primary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkText.Secondary
        )
    }
}
