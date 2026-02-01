package com.deliverytracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.presentation.theme.*

/**
 * Premium Card με gradient left border και soft shadow.
 * Χρησιμοποιείται για Shift και Expense cards.
 *
 * @param modifier Modifier για το εξωτερικό container
 * @param borderGradient Το gradient brush για το left border (null για solid color)
 * @param borderColor Solid color για το left border (αγνοείται αν υπάρχει borderGradient)
 * @param elevation Το elevation/shadow του card
 * @param content Το περιεχόμενο του card
 */
@Composable
fun GradientLeftBorderCard(
    modifier: Modifier = Modifier,
    borderGradient: Brush? = null,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    elevation: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    // Default gradient αν δεν δοθεί
    val actualGradient = borderGradient ?: Brush.verticalGradient(
        colors = listOf(
            HeroGradientStart,
            HeroGradientMid,
            HeroGradientEnd
        )
    )
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(Spacing.radiusLg),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(Spacing.radiusLg),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row {
            // Gradient ή solid left border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .then(
                        if (borderGradient != null) {
                            Modifier.background(brush = actualGradient)
                        } else {
                            Modifier.background(color = borderColor)
                        }
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                content = content
            )
        }
    }
}

/**
 * Predefined gradient για Shift cards.
 */
val ShiftCardGradient = Brush.verticalGradient(
    colors = listOf(
        HeroGradientStart,
        HeroGradientMid,
        HeroGradientEnd
    )
)
