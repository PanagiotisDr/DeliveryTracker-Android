package com.deliverytracker.app.presentation.screens.shifts

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.components.*
import com.deliverytracker.app.presentation.screens.shifts.components.*
import com.deliverytracker.app.presentation.theme.*

/**
 * 📋 Shift List Screen - Premium Redesign 2026
 * 
 * Features:
 * - Glass morphism cards
 * - Shimmer loading
 * - Animated list items
 * - Premium FAB
 * 
 * Τα card components εξάχθηκαν στο components/ShiftCardComponents.kt
 * 
 * @author DeliveryTracker Team
 * @version 2.1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddShift: () -> Unit,
    onNavigateToEditShift: (String) -> Unit,
    viewModel: ShiftListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.nav_shifts),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            QuickAddFAB(
                onClick = onNavigateToAddShift
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Loading state με shimmer
                uiState.isLoading -> {
                    ShimmerList(
                        modifier = Modifier.padding(Spacing.lg),
                        itemCount = 5
                    )
                }
                
                // Empty state
                uiState.shifts.isEmpty() -> {
                    EmptyState(
                        emoji = Emojis.SHIFTS,
                        title = stringResource(R.string.empty_shifts),
                        subtitle = stringResource(R.string.empty_shifts_hint),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Button(
                            onClick = onNavigateToAddShift,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = Shapes.Full
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.new_shift))
                        }
                    }
                }
                
                // Content
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        itemsIndexed(
                            items = uiState.shifts,
                            key = { _, shift -> shift.id }
                        ) { index, shift ->
                            // Staggered animation
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    )
                                ) + slideInVertically(
                                    animationSpec = Motion.entranceTween(
                                        delayMs = staggerDelay(index)
                                    ),
                                    initialOffsetY = { it / 4 }
                                )
                            ) {
                                PremiumShiftCard(
                                    shift = shift,
                                    onEditClick = { onNavigateToEditShift(shift.id) },
                                    onDeleteClick = { viewModel.deleteShift(shift.id) }
                                )
                            }
                        }
                        
                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(Spacing.massive))
                        }
                    }
                }
            }
        }
    }
}
