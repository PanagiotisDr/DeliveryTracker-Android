package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface για authentication operations.
 * Υλοποιείται στο Data layer με Firebase.
 */
interface AuthRepository {
    
    /**
     * Επιστρέφει τον τρέχοντα χρήστη ως Flow.
     * Null αν δεν είναι συνδεδεμένος.
     */
    val currentUser: Flow<User?>
    
    /**
     * Επιστρέφει τον τρέχοντα χρήστη συγχρονικά.
     * Null αν δεν είναι συνδεδεμένος.
     */
    fun getCurrentUser(): User?
    
    /**
     * Επιστρέφει το userId του τρέχοντα χρήστη.
     * Null αν δεν είναι συνδεδεμένος.
     * Χρησιμοποιείται αντί του FirebaseAuth.getInstance().currentUser?.uid
     */
    fun getCurrentUserId(): String?
    
    /**
     * Ελέγχει αν υπάρχει συνδεδεμένος χρήστης.
     */
    val isLoggedIn: Boolean
    
    /**
     * Σύνδεση με email και password.
     */
    suspend fun login(email: String, password: String): Result<User>
    
    /**
     * Εγγραφή νέου χρήστη.
     */
    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<User>
    
    /**
     * Αποσύνδεση χρήστη.
     */
    suspend fun logout()
    
    /**
     * Αποστολή email για επαναφορά κωδικού.
     */
    suspend fun resetPassword(email: String): Result<Unit>
    
    /**
     * Ενημέρωση του PIN του χρήστη.
     */
    suspend fun updatePin(userId: String, pinHash: String?): Result<Unit>
    
    /**
     * Επαλήθευση PIN.
     */
    suspend fun verifyPin(userId: String, pinHash: String): Result<Boolean>
    
    /**
     * Καταγραφή αποτυχημένης προσπάθειας PIN.
     */
    suspend fun recordFailedPinAttempt(userId: String): Result<Int>
    
    /**
     * Επαναφορά PIN lockout.
     */
    suspend fun resetPinLockout(userId: String): Result<Unit>
    
    /**
     * Έλεγχος αν ο χρήστης είναι locked out.
     * Επιστρέφει τα εναπομείναντα milliseconds του lockout, ή 0 αν δεν είναι locked.
     */
    suspend fun checkPinLockout(userId: String): Result<Long>
}
