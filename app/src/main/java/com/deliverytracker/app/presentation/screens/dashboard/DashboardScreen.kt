package com.deliverytracker.app.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.screens.dashboard.components.*
import com.deliverytracker.app.presentation.theme.*

/**
 * 🏠 Dashboard Screen - Premium Redesign 2026
 * 
 * Features:
 * - Gradient hero section με animated counter
 * - Glass morphism stat cards
 * - Progress ring για goals
 * - Shimmer loading states
 * 
 * Τα UI components εξάχθηκαν στο components/DashboardComponents.kt
 * 
 * @author DeliveryTracker Team
 * @version 2.1.0
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // ════════════════════════════════════════════════════════════
        // HERO SECTION — Gradient background με animated counter
        // ════════════════════════════════════════════════════════════
        PremiumHeroSection(
            amount = uiState.todayNetIncome,
            label = stringResource(R.string.hero_today_earnings),
            progress = uiState.dailyProgress,
            goal = uiState.dailyGoal
        )
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ════════════════════════════════════════════════════════════
        // 🆕 NEW ORDER BUTTON — Big and prominent
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
        // TODAY'S STATS — Glass cards
        // ════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        ) {
            // Section header
            Text(
                text = stringResource(R.string.dashboard_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    emoji = Emojis.ORDERS,
                    modifier = Modifier.weight(1f)
                )
                
                // Hours card
                GlassStatCard(
                    value = uiState.todayHours,
                    label = stringResource(R.string.dashboard_hours),
                    emoji = Emojis.TIME,
                    isDecimal = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        // ════════════════════════════════════════════════════════════
        // MONTHLY SUMMARY — Card with progress ring
        // ════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        ) {
            Text(
                text = stringResource(R.string.dashboard_this_month),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
