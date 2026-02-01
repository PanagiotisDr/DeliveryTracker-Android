package com.deliverytracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.presentation.theme.Spacing

/**
 * Premium Stat Badge με emoji και προαιρετικό circular background.
 * Χρησιμοποιείται στο Dashboard, ShiftListScreen, και Statistics.
 *
 * @param emoji Το emoji που εμφανίζεται (π.χ. "💰", "📦")
 * @param value Η τιμή που εμφανίζεται (π.χ. "150.00€", "12")
 * @param label Η ετικέτα κάτω από την τιμή (π.χ. "Έσοδα", "Παραγγελίες")
 * @param valueColor Χρώμα της τιμής (default: onSurface)
 * @param showBackground Αν θα εμφανίζεται circular background πίσω από το emoji
 * @param modifier Modifier για customization
 */
@Composable
fun StatBadge(
    emoji: String,
    value: String,
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    showBackground: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // Αυξημένο width για να χωράνε Ελληνικές λέξεις
        modifier = modifier.widthIn(min = 64.dp, max = 80.dp)
    ) {
        if (showBackground) {
            // Emoji σε circular background
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // Emoji χωρίς background (για Dashboard style)
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = if (showBackground) MaterialTheme.typography.titleSmall 
                    else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = label,
            style = if (showBackground) MaterialTheme.typography.labelSmall 
                    else MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

