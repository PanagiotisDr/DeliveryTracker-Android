package com.deliverytracker.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*
import java.text.DecimalFormat

/**
 * Premium Hero Dashboard Card - Το κεντρικό component του Dashboard.
 * Εμφανίζει το κύριο metric με gradient background,
 * animated progress, και secondary stats.
 * 
 * @param primaryValue Η κύρια τιμή (π.χ. σημερινά έσοδα)
 * @param goalProgress Progress ως 0-1 (π.χ. 0.75 = 75%)
 * @param goalAmount Ο στόχος σε € (null αν δεν έχει οριστεί)
 * @param secondaryStats Λίστα με secondary stats
 */
@Composable
fun HeroDashboard(
    primaryValue: Double,
    goalProgress: Float,
    goalAmount: Double?,
    secondaryStats: List<HeroSecondaryStatItem>,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val shape = RoundedCornerShape(Spacing.radiusXl)
    
    // Premium gradient background
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            HeroGradientStart,
            HeroGradientMid,
            HeroGradientEnd
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                spotColor = HeroGradientMid.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(gradientBrush)
            .padding(Spacing.xl)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Label - λευκό με opacity
            Text(
                text = stringResource(R.string.hero_today_earnings),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Animated primary value - ΜΕΓΑΛΟ με glow effect
            AnimatedCounter(
                targetValue = primaryValue,
                suffix = "€",
                decimalPlaces = 2,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 52.sp,
                    letterSpacing = (-1).sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            
            // Progress bar (αν υπάρχει στόχος)
            if (goalAmount != null && goalAmount > 0) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Custom white progress bar
                HeroProgressBar(
                    progress = goalProgress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Progress text
                val progressPercent = (goalProgress * 100).toInt().coerceAtMost(100)
                Text(
                    text = "${decimalFormat.format(primaryValue)} / ${decimalFormat.format(goalAmount)}€ ($progressPercent%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                // Goal reached message
                if (goalProgress >= 1f) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "🎉 " + stringResource(R.string.dashboard_goal_reached),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Secondary stats row
            if (secondaryStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    secondaryStats.forEach { stat ->
                        HeroSecondaryStat(stat = stat)
                    }
                }
            }
        }
    }
}

/**
 * White progress bar για το Hero Dashboard.
 */
@Composable
private fun HeroProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = Motion.counterTween(),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
        )
    }
}

/**
 * Data class για secondary stats στο Hero Dashboard.
 */
data class HeroSecondaryStatItem(
    val emoji: String,
    val value: String,
    val label: String
)

/**
 * Single secondary stat item - με white text για gradient background.
 */
@Composable
private fun HeroSecondaryStat(stat: HeroSecondaryStatItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 60.dp, max = 100.dp)
    ) {
        Text(
            text = stat.emoji,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stat.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = stat.label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Gradient Progress Bar με χρώματα ανάλογα με το progress.
 * 🔴 < 50%, 🟠 50-99%, 🟢 >= 100%
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = Motion.counterTween(),
        label = "progress"
    )
    
    // Επιλογή χρώματος ανάλογα με το progress
    val progressColor = when {
        progress >= 1f -> GoalSuccess
        progress >= 0.5f -> GoalWarning
        else -> GoalDanger
    }
    
    // Gradient brush
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            progressColor.copy(alpha = 0.7f),
            progressColor
        )
    )
    
    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(gradientBrush)
        )
    }
}
