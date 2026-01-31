package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface για τις βάρδιες.
 * Υλοποιείται στο Data layer με Firestore.
 */
interface ShiftRepository {
    
    /**
     * Επιστρέφει τις βάρδιες του χρήστη ως Flow (real-time updates).
     * @param userId ID του χρήστη
     * @param includeDeleted Αν θα συμπεριληφθούν οι διαγραμμένες
     */
    fun getShifts(userId: String, includeDeleted: Boolean = false): Flow<List<Shift>>
    
    /**
     * Επιστρέφει μια συγκεκριμένη βάρδια.
     */
    suspend fun getShiftById(shiftId: String): Result<Shift>
    
    /**
     * Δημιουργεί νέα βάρδια.
     */
    suspend fun createShift(shift: Shift): Result<Shift>
    
    /**
     * Ενημερώνει υπάρχουσα βάρδια.
     */
    suspend fun updateShift(shift: Shift): Result<Shift>
    
    /**
     * Soft delete - μετακινεί στον κάδο.
     */
    suspend fun softDeleteShift(shiftId: String): Result<Unit>
    
    /**
     * Επαναφορά από τον κάδο.
     */
    suspend fun restoreShift(shiftId: String): Result<Unit>
    
    /**
     * Μόνιμη διαγραφή.
     */
    suspend fun permanentDeleteShift(shiftId: String): Result<Unit>
    
    /**
     * Επιστρέφει βάρδιες για συγκεκριμένη χρονική περίοδο.
     */
    fun getShiftsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Shift>>
}
