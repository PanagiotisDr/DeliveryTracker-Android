package com.deliverytracker.app.presentation.screens.shifts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R

/**
 * Οθόνη προσθήκης/επεξεργασίας βάρδιας.
 * Χρησιμοποιεί stringResource για localization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShiftFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Αν αποθηκεύτηκε, επιστροφή
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    // Εμφάνιση error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (viewModel.isEditMode) 
                            stringResource(R.string.edit_shift)
                        else 
                            stringResource(R.string.new_shift)
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
                actions = {
                    IconButton(
                        onClick = { viewModel.saveShift() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            Icons.Default.Check, 
                            contentDescription = stringResource(R.string.btn_save)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ημερομηνία
                Text(
                    text = "📅 ${stringResource(R.string.shift_date)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = uiState.dateText,
                    onValueChange = { viewModel.updateDate(it) },
                    label = { Text(stringResource(R.string.date_format_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    singleLine = true
                )
                
                HorizontalDivider()
                
                // Ώρες εργασίας
                Text(
                    text = "⏱️ ${stringResource(R.string.shift_duration)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.workedHours,
                        onValueChange = { viewModel.updateWorkedHours(it) },
                        label = { Text(stringResource(R.string.shift_hours)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("8") },
                        suffix = { Text("ω") }
                    )
                    
                    OutlinedTextField(
                        value = uiState.workedMinutes,
                        onValueChange = { viewModel.updateWorkedMinutes(it) },
                        label = { Text(stringResource(R.string.shift_minutes)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("30") },
                        suffix = { Text("λ") }
                    )
                }
                
                HorizontalDivider()
                
                // Έσοδα
                Text(
                    text = "💰 ${stringResource(R.string.stats_income)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.grossIncome,
                        onValueChange = { viewModel.updateGrossIncome(it) },
                        label = { Text(stringResource(R.string.stats_gross)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.tips,
                        onValueChange = { viewModel.updateTips(it) },
                        label = { Text(stringResource(R.string.shift_tips)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                }
                
                // Bonus field
                OutlinedTextField(
                    value = uiState.bonus,
                    onValueChange = { viewModel.updateBonus(it) },
                    label = { Text(stringResource(R.string.shift_bonus)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0,00") },
                    suffix = { Text(stringResource(R.string.currency_symbol)) },
                    leadingIcon = { Icon(Icons.Default.Star, null) }
                )
                
                HorizontalDivider()
                
                // Έξοδα
                Text(
                    text = "📊 ${stringResource(R.string.nav_expenses)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.fuelCost,
                        onValueChange = { viewModel.updateFuelCost(it) },
                        label = { Text(stringResource(R.string.category_fuel)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.otherExpenses,
                        onValueChange = { viewModel.updateOtherExpenses(it) },
                        label = { Text(stringResource(R.string.category_other)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                }
                
                HorizontalDivider()
                
                // Επιπλέον στοιχεία
                Text(
                    text = "📝 ${stringResource(R.string.shift_notes)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.ordersCount,
                        onValueChange = { viewModel.updateOrdersCount(it) },
                        label = { Text(stringResource(R.string.shift_orders)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, null) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.kilometers,
                        onValueChange = { viewModel.updateKilometers(it) },
                        label = { Text(stringResource(R.string.shift_kilometers)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,0") },
                        suffix = { Text("km") }
                    )
                }
                
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    label = { Text(stringResource(R.string.notes_optional)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 4,
                    leadingIcon = { Icon(Icons.Default.Notes, null) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Save button
                Button(
                    onClick = { viewModel.saveShift() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (viewModel.isEditMode) 
                            stringResource(R.string.update)
                        else 
                            stringResource(R.string.btn_save)
                    )
                }
            }
        }
    }
}
