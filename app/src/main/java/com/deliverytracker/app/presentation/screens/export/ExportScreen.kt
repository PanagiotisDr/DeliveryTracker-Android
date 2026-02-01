package com.deliverytracker.app.presentation.screens.export

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Οθόνη εξαγωγής δεδομένων.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Ημερομηνίες - default: τρέχων μήνας
    val calendar = Calendar.getInstance()
    val endDate = remember { calendar.timeInMillis }
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startDate = remember { calendar.timeInMillis }
    
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    // Εμφάνιση μηνυμάτων
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.export_title),
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
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Περίοδος
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.export_period),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${dateFormat.format(Date(startDate))} - ${dateFormat.format(Date(endDate))}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.export_period_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Export Options
                Text(
                    text = stringResource(R.string.export_options_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Βάρδιες CSV
                ExportOptionCard(
                    icon = Icons.Default.CalendarMonth,
                    title = stringResource(R.string.export_shifts_csv),
                    description = stringResource(R.string.export_shifts_csv_desc),
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.exportShiftsToCsv(startDate, endDate) }
                )
                
                // Έξοδα CSV
                ExportOptionCard(
                    icon = Icons.Default.Payments,
                    title = stringResource(R.string.export_expenses_csv),
                    description = stringResource(R.string.export_expenses_csv_desc),
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.exportExpensesToCsv(startDate, endDate) }
                )
                
                // Αναφορά
                ExportOptionCard(
                    icon = Icons.Default.Description,
                    title = stringResource(R.string.export_report),
                    description = stringResource(R.string.export_report_desc),
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.exportReport(startDate, endDate) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Backup Section
                Text(
                    text = stringResource(R.string.backup_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Backup
                ExportOptionCard(
                    icon = Icons.Default.Backup,
                    title = stringResource(R.string.backup_create),
                    description = stringResource(R.string.backup_create_desc),
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.createBackup() }
                )
            }
            
            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ExportOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (!isLoading) onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
