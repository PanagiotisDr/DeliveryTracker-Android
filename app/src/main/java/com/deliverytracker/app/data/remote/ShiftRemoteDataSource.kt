package com.deliverytracker.app.data.remote

import com.deliverytracker.app.domain.model.Shift
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote Data Source για Shifts.
 * Αφαιρεί τη λογική Firestore από το Repository, επιτρέποντας
 * εύκολη αντικατάσταση με Room για offline support στο μέλλον.
 */
@Singleton
class ShiftRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val shiftsCollection = firestore.collection("shifts")
    
    /**
     * Παρακολουθεί τις βάρδιες του χρήστη σε realtime.
     */
    fun observeShifts(userId: String, includeDeleted: Boolean): Flow<List<ShiftDto>> = callbackFlow {
        val query = if (includeDeleted) {
            shiftsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
        } else {
            shiftsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isDeleted", false)
                .orderBy("date", Query.Direction.DESCENDING)
        }
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            val shifts = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(ShiftDto::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            
            trySend(shifts)
        }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Λαμβάνει μία βάρδια με ID.
     */
    suspend fun getShiftById(shiftId: String): ShiftDto? {
        val doc = shiftsCollection.document(shiftId).get().await()
        return doc.toObject(ShiftDto::class.java)?.copy(id = doc.id)
    }
    
    /**
     * Δημιουργεί νέα βάρδια.
     */
    suspend fun createShift(shift: ShiftDto): ShiftDto {
        val docRef = shiftsCollection.document()
        val newShift = shift.copy(
            id = docRef.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        docRef.set(newShift).await()
        return newShift
    }
    
    /**
     * Ενημερώνει υπάρχουσα βάρδια.
     */
    suspend fun updateShift(shift: ShiftDto) {
        shiftsCollection.document(shift.id)
            .set(shift.copy(updatedAt = System.currentTimeMillis()))
            .await()
    }
    
    /**
     * Διαγράφει βάρδια (soft delete).
     */
    suspend fun softDeleteShift(shiftId: String) {
        shiftsCollection.document(shiftId)
            .update(mapOf(
                "isDeleted" to true,
                "deletedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            ))
            .await()
    }
}

/**
 * DTO για Shift - χρησιμοποιείται από το Firestore.
 * Διαφέρει από το domain Shift για να διατηρήσει τον διαχωρισμό.
 */
data class ShiftDto(
    val id: String = "",
    val userId: String = "",
    val date: Long = 0,
    val workedMinutes: Int = 0,
    val grossIncome: Double = 0.0,
    val tips: Double = 0.0,
    val bonus: Double = 0.0,
    val ordersCount: Int = 0,
    val kilometers: Double = 0.0,
    val notes: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
