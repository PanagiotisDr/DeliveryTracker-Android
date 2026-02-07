package com.deliverytracker.app.presentation.screens.expenses

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 💰 Expense List Screen - Premium Redesign 2026
 * 
 * Features:
 * - Glass morphism cards with category color accents
 * - Shimmer loading
 * - Animated list items
 * - Category color indicators
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale("el", "GR")) }
    
    LaunchedEffect(uiState.errorResId, uiState.errorMessage, uiState.successMessageResId) {
        val errorText = uiState.errorResId?.let { context.getString(it) } ?: uiState.errorMessage
        errorText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessageResId?.let {
            snackbarHostState.showSnackbar(context.getString(it))
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.nav_expenses),
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
        floatingActionButton = {
            QuickAddFAB(
                onClick = onNavigateToAddExpense
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Loading with shimmer
                uiState.isLoading -> {
                    ShimmerList(
                        modifier = Modifier.padding(Spacing.lg),
                        itemCount = 5
                    )
                }
                
                // Empty state
                uiState.expenses.isEmpty() -> {
                    EmptyState(
                        emoji = Emojis.MONEY,
                        title = stringResource(R.string.empty_expenses),
                        subtitle = stringResource(R.string.empty_expenses_hint),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Button(
                            onClick = onNavigateToAddExpense,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = Shapes.Full
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.new_expense))
                        }
                    }
                }
                
                // Content
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        itemsIndexed(
                            items = uiState.expenses,
                            key = { _, expense -> expense.id }
                        ) { index, expense ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    )
                                ) + slideInVertically(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    ),
                                    initialOffsetY = { it / 4 }
                                )
                            ) {
                                PremiumExpenseCard(
                                    expense = expense,
                                    dateFormat = dateFormat,
                                    decimalFormat = decimalFormat,
                                    onEdit = { onNavigateToEditExpense(expense.id) },
                                    onDelete = { viewModel.deleteExpense(expense.id) }
                                )
                            }
                        }
                        
                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(Spacing.massive))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Premium Expense Card with category accent
 */
@Composable
private fun PremiumExpenseCard(
    expense: Expense,
    dateFormat: SimpleDateFormat,
    decimalFormat: DecimalFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val categoryColor = expense.category.toColor()
    
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.dialog_delete_expense_title),
            message = stringResource(R.string.dialog_delete_expense_message),
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = categoryColor.copy(alpha = 0.4f),
        contentPadding = PaddingValues(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon with colored background
            Box(
                modifier = Modifier
                    .size(Dimensions.iconHuge)
                    .background(
                        color = categoryColor.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = expense.category.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(expense.category.displayNameResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormat.format(Date(expense.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (expense.notes.isNotEmpty()) {
                    Text(
                        text = expense.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
            
            // Amount (red for expense)
            Text(
                text = "-${decimalFormat.format(expense.amount)}€",
                style = CustomTextStyles.SmallNumber,
                fontWeight = FontWeight.Bold,
                color = SemanticColors.Error
            )
            
            Spacer(modifier = Modifier.width(Spacing.sm))
            
            // Actions
            Column {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(Dimensions.touchTargetMin)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.btn_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimensions.iconSm)
                    )
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(Dimensions.touchTargetMin)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = SemanticColors.Error,
                        modifier = Modifier.size(Dimensions.iconSm)
                    )
                }
            }
        }
    }
}
