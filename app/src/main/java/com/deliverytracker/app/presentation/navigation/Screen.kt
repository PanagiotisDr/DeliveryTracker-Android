package com.deliverytracker.app.presentation.navigation

/**
 * Ορισμός όλων των routes της εφαρμογής.
 * Χρησιμοποιείται για type-safe navigation.
 */
sealed class Screen(val route: String) {
    // Auth screens
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object PinLogin : Screen("pin_login")
    data object PinSetup : Screen("pin_setup")
    
    // Main screens
    data object Dashboard : Screen("dashboard")
    data object ShiftList : Screen("shifts")
    data object ShiftForm : Screen("shift_form?shiftId={shiftId}") {
        fun createRoute(shiftId: String? = null) = 
            if (shiftId != null) "shift_form?shiftId=$shiftId" else "shift_form"
    }
    data object ExpenseList : Screen("expenses")
    data object ExpenseForm : Screen("expense_form?expenseId={expenseId}") {
        fun createRoute(expenseId: String? = null) = 
            if (expenseId != null) "expense_form?expenseId=$expenseId" else "expense_form"
    }
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object RecycleBin : Screen("recycle_bin")
    data object Export : Screen("export")
}
