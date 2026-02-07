package com.deliverytracker.app.presentation.screens.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.theme.*

// ════════════════════════════════════════════════════════════════════════════
// STATISTICS COMPONENTS
// Εξαγωγή UI components από StatisticsScreen για modularity.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Hero card για εμφάνιση καθαρού κέρδους.
 * Αλλάζει χρώμα ανάλογα αν το κέρδος είναι θετικό ή αρνητικό.
 */
@Composable
fun ProfitHeroCard(
    profit: Double,
    isPositive: Boolean
) {
    val gradientColors = if (isPositive) {
        listOf(
            SemanticColors.Success.copy(alpha = 0.15f),
            SemanticColors.Success.copy(alpha = 0.05f)
        )
    } else {
        listOf(
            SemanticColors.Error.copy(alpha = 0.15f),
            SemanticColors.Error.copy(alpha = 0.05f)
        )
    }
    
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = if (isPositive) SemanticColors.Success.copy(alpha = 0.3f)
                     else SemanticColors.Error.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.xl)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    shape = Shapes.Medium
                )
                .padding(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isPositive) AppEmojis.TREND_UP else AppEmojis.TREND_DOWN,
                    style = MaterialTheme.typography.displaySmall
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = stringResource(R.string.stats_net_profit).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = TypographyTokens.letterSpacingWide
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                AnimatedCurrency(
                    targetValue = profit,
                    valueStyle = CustomTextStyles.HeroNumber.copy(
                        color = if (isPositive) SemanticColors.Success 
                               else SemanticColors.Error
                    ),
                    symbolStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = if (isPositive) SemanticColors.Success.copy(alpha = 0.8f)
                               else SemanticColors.Error.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

/**
 * Summary card με glass effect και slot content.
 * Χρησιμοποιείται ως container για ομαδοποιημένα στατιστικά.
 */
@Composable
fun StatsSummaryCard(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Text(
            text = "$emoji $title",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        content()
    }
}

/**
 * Γραμμή στατιστικού με label και animated value.
 * Υποστηρίζει currency και plain number formats.
 */
@Composable
fun StatRow(
    label: String,
    value: Double,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false,
    isCurrency: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (isCurrency) {
            AnimatedCurrency(
                targetValue = value,
                valueStyle = (if (isBold) CustomTextStyles.SmallNumber 
                             else MaterialTheme.typography.bodyMedium).copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    color = valueColor
                ),
                symbolStyle = MaterialTheme.typography.bodySmall.copy(
                    color = valueColor.copy(alpha = 0.8f)
                )
            )
        } else {
            AnimatedCounter(
                targetValue = value,
                style = if (isBold) CustomTextStyles.SmallNumber 
                       else MaterialTheme.typography.bodyMedium,
                color = valueColor,
                decimalPlaces = 1
            )
        }
    }
}

/**
 * Ακέραιο στατιστικό για grid εμφάνιση (π.χ. βάρδιες, παραγγελίες).
 */
@Composable
fun NumberStat(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        AnimatedIntCounter(
            targetValue = value,
            style = CustomTextStyles.MediumNumber,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Δεκαδικό στατιστικό με suffix (π.χ. "42.5h").
 */
@Composable
fun NumberStatDecimal(
    value: Double,
    label: String,
    suffix: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedCounter(
                targetValue = value,
                style = CustomTextStyles.MediumNumber,
                color = MaterialTheme.colorScheme.onSurface,
                decimalPlaces = if (suffix == "h") 1 else 0
            )
            if (suffix.isNotEmpty()) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Card που αναδεικνύει την καλύτερη ημέρα (trophy style).
 */
@Composable
fun BestDayCard(
    income: Double,
    date: String
) {
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = SemanticColors.Warning.copy(alpha = 0.3f),
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SemanticColors.Warning.copy(alpha = 0.15f),
                            SemanticColors.Warning.copy(alpha = 0.05f)
                        )
                    ),
                    shape = Shapes.Medium
                )
                .padding(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = AppEmojis.TROPHY,
                    style = MaterialTheme.typography.displaySmall
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = stringResource(R.string.stats_best_day).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SemanticColors.Warning,
                    letterSpacing = TypographyTokens.letterSpacingWide
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                AnimatedCurrency(
                    targetValue = income,
                    valueStyle = CustomTextStyles.LargeNumber.copy(
                        color = SemanticColors.Warning
                    ),
                    symbolStyle = MaterialTheme.typography.titleMedium.copy(
                        color = SemanticColors.Warning.copy(alpha = 0.8f)
                    )
                )
                
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = SemanticColors.Warning.copy(alpha = 0.7f)
                )
            }
        }
    }
}
