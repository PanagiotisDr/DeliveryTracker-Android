package com.deliverytracker.app.domain.model

/**
 * Domain model για μια βάρδια εργασίας.
 * Περιέχει όλα τα στοιχεία που καταγράφει ο διανομέας.
 */
data class Shift(
    val id: String = "",
    val userId: String = "",
    
    // Χρονικά στοιχεία
    val date: Long = System.currentTimeMillis(),
    val workedHours: Int = 0,      // Ώρες εργασίας
    val workedMinutes: Int = 0,    // Λεπτά εργασίας
    
    // Οικονομικά στοιχεία
    val grossIncome: Double = 0.0,      // Μικτά έσοδα (πλατφόρμα)
    val tips: Double = 0.0,             // Φιλοδωρήματα
    val bonus: Double = 0.0,            // Bonus πλατφόρμας (peak hours, βροχή κτλ) ⭐ ΝΕΟ
    val fuelCost: Double = 0.0,         // Κόστος καυσίμων
    val otherExpenses: Double = 0.0,    // Άλλα έξοδα
    
    // Στατιστικά
    val ordersCount: Int = 0,           // Αριθμός παραγγελιών
    val kilometers: Double = 0.0,       // Χιλιόμετρα που διανύθηκαν ⭐ ΝΕΟ (απλοποιημένο)
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
     * Μικτά + Tips + Bonus - Καύσιμα - Άλλα έξοδα
     */
    val netIncome: Double
        get() = grossIncome + tips + bonus - fuelCost - otherExpenses
    
    /**
     * Υπολογισμός συνολικών εσόδων (μικτά + tips + bonus).
     */
    val totalIncome: Double
        get() = grossIncome + tips + bonus
    
    /**
     * Υπολογισμός συνολικών εξόδων.
     */
    val totalExpenses: Double
        get() = fuelCost + otherExpenses
    
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
     * Μορφοποιημένη εμφάνιση ωρών (π.χ. "8ω 30λ").
     */
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
