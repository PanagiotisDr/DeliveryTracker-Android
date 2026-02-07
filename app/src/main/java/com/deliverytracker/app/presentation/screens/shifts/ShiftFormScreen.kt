package com.deliverytracker.app.presentation.screens.shifts

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.presentation.components.ValidatedTextField
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Οθόνη προσθήκης/επεξεργασίας βάρδιας.
 * Βελτιωμένη έκδοση με Date Picker, υποχρεωτικά πεδία (*), και αυστηρό validation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShiftFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // State για Date Picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        // Περιορισμός: μόνο μέχρι σήμερα (όχι μελλοντικές ημερομηνίες)
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    
    // Αν αποθηκεύτηκε, επιστροφή
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    // Εμφάνιση error — dual-error pattern (validation + dynamic)
    LaunchedEffect(uiState.errorResId, uiState.errorMessage) {
        val message = when {
            uiState.errorResId != null -> null // Θα γίνει resolve στο @Composable scope
            uiState.errorMessage != null -> uiState.errorMessage
            else -> null
        }
        // Αν έχουμε errorResId, δεν μπορούμε να καλέσουμε stringResource εδώ
        // οπότε χρησιμοποιούμε dedicated composable effect
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(uiState.errorMessage!!)
            viewModel.clearError()
        }
    }
    
    // Resolve validation errors μέσω stringResource
    uiState.errorResId?.let { resId ->
        val errorText = stringResource(resId)
        LaunchedEffect(resId) {
            snackbarHostState.showSnackbar(errorText)
            viewModel.clearError()
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.updateDate(dateFormat.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (viewModel.isEditMode) 
                            stringResource(R.string.edit_shift)
                        else 
                            stringResource(R.string.new_shift),
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
                actions = {
                    IconButton(
                        onClick = { viewModel.saveShift() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            Icons.Default.Check, 
                            contentDescription = stringResource(R.string.btn_save),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
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
                // Ημερομηνία - με Date Picker
                Text(
                    text = "📅 ${stringResource(R.string.shift_date)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = uiState.dateText,
                    onValueChange = { /* Read-only, Date Picker */ },
                    label = { Text(stringResource(R.string.date_format_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.EditCalendar, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                HorizontalDivider()
                
                // Ώρες εργασίας - ΥΠΟΧΡΕΩΤΙΚΟ
                Text(
                    text = "⏱️ ${stringResource(R.string.shift_duration)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ώρες με validation feedback
                    ValidatedTextField(
                        value = uiState.workedHours,
                        onValueChange = { viewModel.updateWorkedHours(it) },
                        label = stringResource(R.string.shift_hours),
                        isValid = uiState.workedHours.isNotBlank() && uiState.workedHours.toIntOrNull() != null,
                        modifier = Modifier.weight(1f),
                        suffix = { Text(stringResource(R.string.shift_hours_suffix)) },
                        keyboardType = KeyboardType.Number
                    )
                    
                    // Λεπτά με validation feedback
                    ValidatedTextField(
                        value = uiState.workedMinutes,
                        onValueChange = { viewModel.updateWorkedMinutes(it) },
                        label = stringResource(R.string.shift_minutes),
                        isValid = uiState.workedMinutes.isNotBlank() && uiState.workedMinutes.toIntOrNull() != null,
                        modifier = Modifier.weight(1f),
                        suffix = { Text(stringResource(R.string.shift_minutes_suffix)) },
                        keyboardType = KeyboardType.Number
                    )
                }
                
                HorizontalDivider()
                
                // Έσοδα - ΥΠΟΧΡΕΩΤΙΚΟ
                Text(
                    text = "${Emojis.MONEY} ${stringResource(R.string.stats_income)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Εσοδα - με validation feedback
                    ValidatedTextField(
                        value = uiState.grossIncome,
                        onValueChange = { viewModel.updateGrossIncome(it) },
                        label = stringResource(R.string.stats_gross),
                        isValid = uiState.grossIncome.isNotBlank() && 
                            uiState.grossIncome.replace(",", ".").toDoubleOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        suffix = { Text(stringResource(R.string.currency_symbol)) },
                        keyboardType = KeyboardType.Decimal
                    )
                    
                    // Tips - προαιρετικό, πράσινο όταν έχει τιμή
                    ValidatedTextField(
                        value = uiState.tips,
                        onValueChange = { viewModel.updateTips(it) },
                        label = stringResource(R.string.shift_tips),
                        isValid = uiState.tips.isNotBlank() && 
                            uiState.tips.replace(",", ".").toDoubleOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        suffix = { Text(stringResource(R.string.currency_symbol)) },
                        keyboardType = KeyboardType.Decimal
                    )
                }
                
                // Bonus field - προαιρετικό, πράσινο όταν έχει τιμή
                ValidatedTextField(
                    value = uiState.bonus,
                    onValueChange = { viewModel.updateBonus(it) },
                    label = stringResource(R.string.shift_bonus),
                    isValid = uiState.bonus.isNotBlank() && 
                        uiState.bonus.replace(",", ".").toDoubleOrNull()?.let { it > 0 } == true,
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text(stringResource(R.string.currency_symbol)) },
                    leadingIcon = { Icon(Icons.Default.Star, null) },
                    keyboardType = KeyboardType.Decimal
                )
                
                HorizontalDivider()
                
                // Στατιστικά - ΥΠΟΧΡΕΩΤΙΚΑ
                Text(
                    text = "${Emojis.STATS} ${stringResource(R.string.nav_statistics)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Παραγγελίες - με validation feedback
                    ValidatedTextField(
                        value = uiState.ordersCount,
                        onValueChange = { viewModel.updateOrdersCount(it) },
                        label = stringResource(R.string.shift_orders),
                        isValid = uiState.ordersCount.isNotBlank() && uiState.ordersCount.toIntOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                        keyboardType = KeyboardType.Number
                    )
                    
                    // Χιλιόμετρα - με validation feedback
                    ValidatedTextField(
                        value = uiState.kilometers,
                        onValueChange = { viewModel.updateKilometers(it) },
                        label = stringResource(R.string.shift_kilometers),
                        isValid = uiState.kilometers.isNotBlank() && 
                            uiState.kilometers.replace(",", ".").toDoubleOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        suffix = { Text(stringResource(R.string.unit_km)) },
                        keyboardType = KeyboardType.Decimal
                    )
                }
                
                HorizontalDivider()
                
                // Σημειώσεις - ΠΡΟΑΙΡΕΤΙΚΟ
                Text(
                    text = "📝 ${stringResource(R.string.shift_notes)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    label = { Text(stringResource(R.string.notes_optional)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Dimensions.textFieldMinHeight),
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

