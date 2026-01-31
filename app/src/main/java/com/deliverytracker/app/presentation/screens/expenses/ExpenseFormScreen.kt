package com.deliverytracker.app.presentation.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.PaymentMethod

/**
 * Οθόνη προσθήκης/επεξεργασίας εξόδου.
 * Χρησιμοποιεί stringResource για localization.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseFormViewModel = hiltViewModel()
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
                            stringResource(R.string.edit_expense)
                        else 
                            stringResource(R.string.new_expense)
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
                        onClick = { viewModel.saveExpense() },
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
                    text = "📅 ${stringResource(R.string.expense_date)}",
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
                
                // Κατηγορία
                Text(
                    text = "📁 ${stringResource(R.string.expense_category)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Category chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExpenseCategory.entries.forEach { category ->
                        FilterChip(
                            selected = uiState.category == category,
                            onClick = { viewModel.updateCategory(category) },
                            label = { Text("${category.emoji} ${category.displayName}") }
                        )
                    }
                }
                
                HorizontalDivider()
                
                // Ποσό
                Text(
                    text = "💵 ${stringResource(R.string.expense_amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    label = { Text(stringResource(R.string.expense_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0,00") },
                    suffix = { Text(stringResource(R.string.currency_symbol)) },
                    leadingIcon = { Icon(Icons.Default.Euro, null) }
                )
                
                HorizontalDivider()
                
                // Μέθοδος Πληρωμής
                Text(
                    text = "💳 ${stringResource(R.string.expense_payment_method)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = uiState.paymentMethod == method,
                                    onClick = { viewModel.updatePaymentMethod(method) },
                                    role = Role.RadioButton
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.paymentMethod == method,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (method) {
                                    PaymentMethod.CASH -> "💵 ${stringResource(R.string.payment_cash)}"
                                    PaymentMethod.CARD -> "💳 ${stringResource(R.string.payment_card)}"
                                }
                            )
                        }
                    }
                }
                
                HorizontalDivider()
                
                // Σημειώσεις
                Text(
                    text = "📝 ${stringResource(R.string.expense_notes)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
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
                    onClick = { viewModel.saveExpense() },
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
