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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Οθόνη προσθήκης/επεξεργασίας βάρδιας.
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
                    Text(if (viewModel.isEditMode) "Επεξεργασία Βάρδιας" else "Νέα Βάρδια") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Πίσω")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveShift() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Check, "Αποθήκευση")
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
                    text = "📅 Ημερομηνία",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = uiState.dateText,
                    onValueChange = { viewModel.updateDate(it) },
                    label = { Text("Ημερομηνία (ΗΗ/ΜΜ/ΕΕΕΕ)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    singleLine = true
                )
                
                HorizontalDivider()
                
                // Ώρες εργασίας
                Text(
                    text = "⏱️ Διάρκεια Εργασίας",
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
                        label = { Text("Ώρες") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("8") },
                        suffix = { Text("ω") }
                    )
                    
                    OutlinedTextField(
                        value = uiState.workedMinutes,
                        onValueChange = { viewModel.updateWorkedMinutes(it) },
                        label = { Text("Λεπτά") },
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
                    text = "💰 Έσοδα",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.grossIncome,
                        onValueChange = { viewModel.updateGrossIncome(it) },
                        label = { Text("Μικτά") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text("€") }
                    )
                    
                    OutlinedTextField(
                        value = uiState.tips,
                        onValueChange = { viewModel.updateTips(it) },
                        label = { Text("Tips") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text("€") }
                    )
                }
                
                // Bonus field
                OutlinedTextField(
                    value = uiState.bonus,
                    onValueChange = { viewModel.updateBonus(it) },
                    label = { Text("Bonus (Peak, Βροχή κτλ)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0,00") },
                    suffix = { Text("€") },
                    leadingIcon = { Icon(Icons.Default.Star, null) }
                )
                
                HorizontalDivider()
                
                // Έξοδα
                Text(
                    text = "📊 Έξοδα",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.fuelCost,
                        onValueChange = { viewModel.updateFuelCost(it) },
                        label = { Text("Καύσιμα") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text("€") }
                    )
                    
                    OutlinedTextField(
                        value = uiState.otherExpenses,
                        onValueChange = { viewModel.updateOtherExpenses(it) },
                        label = { Text("Άλλα") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        placeholder = { Text("0,00") },
                        suffix = { Text("€") }
                    )
                }
                
                HorizontalDivider()
                
                // Επιπλέον στοιχεία
                Text(
                    text = "📝 Λεπτομέρειες",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.ordersCount,
                        onValueChange = { viewModel.updateOrdersCount(it) },
                        label = { Text("Παραγγελίες") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, null) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.kilometers,
                        onValueChange = { viewModel.updateKilometers(it) },
                        label = { Text("Χιλιόμετρα") },
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
                    label = { Text("Σημειώσεις") },
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
                    Text(if (viewModel.isEditMode) "Ενημέρωση" else "Αποθήκευση")
                }
            }
        }
    }
}
