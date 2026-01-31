package com.deliverytracker.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Οθόνη ρύθμισης PIN μετά το πρώτο login.
 */
@Composable
fun PinSetupScreen(
    onSetupComplete: () -> Unit,
    onSkip: () -> Unit,
    viewModel: PinSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Επιτυχής αποθήκευση ή skip
    LaunchedEffect(uiState.isSaved, uiState.isSkipped) {
        if (uiState.isSaved || uiState.isSkipped) {
            onSetupComplete()
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
            
            // Icon
            Text(
                text = "🔐",
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (uiState.isConfirming) "Επιβεβαίωσε το PIN" else "Όρισε το PIN",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (uiState.isConfirming) 
                    "Εισάγετε ξανά το PIN για επιβεβαίωση" 
                else 
                    "Θα το χρησιμοποιείς για γρήγορη σύνδεση",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // PIN Dots
            PinDotsSetup(
                enteredLength = if (uiState.isConfirming) 
                    uiState.confirmPin.length 
                else 
                    uiState.pin.length
            )
            
            // Error
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Number Pad
            NumberPadSetup(
                onDigitClick = { viewModel.addDigit(it) },
                onDeleteClick = { viewModel.removeDigit() },
                enabled = !uiState.isLoading
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Skip button
            TextButton(onClick = { viewModel.skip() }) {
                Text("Παράλειψη - Όχι τώρα")
            }
            
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun PinDotsSetup(enteredLength: Int) {
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

@Composable
private fun NumberPadSetup(
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
                        Spacer(modifier = Modifier.size(72.dp))
                    } else if (button == "⌫") {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable(enabled = enabled) { onDeleteClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Διαγραφή",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable(enabled = enabled) { onDigitClick(button) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = button,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
