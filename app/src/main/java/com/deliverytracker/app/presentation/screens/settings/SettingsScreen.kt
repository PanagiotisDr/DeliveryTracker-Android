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
                actions = {
                    TextButton(
                        onClick = { viewModel.saveSettings() },
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.btn_save),
                            color = BrandColors.Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurfaces.Background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkSurfaces.Background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = BrandColors.Primary,
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
                                        colors = Gradients.EarningsVibrant
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.username.take(1).uppercase().ifEmpty { "?" },
                                style = MaterialTheme.typography.headlineSmall,
                                color = DarkText.OnPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(Spacing.lg))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.username.ifEmpty { stringResource(R.string.settings_user) },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText.Primary
                            )
                            Text(
                                text = uiState.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkText.Secondary
                            )
                        }
                        
                        if (uiState.hasPin) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = BrandColors.Primary,
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
                        color = BrandColors.Primary.copy(alpha = 0.1f),
                        shape = Shapes.Medium
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = BrandColors.Primary,
                                modifier = Modifier.size(Dimensions.iconSm)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = stringResource(R.string.settings_tax_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkText.Secondary
                            )
                        }
                    }
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
                        containerColor = BrandColors.Primary,
                        contentColor = DarkText.OnPrimary
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
        backgroundColor = DarkSurfaces.SurfaceContainer,
        borderColor = DarkBorders.Glass,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        if (title != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = BrandColors.Primary,
                        modifier = Modifier.size(Dimensions.iconSm)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText.Primary
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
                color = DarkText.Secondary
            ) 
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        suffix = { 
            Text(
                text = suffix,
                color = DarkText.Secondary
            ) 
        },
        shape = Shapes.Medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandColors.Primary,
            unfocusedBorderColor = DarkBorders.Subtle,
            focusedLabelColor = BrandColors.Primary,
            cursorColor = BrandColors.Primary,
            focusedTextColor = DarkText.Primary,
            unfocusedTextColor = DarkText.Primary
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
                selectedColor = BrandColors.Primary,
                unselectedColor = DarkText.Tertiary
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
            tint = if (isSelected) BrandColors.Primary else DarkText.Secondary,
            modifier = Modifier.size(Dimensions.iconSm)
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) BrandColors.Primary else DarkText.Primary
        )
    }
}
