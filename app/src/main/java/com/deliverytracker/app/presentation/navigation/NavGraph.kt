package com.deliverytracker.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.deliverytracker.app.presentation.screens.auth.LoginScreen
import com.deliverytracker.app.presentation.screens.auth.PinLoginScreen
import com.deliverytracker.app.presentation.screens.auth.PinSetupScreen
import com.deliverytracker.app.presentation.screens.auth.RegisterScreen
import com.deliverytracker.app.presentation.screens.common.ComingSoonScreen
import com.deliverytracker.app.presentation.screens.dashboard.DashboardScreen
import com.deliverytracker.app.presentation.screens.shifts.ShiftFormScreen
import com.deliverytracker.app.presentation.screens.shifts.ShiftListScreen
import com.google.firebase.auth.FirebaseAuth

/**
 * Κύριο Navigation Graph της εφαρμογής.
 * Διαχειρίζεται τη μετάβαση μεταξύ οθονών.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    hasPin: Boolean = false
) {
    // Αν είναι συνδεδεμένος και έχει PIN -> PinLogin
    // Αν είναι συνδεδεμένος χωρίς PIN -> Dashboard
    // Αν δεν είναι συνδεδεμένος -> Login
    val startDestination = when {
        isLoggedIn && hasPin -> Screen.PinLogin.route
        isLoggedIn -> Screen.Dashboard.route
        else -> Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ============ Auth Screens ============
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNeedsPinSetup = {
                    navController.navigate(Screen.PinSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Μετά την εγγραφή, πήγαινε στο PIN setup
                    navController.navigate(Screen.PinSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // ============ PIN Screens ============
        
        composable(Screen.PinLogin.route) {
            PinLoginScreen(
                onPinSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.PinLogin.route) { inclusive = true }
                    }
                },
                onUsePassword = {
                    // Αποσύνδεση για να πάει στο κανονικό login
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.PinLogin.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.PinSetup.route) {
            PinSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                }
            )
        }
        
        // ============ Main Screens ============
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToShifts = {
                    navController.navigate(Screen.ShiftList.route)
                },
                onNavigateToExpenses = {
                    navController.navigate(Screen.ExpenseList.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }
        
        // ============ Shift Screens ============
        
        composable(Screen.ShiftList.route) {
            ShiftListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddShift = { 
                    navController.navigate(Screen.ShiftForm.createRoute()) 
                },
                onNavigateToEditShift = { shiftId ->
                    navController.navigate(Screen.ShiftForm.createRoute(shiftId))
                }
            )
        }
        
        composable(
            route = Screen.ShiftForm.route,
            arguments = listOf(
                navArgument("shiftId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ShiftFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ============ Expense Screens ============
        
        composable(Screen.ExpenseList.route) {
            com.deliverytracker.app.presentation.screens.expenses.ExpenseListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddExpense = {
                    navController.navigate(Screen.ExpenseForm.createRoute())
                },
                onNavigateToEditExpense = { expenseId ->
                    navController.navigate(Screen.ExpenseForm.createRoute(expenseId))
                }
            )
        }
        
        composable(
            route = Screen.ExpenseForm.route,
            arguments = listOf(
                androidx.navigation.navArgument("expenseId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            com.deliverytracker.app.presentation.screens.expenses.ExpenseFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Statistics.route) {
            com.deliverytracker.app.presentation.screens.statistics.StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            com.deliverytracker.app.presentation.screens.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RecycleBin.route) {
            com.deliverytracker.app.presentation.screens.recyclebin.RecycleBinScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
