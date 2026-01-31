package com.deliverytracker.app.presentation.screens.settings

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
import com.deliverytracker.app.domain.model.ThemeMode

/**
 * Οθόνη Ρυθμίσεων.
 * Χρησιμοποιεί stringResource για localization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(R.string.msg_settings_saved)
    
    // Snackbar για μηνύματα
    LaunchedEffect(uiState.isSaved, uiState.error) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar(savedMessage)
            viewModel.clearMessages()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ ${stringResource(R.string.nav_settings)}") },
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
                        onClick = { viewModel.saveSettings() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            Icons.Default.Save, 
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
                // Προφίλ Χρήστη
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = uiState.username.ifEmpty { stringResource(R.string.settings_user) },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = uiState.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.hasPin) {
                                Text(
                                    text = "🔒 ${stringResource(R.string.settings_pin_active)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                HorizontalDivider()
                
                // ============ Στόχοι ============
                Text(
                    text = "🎯 ${stringResource(R.string.settings_goals)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.dailyGoal,
                        onValueChange = { viewModel.updateDailyGoal(it) },
                        label = { Text(stringResource(R.string.settings_goal_daily)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.weeklyGoal,
                        onValueChange = { viewModel.updateWeeklyGoal(it) },
                        label = { Text(stringResource(R.string.settings_goal_weekly)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.monthlyGoal,
                        onValueChange = { viewModel.updateMonthlyGoal(it) },
                        label = { Text(stringResource(R.string.settings_goal_monthly)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.yearlyGoal,
                        onValueChange = { viewModel.updateYearlyGoal(it) },
                        label = { Text(stringResource(R.string.settings_goal_yearly)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                }
                
                HorizontalDivider()
                
                // ============ Φορολογικά ============
                Text(
                    text = "💼 ${stringResource(R.string.settings_tax)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.vatRate,
                        onValueChange = { viewModel.updateVatRate(it) },
                        label = { Text(stringResource(R.string.settings_vat)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = { Text("%") },
                        leadingIcon = { Icon(Icons.Default.Percent, null) }
                    )
                    
                    OutlinedTextField(
                        value = uiState.monthlyEfka,
                        onValueChange = { viewModel.updateMonthlyEfka(it) },
                        label = { Text(stringResource(R.string.settings_efka)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text(stringResource(R.string.currency_symbol)) }
                    )
                }
                
                // Επεξήγηση
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "💡 ${stringResource(R.string.settings_tax_hint)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                HorizontalDivider()
                
                // ============ Θέμα ============
                Text(
                    text = "🎨 ${stringResource(R.string.settings_theme)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                ThemeMode.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = uiState.theme == theme,
                                onClick = { viewModel.updateTheme(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.theme == theme,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (theme) {
                                ThemeMode.SYSTEM -> "🌓 ${stringResource(R.string.theme_system)}"
                                ThemeMode.LIGHT -> "☀️ ${stringResource(R.string.theme_light)}"
                                ThemeMode.DARK -> "🌙 ${stringResource(R.string.theme_dark)}"
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Save button
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_save_settings))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
