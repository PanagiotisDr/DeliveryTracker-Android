package com.deliverytracker.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.deliverytracker.app.presentation.theme.GoalSuccess

/**
 * Premium OutlinedTextField με visual validation feedback.
 * 
 * - Πράσινο border + checkmark όταν το πεδίο είναι valid
 * - Neutral border για empty/invalid fields
 * - Animated transitions μεταξύ states
 * 
 * @param value Η τρέχουσα τιμή του πεδίου
 * @param onValueChange Callback για αλλαγή τιμής
 * @param label Το label του πεδίου
 * @param isValid Αν η τιμή είναι έγκυρη (για visual feedback)
 * @param modifier Modifier για styling
 * @param leadingIcon Προαιρετικό icon στην αρχή
 * @param suffix Προαιρετικό suffix (π.χ. "€", "χλμ")
 * @param keyboardType Τύπος πληκτρολογίου
 * @param singleLine Αν είναι single line
 * @param readOnly Αν είναι read-only
 * @param enabled Αν είναι ενεργοποιημένο
 */
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isValid: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    // Animated color transition για smooth UX
    val borderColor by animateColorAsState(
        targetValue = if (isValid) GoalSuccess else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isValid) GoalSuccess else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "iconColor"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        trailingIcon = {
            // Animated checkmark για valid fields
            if (isValid) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        },
        suffix = suffix,
        singleLine = singleLine,
        readOnly = readOnly,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = if (isValid) GoalSuccess.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline,
            cursorColor = if (isValid) GoalSuccess else MaterialTheme.colorScheme.primary
        )
    )
}
