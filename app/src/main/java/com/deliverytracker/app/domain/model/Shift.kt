package com.deliverytracker.app.domain.model

/**
 * Domain model για μια βάρδια εργασίας.
 * Περιέχει όλα τα στοιχεία που καταγράφει ο διανομέας.
 * 
 * ΣΗΜΑΝΤΙΚΟ: Τα έξοδα (καύσιμα, συντήρηση κτλ) καταχωρούνται ξεχωριστά
 * μέσω του Expense entity για καλύτερη οργάνωση και κατηγοριοποίηση.
 */
data class Shift(
    val id: String = "",
    val userId: String = "",
    
    // Χρονικά στοιχεία
    val date: Long = System.currentTimeMillis(),
    val workedHours: Int = 0,      // Ώρες εργασίας
    val workedMinutes: Int = 0,    // Λεπτά εργασίας
    
    // Οικονομικά στοιχεία (μόνο έσοδα - τα έξοδα είναι στο Expense)
    val grossIncome: Double = 0.0,      // Μικτά έσοδα (πλατφόρμα)
    val tips: Double = 0.0,             // Φιλοδωρήματα
    val bonus: Double = 0.0,            // Bonus πλατφόρμας (peak hours, βροχή κτλ)
    
    // Στατιστικά
    val ordersCount: Int = 0,           // Αριθμός παραγγελιών
    val kilometers: Double = 0.0,       // Χιλιόμετρα που διανύθηκαν
    val kilometersStart: Double? = null, // Χιλιόμετρα - αρχή (legacy)
    val kilometersEnd: Double? = null,   // Χιλιόμετρα - τέλος (legacy)
    
    // Σημειώσεις
    val notes: String = "",
    
    // Soft delete
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Υπολογισμός καθαρών εσόδων.
     * Μικτά + Tips + Bonus (τα έξοδα υπολογίζονται ξεχωριστά από το Expense entity)
     */
    val netIncome: Double
        get() = grossIncome + tips + bonus
    
    /**
     * Alias για backward compatibility.
     * @see netIncome
     */
    @Deprecated("Χρησιμοποίησε netIncome αντί totalIncome", replaceWith = ReplaceWith("netIncome"))
    val totalIncome: Double
        get() = netIncome
    
    /**
     * Υπολογισμός χιλιομέτρων (νέο field ή legacy).
     */
    val totalKilometers: Double
        get() = if (kilometers > 0) {
            kilometers
        } else if (kilometersStart != null && kilometersEnd != null) {
            kilometersEnd - kilometersStart
        } else 0.0
    
    /**
     * Υπολογισμός ωρών εργασίας σε δεκαδική μορφή.
     */
    val hoursWorked: Double
        get() = workedHours + (workedMinutes / 60.0)
    
    /**
     * Μορφοποιημένη εμφάνιση ωρών (Ελληνικά μόνο - "8ω 30λ").
     * @deprecated Χρησιμοποίησε Shift.formattedWorkTime(context) από presentation layer
     */
    @Deprecated("Hardcoded ελληνικά - χρησιμοποίησε Shift.formattedWorkTime(context)")
    val formattedWorkTime: String
        get() = "${workedHours}ω ${workedMinutes}λ"
    
    /**
     * Υπολογισμός εσόδων ανά ώρα.
     */
    val incomePerHour: Double
        get() = if (hoursWorked > 0) netIncome / hoursWorked else 0.0
    
    /**
     * Υπολογισμός εσόδων ανά παραγγελία.
     */
    val incomePerOrder: Double
        get() = if (ordersCount > 0) netIncome / ordersCount else 0.0
    
    /**
     * Υπολογισμός εσόδων ανά χιλιόμετρο.
     */
    val incomePerKm: Double
        get() = if (totalKilometers > 0) netIncome / totalKilometers else 0.0
    
    /**
     * Υπολογισμός ΦΠΑ (επί εσόδων + bonus, ΟΧΙ επί tips).
     */
    fun calculateVAT(vatRate: Double = 0.24): Double {
        return (grossIncome + bonus) * vatRate
    }
    
    /**
     * Μεικτό εισόδημα με ΦΠΑ.
     */
    fun grossIncomeWithVAT(vatRate: Double = 0.24): Double {
        return netIncome + calculateVAT(vatRate)
    }
}
