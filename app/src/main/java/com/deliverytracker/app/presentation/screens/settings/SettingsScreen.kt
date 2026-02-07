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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*

/**
 * ⚙️ Settings Screen - Premium Redesign 2026
 * 
 * Features:
 * - Glass morphism cards
 * - Organized sections
 * - Premium form styling
 * - Gradient save button
 * 
 * @author DeliveryTracker Team
 * @version 2.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPinSetup: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.msg_settings_saved)
    val pinRemovedMessage = stringResource(R.string.msg_pin_removed)
    // Τοπικό state για το dialog επιβεβαίωσης αφαίρεσης PIN
    var showPinRemoveDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isSaved, uiState.pinRemoved, uiState.errorResId, uiState.errorMessage) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar(savedMessage)
            viewModel.clearMessages()
        }
        if (uiState.pinRemoved) {
            snackbarHostState.showSnackbar(pinRemovedMessage)
            viewModel.clearMessages()
        }
        val errorText = uiState.errorResId?.let { context.getString(it) } ?: uiState.errorMessage
        errorText?.let {
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
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
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
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = Dimensions.progressHeightDefault
                )
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
                // ════════════════════════════════════════════════════════════
                // PROFILE CARD
                // ════════════════════════════════════════════════════════════
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gradient avatar
                        Box(
                            modifier = Modifier
                                .size(Dimensions.iconXl)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    ),
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
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
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
                                modifier = Modifier.size(Dimensions.iconSm)
                            )
                        }
                    }
                }
                
                // ════════════════════════════════════════════════════════════
                // ΣΤΟΧΟΙ (GOALS)
                // ════════════════════════════════════════════════════════════
                SettingsCard(
                    title = stringResource(R.string.settings_goals),
                    icon = Icons.Default.Flag
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        PremiumOutlinedField(
                            value = uiState.dailyGoal,
                            onValueChange = { viewModel.updateDailyGoal(it) },
                            label = stringResource(R.string.settings_goal_daily),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                        PremiumOutlinedField(
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
                        PremiumOutlinedField(
                            value = uiState.monthlyGoal,
                            onValueChange = { viewModel.updateMonthlyGoal(it) },
                            label = stringResource(R.string.settings_goal_monthly),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                        PremiumOutlinedField(
                            value = uiState.yearlyGoal,
                            onValueChange = { viewModel.updateYearlyGoal(it) },
                            label = stringResource(R.string.settings_goal_yearly),
                            suffix = "€",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // ════════════════════════════════════════════════════════════
                // ΦΟΡΟΛΟΓΙΚΑ (TAX)
                // ════════════════════════════════════════════════════════════
                SettingsCard(
                    title = stringResource(R.string.settings_tax),
                    icon = Icons.Default.AccountBalance
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        PremiumOutlinedField(
                            value = uiState.vatRate,
                            onValueChange = { viewModel.updateVatRate(it) },
                            label = stringResource(R.string.settings_vat),
                            suffix = "%",
                            modifier = Modifier.weight(1f)
                        )
                        PremiumOutlinedField(
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = Shapes.Medium
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimensions.iconSm)
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
                
                // ════════════════════════════════════════════════════════════
                // ΑΣΦΑΛΕΙΑ (SECURITY) — PIN Management
                // ════════════════════════════════════════════════════════════
                SettingsCard(
                    title = stringResource(R.string.settings_security),
                    icon = Icons.Default.Lock
                ) {
                    // PIN Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Pin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimensions.iconSm)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_pin),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.settings_pin_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.hasPin,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    // Πλοήγηση στη ρύθμιση PIN
                                    onNavigateToPinSetup()
                                } else {
                                    // Εμφάνιση dialog επιβεβαίωσης για αφαίρεση
                                    showPinRemoveDialog = true
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    
                    // Κουμπί αλλαγής PIN (μόνο αν ενεργό)
                    if (uiState.hasPin) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.sm),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPinSetup() }
                                .padding(vertical = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimensions.iconSm)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = stringResource(R.string.settings_change_pin),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Dialog Επιβεβαίωσης Αφαίρεσης PIN
                if (showPinRemoveDialog) {
                    AlertDialog(
                        onDismissRequest = { showPinRemoveDialog = false },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                        title = { Text(stringResource(R.string.settings_remove_pin)) },
                        text = { Text(stringResource(R.string.settings_pin_remove_confirm)) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.removePin()
                                    showPinRemoveDialog = false
                                }
                            ) {
                                Text(
                                    stringResource(R.string.settings_remove_pin),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPinRemoveDialog = false }) {
                                Text(stringResource(R.string.btn_cancel))
                            }
                        }
                    )
                }
                
                // ════════════════════════════════════════════════════════════
                // ΘΕΜΑ (THEME)
                // ════════════════════════════════════════════════════════════
                SettingsCard(
                    title = stringResource(R.string.settings_theme),
                    icon = Icons.Default.Palette
                ) {
                    ThemeMode.entries.forEach { theme ->
                        ThemeOption(
                            theme = theme,
                            isSelected = uiState.theme == theme,
                            onClick = { viewModel.updateTheme(theme) }
                        )
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.md),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    
                    // Dynamic Color (Material You) Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Colorize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimensions.iconSm)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_dynamic_color),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.settings_dynamic_color_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.dynamicColor,
                            onCheckedChange = { viewModel.updateDynamicColor(it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // ════════════════════════════════════════════════════════════
                // SAVE BUTTON
                // ════════════════════════════════════════════════════════════
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.buttonHeightLg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = Shapes.Full,
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

// ════════════════════════════════════════════════════════════════════════════
// PRIVATE COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Settings card with glass effect
 */
@Composable
private fun SettingsCard(
    title: String? = null,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        if (title != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimensions.iconSm)
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

/**
 * Premium outlined text field
 */
@Composable
private fun PremiumOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        suffix = { 
            Text(
                text = suffix,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        shape = Shapes.Medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * Theme option row
 */
@Composable
private fun ThemeOption(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.Small)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
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
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimensions.iconSm)
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
