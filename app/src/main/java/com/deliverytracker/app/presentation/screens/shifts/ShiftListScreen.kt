package com.deliverytracker.app.presentation.screens.shifts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.presentation.components.ConfirmDeleteDialog
import com.deliverytracker.app.presentation.components.EmptyState
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📋 Shift List Screen - Google Pay Style
 * Clean white cards με soft shadows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddShift: () -> Unit,
    onNavigateToEditShift: (String) -> Unit,
    viewModel: ShiftListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
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
                        text = stringResource(R.string.nav_shifts),
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddShift,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(Spacing.radiusMd)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = stringResource(R.string.new_shift),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                uiState.shifts.isEmpty() -> {
                    EmptyState(
                        emoji = "📋",
                        title = stringResource(R.string.empty_shifts),
                        subtitle = stringResource(R.string.empty_shifts_hint),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Button(
                            onClick = onNavigateToAddShift,
                            shape = RoundedCornerShape(Spacing.radiusFull)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.new_shift))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        itemsIndexed(
                            items = uiState.shifts,
                            key = { _, shift -> shift.id }
                        ) { _, shift ->
                            GPay_ShiftCard(
                                shift = shift,
                                onEditClick = { onNavigateToEditShift(shift.id) },
                                onDeleteClick = { viewModel.deleteShift(shift.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 💳 Google Pay Style Shift Card
 * Clean white, rounded, με soft shadow.
 */
@Composable
private fun GPay_ShiftCard(
    shift: Shift,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale("el", "GR")) }
    
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.dialog_delete_shift_title),
            message = stringResource(R.string.dialog_delete_shift_message),
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    
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
        Column(
            modifier = Modifier.padding(Spacing.lg)
        ) {
            // Header: Date + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(Date(shift.date)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = shift.formattedWorkTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.btn_edit),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.btn_delete),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Stats Grid - 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GPay_CardStat(
                    value = "%.2f€".format(shift.netIncome),
                    label = stringResource(R.string.dashboard_net),
                    valueColor = if (shift.netIncome >= 0) GPay_Success 
                                else MaterialTheme.colorScheme.error
                )
                GPay_CardStat(
                    value = "%.1fh".format(shift.hoursWorked),
                    label = stringResource(R.string.dashboard_hours)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GPay_CardStat(
                    value = shift.ordersCount.toString(),
                    label = stringResource(R.string.dashboard_orders)
                )
                GPay_CardStat(
                    value = "%.2f€".format(shift.incomePerHour),
                    label = stringResource(R.string.stats_per_hour)
                )
            }
        }
    }
}

/**
 * Stat item για μέσα σε cards
 */
@Composable
private fun GPay_CardStat(
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
