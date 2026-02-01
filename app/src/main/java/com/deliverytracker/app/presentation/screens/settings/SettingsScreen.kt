package com.deliverytracker.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.presentation.theme.*

/**
 * ⚙️ Settings Screen - Google Pay Style
 * Clean white cards με grouped sections.
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
                title = { 
                    Text(
                        text = stringResource(R.string.nav_settings),
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
                actions = {
                    TextButton(
                        onClick = { viewModel.saveSettings() },
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.btn_save),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // ============ PROFILE CARD ============
                GPay_SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.username.take(1).uppercase().ifEmpty { "?" },
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(Spacing.lg))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.username.ifEmpty { stringResource(R.string.settings_user) },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = uiState.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (uiState.hasPin) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                // ============ ΣΤΟΧΟΙ ============
                GPay_SettingsCard(
                    title = stringResource(R.string.settings_goals),
                    icon = Icons.Default.Flag
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        GPay_OutlinedField(
                            value = uiState.dailyGoal,
                            onValueChange = { viewModel.updateDailyGoal(it) },
                            label = stringResource(R.string.settings_goal_daily),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                        GPay_OutlinedField(
                            value = uiState.weeklyGoal,
                            onValueChange = { viewModel.updateWeeklyGoal(it) },
                            label = stringResource(R.string.settings_goal_weekly),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        GPay_OutlinedField(
                            value = uiState.monthlyGoal,
                            onValueChange = { viewModel.updateMonthlyGoal(it) },
                            label = stringResource(R.string.settings_goal_monthly),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                        GPay_OutlinedField(
                            value = uiState.yearlyGoal,
                            onValueChange = { viewModel.updateYearlyGoal(it) },
                            label = stringResource(R.string.settings_goal_yearly),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // ============ ΦΟΡΟΛΟΓΙΚΑ ============
                GPay_SettingsCard(
                    title = stringResource(R.string.settings_tax),
                    icon = Icons.Default.AccountBalance
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        GPay_OutlinedField(
                            value = uiState.vatRate,
                            onValueChange = { viewModel.updateVatRate(it) },
                            label = stringResource(R.string.settings_vat),
                            suffix = "%",
                            modifier = Modifier.weight(1f)
                        )
                        GPay_OutlinedField(
                            value = uiState.monthlyEfka,
                            onValueChange = { viewModel.updateMonthlyEfka(it) },
                            label = stringResource(R.string.settings_efka),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Info hint
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(Spacing.radiusSm)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = stringResource(R.string.settings_tax_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // ============ ΘΕΜΑ ============
                GPay_SettingsCard(
                    title = stringResource(R.string.settings_theme),
                    icon = Icons.Default.Palette
                ) {
                    ThemeMode.entries.forEach { theme ->
                        GPay_ThemeOption(
                            theme = theme,
                            isSelected = uiState.theme == theme,
                            onClick = { viewModel.updateTheme(theme) }
                        )
                    }
                }
                
                // ============ SAVE BUTTON ============
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(Spacing.radiusFull),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = stringResource(R.string.btn_save_settings),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

/**
 * Settings Card wrapper
 */
@Composable
private fun GPay_SettingsCard(
    title: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
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
        Column(modifier = Modifier.padding(Spacing.lg)) {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
            content()
        }
    }
}

/**
 * Outlined TextField με Google Pay style
 */
@Composable
private fun GPay_OutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        suffix = { Text(suffix) },
        shape = RoundedCornerShape(Spacing.radiusMd)
    )
}

/**
 * Theme option row
 */
@Composable
private fun GPay_ThemeOption(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.radiusSm))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        
        val (icon, text) = when (theme) {
            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto to stringResource(R.string.theme_system)
            ThemeMode.LIGHT -> Icons.Default.LightMode to stringResource(R.string.theme_light)
            ThemeMode.DARK -> Icons.Default.DarkMode to stringResource(R.string.theme_dark)
        }
        
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary 
                  else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurface
        )
    }
}
