package com.deliverytracker.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.deliverytracker.app.presentation.theme.Dimensions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.AppEmojis

/**
 * Οθόνη σύνδεσης με PIN.
 */
@Composable
fun PinLoginScreen(
    onPinSuccess: () -> Unit,
    onUsePassword: () -> Unit,
    viewModel: PinLoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Επιτυχία -> Dashboard
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onPinSuccess()
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Logo
            Text(
                text = AppEmojis.MOTORCYCLE,
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.auth_enter_pin),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // PIN Dots
            PinDots(enteredLength = uiState.enteredPin.length)
            
            // Error message - Χρήση errorRes ή errorMessage
            val errorText = uiState.errorRes?.let { stringResource(it) } ?: uiState.errorMessage
            errorText?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Number Pad
            NumberPad(
                onDigitClick = { viewModel.addDigit(it) },
                onDeleteClick = { viewModel.removeDigit() },
                enabled = !uiState.isLoading
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Σύνδεση με κωδικό
            TextButton(onClick = onUsePassword) {
                Text(stringResource(R.string.auth_login_with_password))
            }
            
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * Οι τελείες που δείχνουν πόσα ψηφία υπάρχουν.
 */
@Composable
private fun PinDots(enteredLength: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < enteredLength) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

/**
 * Αριθμητικό πληκτρολόγιο.
 */
@Composable
private fun NumberPad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    enabled: Boolean
) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                row.forEach { button ->
                    if (button.isEmpty()) {
                        // Κενό κουμπί
                        Spacer(modifier = Modifier.size(72.dp))
                    } else if (button == "⌫") {
                        // Delete button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable(enabled = enabled) { onDeleteClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = stringResource(R.string.action_delete),
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        // Αριθμητικό κουμπί
                        NumberButton(
                            digit = button,
                            onClick = { onDigitClick(button) },
                            enabled = enabled
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Κουμπί αριθμού.
 */
@Composable
private fun NumberButton(
    digit: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(
                width = Dimensions.borderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
