package com.deliverytracker.app.domain.model

/**
 * Domain model για τον χρήστη.
 * Περιέχει τα βασικά στοιχεία που χρειάζεται η εφαρμογή.
 */
data class User(
    val id: String,
    val email: String,
    val username: String,
    val role: UserRole = UserRole.USER,
    val pinHash: String? = null,
    val pinSalt: String? = null,
    val failedPinAttempts: Int = 0,
    val pinLockoutEnd: Long? = null,
    val lastLoginAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Ελέγχει αν ο χρήστης είναι admin.
     */
    val isAdmin: Boolean
        get() = role == UserRole.ADMIN
    
    /**
     * Ελέγχει αν ο χρήστης έχει ορίσει PIN.
     */
    val hasPin: Boolean
        get() = !pinHash.isNullOrEmpty()
    
    /**
     * Ελέγχει αν το PIN είναι κλειδωμένο.
     */
    val isPinLocked: Boolean
        get() = pinLockoutEnd?.let { it > System.currentTimeMillis() } ?: false
}

/**
 * Ρόλοι χρήστη στην εφαρμογή.
 */
enum class UserRole {
    USER,
    ADMIN
}
