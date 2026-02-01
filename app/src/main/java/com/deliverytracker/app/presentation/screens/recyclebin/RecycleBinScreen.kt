package com.deliverytracker.app.presentation.screens.recyclebin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Οθόνη Κάδου Ανακύκλωσης.
 * Χρησιμοποιεί stringResource για localization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecycleBinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    // Snackbar messages
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.recycle_bin_title),
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkSurfaces.Background
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
        } else if (uiState.deletedShifts.isEmpty() && uiState.deletedExpenses.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗑️", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.empty_recycle_bin),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.empty_recycle_bin_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Βάρδιες Section
                if (uiState.deletedShifts.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.recycle_bin_shifts, uiState.deletedShifts.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(uiState.deletedShifts, key = { "shift_${it.id}" }) { shift ->
                        DeletedShiftCard(
                            shift = shift,
                            dateFormat = dateFormat,
                            decimalFormat = decimalFormat,
                            onRestore = { viewModel.restoreShift(shift.id) },
                            onPermanentDelete = { viewModel.permanentDeleteShift(shift.id) }
                        )
                    }
                }
                
                // Έξοδα Section
                if (uiState.deletedExpenses.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.recycle_bin_expenses, uiState.deletedExpenses.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(uiState.deletedExpenses, key = { "expense_${it.id}" }) { expense ->
                        DeletedExpenseCard(
                            expense = expense,
                            dateFormat = dateFormat,
                            decimalFormat = decimalFormat,
                            onRestore = { viewModel.restoreExpense(expense.id) },
                            onPermanentDelete = { viewModel.permanentDeleteExpense(expense.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card για διαγραμμένη βάρδια.
 */
@Composable
private fun DeletedShiftCard(
    shift: Shift,
    dateFormat: SimpleDateFormat,
    decimalFormat: DecimalFormat,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text("📋", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormat.format(Date(shift.date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${shift.workedHours}ω ${shift.workedMinutes}λ • ${shift.ordersCount} παρ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Amount
            Text(
                text = "${decimalFormat.format(shift.totalIncome)}€",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Actions
            IconButton(onClick = onRestore) {
                Icon(
                    Icons.Default.Restore, 
                    contentDescription = stringResource(R.string.action_restore),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.DeleteForever, 
                    contentDescription = stringResource(R.string.action_permanent_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    // Permanent delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_permanent_delete_title)) },
            text = { Text(stringResource(R.string.dialog_permanent_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onPermanentDelete()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_delete), 
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

/**
 * Card για διαγραμμένο έξοδο.
 */
@Composable
private fun DeletedExpenseCard(
    expense: Expense,
    dateFormat: SimpleDateFormat,
    decimalFormat: DecimalFormat,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(expense.category.emoji, style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Amount
            Text(
                text = "-${decimalFormat.format(expense.amount)}€",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            
            // Actions
            IconButton(onClick = onRestore) {
                Icon(
                    Icons.Default.Restore, 
                    contentDescription = stringResource(R.string.action_restore),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.DeleteForever, 
                    contentDescription = stringResource(R.string.action_permanent_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    // Permanent delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_permanent_delete_title)) },
            text = { Text(stringResource(R.string.dialog_permanent_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onPermanentDelete()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_delete), 
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}
