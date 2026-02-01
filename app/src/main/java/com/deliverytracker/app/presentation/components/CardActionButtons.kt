package com.deliverytracker.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.Spacing

/**
 * Reusable Edit/Delete action buttons για cards.
 * Χρησιμοποιείται σε ShiftCard, ExpenseCard κλπ.
 *
 * @param onEdit Callback για το edit button
 * @param onDelete Callback για το delete button
 * @param modifier Modifier για customization
 */
@Composable
fun CardActionButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = modifier
    ) {
        // Edit button με subtle background
        Surface(
            onClick = onEdit,
            shape = RoundedCornerShape(Spacing.radiusSm),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.btn_edit),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(Spacing.sm)
            )
        }
        
        // Delete button με error background
        Surface(
            onClick = onDelete,
            shape = RoundedCornerShape(Spacing.radiusSm),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.btn_delete),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(Spacing.sm)
            )
        }
    }
}
