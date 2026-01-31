package com.deliverytracker.app.presentation.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import java.text.DecimalFormat

/**
 * Dashboard screen - Η κύρια οθόνη της εφαρμογής.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_dashboard)) },
                actions = {
                    IconButton(onClick = viewModel::logout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.auth_logout)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToShifts,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Νέα Βάρδια")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Welcome Message
            uiState.username?.let { username ->
                Text(
                    text = "Καλωσήρθες, $username! 👋",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // ============ ΣΗΜΕΡΙΝΑ ΣΤΑΤΙΣΤΙΚΑ ============
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📅 Σήμερα",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            emoji = "💰",
                            value = "${decimalFormat.format(uiState.todayNetIncome)}€",
                            label = "Καθαρά"
                        )
                        StatItem(
                            emoji = "📦",
                            value = uiState.todayOrders.toString(),
                            label = "Παραγγελίες"
                        )
                        StatItem(
                            emoji = "⏱",
                            value = String.format("%.1fω", uiState.todayHours),
                            label = "Ώρες"
                        )
                        if (uiState.todayBonus > 0) {
                            StatItem(
                                emoji = "⭐",
                                value = "${decimalFormat.format(uiState.todayBonus)}€",
                                label = "Bonus"
                            )
                        }
                    }
                    
                    // Daily Goal Progress
                    if (uiState.dailyGoal != null && uiState.dailyGoal!! > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        GoalProgressBar(
                            progress = uiState.dailyProgress,
                            current = uiState.todayNetIncome,
                            goal = uiState.dailyGoal!!,
                            label = "Ημερήσιος Στόχος"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ============ ΜΗΝΙΑΙΑ ΣΤΑΤΙΣΤΙΚΑ ============
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Αυτόν τον μήνα",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            emoji = "💵",
                            value = "${decimalFormat.format(uiState.monthNetIncome)}€",
                            label = "Καθαρά"
                        )
                        StatItem(
                            emoji = "📦",
                            value = uiState.monthOrders.toString(),
                            label = "Παραγγελίες"
                        )
                        StatItem(
                            emoji = "📋",
                            value = uiState.monthShifts.toString(),
                            label = "Βάρδιες"
                        )
                    }
                    
                    // Monthly Goal Progress
                    if (uiState.monthlyGoal != null && uiState.monthlyGoal!! > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        GoalProgressBar(
                            progress = uiState.monthlyProgress,
                            current = uiState.monthNetIncome,
                            goal = uiState.monthlyGoal!!,
                            label = "Μηνιαίος Στόχος"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.nav_shifts),
                    onClick = onNavigateToShifts,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.Receipt,
                    title = stringResource(R.string.nav_expenses),
                    onClick = onNavigateToExpenses,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.BarChart,
                    title = stringResource(R.string.nav_statistics),
                    onClick = onNavigateToStatistics,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.nav_settings),
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Στατιστικό στοιχείο.
 */
@Composable
private fun StatItem(
    emoji: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
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

/**
 * Progress bar για στόχους.
 */
@Composable
private fun GoalProgressBar(
    progress: Float,
    current: Double,
    goal: Double,
    label: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "progress"
    )
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${decimalFormat.format(current)} / ${decimalFormat.format(goal)}€ (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (progress >= 1f) MaterialTheme.colorScheme.tertiary 
                    else MaterialTheme.colorScheme.primary
        )
        if (progress >= 1f) {
            Text(
                text = "🎉 Μπράβο! Πέτυχες τον στόχο!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

/**
 * Quick action card component.
 */
@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
