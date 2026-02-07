package com.deliverytracker.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.deliverytracker.app.presentation.screens.auth.LoginScreen
import com.deliverytracker.app.presentation.screens.auth.PinLoginScreen
import com.deliverytracker.app.presentation.screens.auth.PinSetupScreen
import com.deliverytracker.app.presentation.screens.auth.RegisterScreen
import com.deliverytracker.app.presentation.screens.commandcenter.CommandCenterScreen
import com.deliverytracker.app.presentation.screens.shifts.ShiftFormScreen
import com.deliverytracker.app.presentation.screens.shifts.ShiftListScreen

// Διάρκεια animations σε ms — κεντρικά για εύκολη αλλαγή
private const val NAV_ANIM_DURATION = 300
private const val NAV_ANIM_DURATION_FAST = 200

/**
 * Κύριο Navigation Graph της εφαρμογής.
 * Χρησιμοποιεί Type-Safe Navigation (Compose 2.9+).
 * Animated transitions: fade+slide οριζόντια για main, vertical slide για forms.
 *
 * @param modifier Modifier για padding από το parent Scaffold
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    hasPin: Boolean = false,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Box wrapper για να εφαρμόσουμε το padding από το Scaffold
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = when {
                isLoggedIn && hasPin -> PinLogin
                isLoggedIn -> Dashboard
                else -> Login
            },
            // Default transitions — fade + subtle horizontal slide
            enterTransition = {
                fadeIn(tween(NAV_ANIM_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it / 4 },
                    animationSpec = tween(NAV_ANIM_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(NAV_ANIM_DURATION_FAST)) + slideOutHorizontally(
                    targetOffsetX = { -it / 4 },
                    animationSpec = tween(NAV_ANIM_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(NAV_ANIM_DURATION)) + slideInHorizontally(
                    initialOffsetX = { -it / 4 },
                    animationSpec = tween(NAV_ANIM_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(NAV_ANIM_DURATION_FAST)) + slideOutHorizontally(
                    targetOffsetX = { it / 4 },
                    animationSpec = tween(NAV_ANIM_DURATION)
                )
            }
        ) {
            // ════════════════════════════════════════════════════
            // AUTH SCREENS
            // ════════════════════════════════════════════════════
            
            composable<Login> {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Register)
                    },
                    onLoginSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo<Login> { inclusive = true }
                        }
                    },
                    onNeedsPinSetup = {
                        navController.navigate(PinSetup) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Register> {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        // Μετά την εγγραφή, πήγαινε στο PIN setup
                        navController.navigate(PinSetup) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }
            
            // ════════════════════════════════════════════════════
            // PIN SCREENS
            // ════════════════════════════════════════════════════
            
            composable<PinLogin> {
                PinLoginScreen(
                    onPinSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo<PinLogin> { inclusive = true }
                        }
                    },
                    onUsePassword = {
                        // Αποσύνδεση για να πάει στο κανονικό login
                        onSignOut()
                        navController.navigate(Login) {
                            popUpTo<PinLogin> { inclusive = true }
                        }
                    }
                )
            }
            
            composable<PinSetup> {
                PinSetupScreen(
                    onSetupComplete = {
                        navController.navigate(Dashboard) {
                            popUpTo<PinSetup> { inclusive = true }
                        }
                    },
                    onSkip = {
                        navController.navigate(Dashboard) {
                            popUpTo<PinSetup> { inclusive = true }
                        }
                    }
                )
            }
            
            // ════════════════════════════════════════════════════
            // MAIN SCREENS
            // ════════════════════════════════════════════════════
            
            composable<Dashboard> {
                CommandCenterScreen(
                    onNavigateToAddShift = {
                        navController.navigate(ShiftForm())
                    },
                    onNavigateToAddExpense = {
                        navController.navigate(ExpenseForm())
                    },
                    onNavigateToSettings = {
                        navController.navigate(Settings)
                    },
                    onNavigateToShiftDetail = { shiftId ->
                        navController.navigate(ShiftForm(shiftId = shiftId))
                    }
                )
            }
            
            // ════════════════════════════════════════════════════
            // SHIFT SCREENS
            // ════════════════════════════════════════════════════
            
            composable<ShiftList> {
                ShiftListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddShift = {
                        navController.navigate(ShiftForm())
                    },
                    onNavigateToEditShift = { shiftId ->
                        navController.navigate(ShiftForm(shiftId = shiftId))
                    }
                )
            }
            
            // Vertical slide — modal-style transition για φόρμες
            composable<ShiftForm>(
                enterTransition = {
                    fadeIn(tween(NAV_ANIM_DURATION)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
                },
                popExitTransition = {
                    fadeOut(tween(NAV_ANIM_DURATION_FAST)) + slideOutVertically(
                        targetOffsetY = { it / 3 },
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
                }
            ) {
                ShiftFormScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // ════════════════════════════════════════════════════
            // EXPENSE SCREENS
            // ════════════════════════════════════════════════════
            
            composable<ExpenseList> {
                com.deliverytracker.app.presentation.screens.expenses.ExpenseListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddExpense = {
                        navController.navigate(ExpenseForm())
                    },
                    onNavigateToEditExpense = { expenseId ->
                        navController.navigate(ExpenseForm(expenseId = expenseId))
                    }
                )
            }
            
            // Vertical slide — modal-style transition για φόρμες
            composable<ExpenseForm>(
                enterTransition = {
                    fadeIn(tween(NAV_ANIM_DURATION)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
                },
                popExitTransition = {
                    fadeOut(tween(NAV_ANIM_DURATION_FAST)) + slideOutVertically(
                        targetOffsetY = { it / 3 },
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
                }
            ) {
                com.deliverytracker.app.presentation.screens.expenses.ExpenseFormScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // ════════════════════════════════════════════════════
            // OTHER SCREENS
            // ════════════════════════════════════════════════════
            
            composable<Statistics> {
                com.deliverytracker.app.presentation.screens.statistics.StatisticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<Settings> {
                com.deliverytracker.app.presentation.screens.settings.SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPinSetup = { navController.navigate(PinSetup) }
                )
            }
            
            composable<RecycleBin> {
                com.deliverytracker.app.presentation.screens.recyclebin.RecycleBinScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<Export> {
                com.deliverytracker.app.presentation.screens.export.ExportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        } // NavHost
    } // Box
}
