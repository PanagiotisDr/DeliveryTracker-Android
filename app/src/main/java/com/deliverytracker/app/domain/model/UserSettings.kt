package com.deliverytracker.app.domain.model

/**
 * Ρυθμίσεις χρήστη.
 * Περιέχει στόχους, φορολογικές ρυθμίσεις και προτιμήσεις.
 */
data class UserSettings(
    val id: String = "",
    val userId: String = "",
    
    // ============ Θέμα Εμφάνισης ============
    val theme: ThemeMode = ThemeMode.SYSTEM,
    
    /** Dynamic Color (Material You) — χρώματα wallpaper αντί Warm Premium */
    val dynamicColor: Boolean = false,
    
    // ============ Φορολογικές Ρυθμίσεις ============
    
    /** Ποσοστό ΦΠΑ (default 24%) */
    val vatRate: Double = 0.24,
    
    /** Μηνιαία εισφορά ΕΦΚΑ σε ευρώ (default 254€) */
    val monthlyEfkaAmount: Double = 254.0,
    
    // ============ Στόχοι (σε Καθαρό Εισόδημα) ============
    
    /** Ημερήσιος στόχος σε ευρώ */
    val dailyGoal: Double? = null,
    
    /** Εβδομαδιαίος στόχος σε ευρώ */
    val weeklyGoal: Double? = null,
    
    /** Μηνιαίος στόχος σε ευρώ */
    val monthlyGoal: Double? = null,
    
    /** Ετήσιος στόχος σε ευρώ */
    val yearlyGoal: Double? = null,
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * ΦΠΑ σε μορφή % (π.χ. 24 αντί 0.24).
     */
    val vatPercentage: Double
        get() = vatRate * 100
    
    /**
     * Υπολογισμός εβδομαδιαίου στόχου από ημερήσιο (αν δεν έχει οριστεί).
     */
    val calculatedWeeklyGoal: Double?
        get() = weeklyGoal ?: dailyGoal?.times(7)
    
    /**
     * Υπολογισμός μηνιαίου στόχου από ημερήσιο (αν δεν έχει οριστεί).
     */
    val calculatedMonthlyGoal: Double?
        get() = monthlyGoal ?: dailyGoal?.times(22) // ~22 εργάσιμες μέρες
}

/**
 * Θέμα εμφάνισης.
 */
enum class ThemeMode {
    SYSTEM,  // Ακολουθεί το σύστημα
    LIGHT,   // Φωτεινό
    DARK     // Σκοτεινό
}
