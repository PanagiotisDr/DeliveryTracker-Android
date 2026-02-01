package com.deliverytracker.app.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.deliverytracker.app.R

/**
 * Generic dialog επιβεβαίωσης διαγραφής.
 * Χρησιμοποιείται σε ShiftCard, ExpenseCard, RecycleBin κλπ.
 *
 * @param title Ο τίτλος του dialog
 * @param message Το μήνυμα επιβεβαίωσης
 * @param onConfirm Callback όταν ο χρήστης επιβεβαιώσει τη διαγραφή
 * @param onDismiss Callback όταν ο χρήστης ακυρώσει
 */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.btn_delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}
