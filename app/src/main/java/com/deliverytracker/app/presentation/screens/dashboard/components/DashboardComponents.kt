package com.deliverytracker.app.presentation.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*

// ════════════════════════════════════════════════════════════════════════════
// DASHBOARD COMPONENTS
// Εξαγωγή UI components από DashboardScreen για καλύτερη modularity.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Hero section με gradient background και animated counter.
 * Εμφανίζει τα σημερινά έσοδα και πρόοδο προς τον ημερήσιο στόχο.
 */
@Composable
fun PremiumHeroSection(
    amount: Double,
    label: String,
    progress: Float,
    goal: Double?
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    // Χρήση Warm Premium gradient (Deep Orange → Gold)
    val gradientColors = if (isDarkTheme) {
        listOf(
            BrandColors.Orange500,  // #FF5722 Deep Orange
            BrandColors.Gold500     // #FFC107 Gold
        )
    } else {
        listOf(
            BrandColors.Orange600,  // Πιο σκούρο για light mode
            BrandColors.Gold400
        )
    }
    
    val textColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            )
            .padding(
                horizontal = Spacing.lg,
                vertical = Spacing.xxl
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = textColor.copy(alpha = 0.8f),
                letterSpacing = TypographyTokens.letterSpacingWide
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Animated amount — THE BIG NUMBER
            AnimatedCurrency(
                targetValue = amount,
                valueStyle = CustomTextStyles.HeroNumber.copy(
                    color = textColor
                ),
                symbolStyle = MaterialTheme.typography.headlineLarge.copy(
                    color = textColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Normal
                ),
                color = textColor
            )
            
            // Goal progress (αν έχει οριστεί)
            if (goal != null && goal > 0) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(Dimensions.progressHeightDefault)
                        .background(
                            color = textColor.copy(alpha = 0.3f),
                            shape = Shapes.Full
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(
                                color = textColor,
                                shape = Shapes.Full
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                // Progress text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedPercentage(
                        targetValue = progress,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                    Text(
                        text = " ${stringResource(R.string.dashboard_daily_goal).lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Glass morphism stat card με animated value.
 * Χρησιμοποιείται για τα σημερινά στατιστικά (orders, hours).
 */
@Composable
fun GlassStatCard(
    value: Number,
    label: String,
    emoji: String,
    modifier: Modifier = Modifier,
    isDecimal: Boolean = false
) {
    GlassCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        glowEnabled = false,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Animated value
            if (isDecimal) {
                AnimatedCounter(
                    targetValue = value.toDouble(),
                    style = CustomTextStyles.MediumNumber,
                    color = MaterialTheme.colorScheme.onSurface,
                    decimalPlaces = 1
                )
            } else {
                AnimatedIntCounter(
                    targetValue = value.toInt(),
                    style = CustomTextStyles.MediumNumber,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Μηνιαίο summary card με progress ring.
 * Εμφανίζει καθαρά έσοδα, παραγγελίες, βάρδιες και πρόοδο.
 */
@Composable
fun MonthSummaryCard(
    netIncome: Double,
    orders: Int,
    shifts: Int,
    goal: Double?,
    progress: Float
) {
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f),
        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        // Top row: Net income + Progress ring
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Net income
            Column {
                Text(
                    text = stringResource(R.string.dashboard_net),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                AnimatedCurrency(
                    targetValue = netIncome,
                    valueStyle = CustomTextStyles.LargeNumber.copy(
                        color = if (netIncome >= 0) SemanticColors.Success 
                               else SemanticColors.Error
                    ),
                    symbolStyle = MaterialTheme.typography.titleMedium.copy(
                        color = if (netIncome >= 0) SemanticColors.Success.copy(alpha = 0.8f)
                               else SemanticColors.Error.copy(alpha = 0.8f)
                    )
                )
            }
            
            // Progress ring (αν υπάρχει στόχος)
            if (goal != null && goal > 0) {
                ProgressRingWithLabel(
                    progress = progress,
                    size = Dimensions.progressCircularMd,
                    strokeWidth = Dimensions.progressHeightDefault
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = Dimensions.borderHairline
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        // Bottom row: Orders & Shifts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniStat(
                value = orders,
                label = stringResource(R.string.dashboard_orders)
            )
            
            MiniStat(
                value = shifts,
                label = stringResource(R.string.dashboard_shifts)
            )
        }
    }
}

/**
 * Μικρό stat widget για χρήση μέσα σε cards.
 */
@Composable
fun MiniStat(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedIntCounter(
            targetValue = value,
            style = CustomTextStyles.SmallNumber,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
