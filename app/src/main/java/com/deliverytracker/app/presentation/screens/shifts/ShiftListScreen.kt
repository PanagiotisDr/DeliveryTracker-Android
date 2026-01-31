package com.deliverytracker.app.presentation.screens.shifts

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.domain.model.Shift
import java.text.SimpleDateFormat
import java.util.*

/**
 * Οθόνη λίστας βαρδιών.
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
    
    // Εμφάνιση μηνυμάτων
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
                title = { Text("Βάρδιες") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Πίσω")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddShift,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Προσθήκη βάρδιας")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.shifts.isEmpty() -> {
                    EmptyShiftsContent(
                        modifier = Modifier.align(Alignment.Center),
                        onAddClick = onNavigateToAddShift
                    )
                }
                else -> {
                    ShiftsList(
                        shifts = uiState.shifts,
                        onEditClick = onNavigateToEditShift,
                        onDeleteClick = { viewModel.deleteShift(it) }
                    )
                }
            }
        }
    }
}

/**
 * Περιεχόμενο όταν δεν υπάρχουν βάρδιες.
 */
@Composable
private fun EmptyShiftsContent(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WorkOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Δεν έχεις καταχωρήσει βάρδιες",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Πάτα το κουμπί + για να προσθέσεις την πρώτη σου βάρδια!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Νέα Βάρδια")
        }
    }
}

/**
 * Λίστα βαρδιών.
 */
@Composable
private fun ShiftsList(
    shifts: List<Shift>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(shifts, key = { it.id }) { shift ->
            ShiftCard(
                shift = shift,
                onEditClick = { onEditClick(shift.id) },
                onDeleteClick = { onDeleteClick(shift.id) }
            )
        }
    }
}

/**
 * Κάρτα βάρδιας.
 */
@Composable
private fun ShiftCard(
    shift: Shift,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Dialog επιβεβαίωσης διαγραφής
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Διαγραφή Βάρδιας") },
            text = { Text("Είσαι σίγουρος ότι θέλεις να διαγράψεις αυτή τη βάρδια;") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("Διαγραφή", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Ακύρωση")
                }
            }
        )
    }
    
    val dateFormat = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale("el", "GR")) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header με ημερομηνία και actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(Date(shift.date)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = shift.formattedWorkTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            "Επεξεργασία",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            "Διαγραφή",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Στατιστικά
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Καθαρά",
                    value = "%.2f€".format(shift.netIncome),
                    color = if (shift.netIncome >= 0) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.error
                )
                StatItem(
                    label = "Ώρες",
                    value = "%.1f".format(shift.hoursWorked)
                )
                StatItem(
                    label = "Παραγγελίες",
                    value = shift.ordersCount.toString()
                )
                StatItem(
                    label = "€/Ώρα",
                    value = "%.2f".format(shift.incomePerHour)
                )
            }
        }
    }
}

/**
 * Στοιχείο στατιστικού στην κάρτα.
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
