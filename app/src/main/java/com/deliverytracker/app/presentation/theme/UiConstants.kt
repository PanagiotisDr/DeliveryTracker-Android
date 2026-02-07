package com.deliverytracker.app.presentation.theme
import androidx.compose.ui.unit.sp

/**
 * Κεντρικό σημείο για όλα τα UI constants που δεν είναι χρώματα/διαστάσεις
 * 
 * Ακολουθεί το Zero Hardcode Policy - όλα τα emojis και magic numbers
 * ορίζονται εδώ για εύκολη συντήρηση
 * 
 * @author DeliveryTracker Team
 * @version 1.0.0
 */

// ══════════════════════════════════════════════════════════════════
// EMOJIS - Κεντρική διαχείριση
// ══════════════════════════════════════════════════════════════════

/**
 * Όλα τα emojis που χρησιμοποιούνται στην εφαρμογή
 * Για εύκολη αλλαγή και consistency
 */
object Emojis {
    // Dashboard & Stats
    const val ORDERS = "📦"
    const val TIME = "⏱️"
    const val MONEY = "💰"
    const val SHIFTS = "📋"
    const val GOAL_REACHED = "🎯"
    const val GOAL_PROGRESS = "💪"
    
    // Expense Categories
    const val FUEL = "⛽"
    const val MAINTENANCE = "🔧"
    const val INSURANCE = "🛡️"
    const val TAX = "🏛️"
    const val EQUIPMENT = "🎒"
    const val PHONE = "📱"
    const val ROAD_TAX = "🛣️"
    const val KTEO = "🚗"
    const val FINES = "⚠️"
    const val OTHER = "📌"
    
    // Status & Feedback
    const val SUCCESS = "✅"
    const val ERROR = "❌"
    const val WARNING = "⚠️"
    const val INFO = "ℹ️"
    const val LOADING = "⏳"
    
    // Actions
    const val ADD = "➕"
    const val EDIT = "✏️"
    const val DELETE = "🗑️"
    const val SAVE = "💾"
    const val EXPORT = "📤"
    const val IMPORT = "📥"
    
    // Navigation
    const val HOME = "🏠"
    const val SETTINGS = "⚙️"
    const val STATS = "📊"
    const val CALENDAR = "📅"
}

// ══════════════════════════════════════════════════════════════════
// BUSINESS RULES - Magic Numbers
// ══════════════════════════════════════════════════════════════════

/**
 * Business rules και limits
 * Όλα τα magic numbers σε ένα σημείο
 */
object BusinessRules {
    // Authentication
    const val MIN_PASSWORD_LENGTH = 6
    const val PIN_LENGTH = 4
    const val MAX_LOGIN_ATTEMPTS = 5
    
    // Shifts
    const val MAX_SHIFTS_PER_DAY = 3
    const val DEFAULT_DAILY_GOAL = 100.0
    const val MAX_HOURS_PER_SHIFT = 24
    const val MAX_SHIFT_MINUTES_PER_DAY = 1440  // 24 ώρες × 60 λεπτά
    
    // Expenses
    const val MAX_EXPENSE_AMOUNT = 10000.0
    const val EXPENSE_DECIMAL_PLACES = 2
    
    // Statistics
    const val STATS_DAYS_DEFAULT = 30
    const val CHART_MAX_ITEMS = 7
    
    // Performance thresholds (καθαρά κέρδη)
    const val INCOME_THRESHOLD_HIGH = 100.0   // Πράσινο
    const val INCOME_THRESHOLD_MEDIUM = 50.0  // Κίτρινο
    // κάτω από MEDIUM = κόκκινο
}

// ══════════════════════════════════════════════════════════════════
// FORMAT PATTERNS - Date/Number formatting
// ══════════════════════════════════════════════════════════════════

/**
 * Patterns για formatting
 * Χρησιμοποιούνται με DateTimeFormatter και NumberFormat
 */
object FormatPatterns {
    // Dates
    const val DATE_DISPLAY = "dd/MM/yyyy"
    const val DATE_SHORT = "dd/MM"
    const val TIME_DISPLAY = "HH:mm"
    const val DATETIME_DISPLAY = "dd/MM/yyyy HH:mm"
    const val MONTH_YEAR = "MMMM yyyy"
    const val DAY_OF_WEEK = "EEEE"
    
    // Numbers
    const val CURRENCY_SYMBOL = "€"
    const val DECIMAL_SEPARATOR = ","
    const val CURRENCY_DECIMAL_PLACES = 2
}

// ══════════════════════════════════════════════════════════════════
// TYPOGRAPHY TOKENS - Letter spacing, line height
// ══════════════════════════════════════════════════════════════════

/**
 * Typography tokens για consistent styling
 */
object TypographyTokens {
    /** Wide letter spacing for labels: 2.sp */
    val letterSpacingWide = 2.sp
    
    /** Normal letter spacing: 0.sp */
    val letterSpacingNormal = 0.sp
    
    /** Tight letter spacing: -0.5.sp */
    val letterSpacingTight = (-0.5).sp
}
