package com.deliverytracker.app.domain.model

/**
 * Sealed class για τα αποτελέσματα ασύγχρονων λειτουργιών.
 * Χρησιμοποιείται για type-safe error handling.
 */
sealed class Result<out T> {
    /**
     * Επιτυχές αποτέλεσμα με δεδομένα.
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Αποτυχία με μήνυμα σφάλματος.
     */
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()
    
    /**
     * Κατάσταση φόρτωσης.
     */
    data object Loading : Result<Nothing>()
    
    /**
     * Helper για έλεγχο αν είναι επιτυχία.
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Helper για έλεγχο αν είναι σφάλμα.
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Επιστρέφει τα δεδομένα αν είναι επιτυχία, αλλιώς null.
     */
    fun getOrNull(): T? = (this as? Success)?.data
}
