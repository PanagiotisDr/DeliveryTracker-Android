package com.deliverytracker.app.data.repository

import android.content.Context
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation του ShiftRepository.
 * Χρησιμοποιεί context.getString() για proper i18n error messages.
 */
@Singleton
class ShiftRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : ShiftRepository {
    
    private val shiftsCollection = firestore.collection("shifts")
    
    override fun getShifts(userId: String, includeDeleted: Boolean): Flow<List<Shift>> = callbackFlow {
        // Server-side filtering με composite index
        // Απαιτεί το firestore.indexes.json να έχει deployed
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
                doc.toShift()
            } ?: emptyList()
            
            // Πλέον δεν χρειάζεται client-side filtering/sorting
            trySend(shifts)
        }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun getShiftById(shiftId: String): Result<Shift> {
        return try {
            val doc = shiftsCollection.document(shiftId).get().await()
            val shift = doc.toShift()
            if (shift != null) {
                Result.Success(shift)
            } else {
                Result.Error(context.getString(R.string.error_shift_not_found))
            }
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_loading_data, e.message ?: ""), e)
        }
    }
    
    override suspend fun createShift(shift: Shift): Result<Shift> {
        return try {
            val docRef = shiftsCollection.document()
            val newShift = shift.copy(
                id = docRef.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(newShift.toMap()).await()
            Result.Success(newShift)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_creating, e.message ?: ""), e)
        }
    }
    
    override suspend fun updateShift(shift: Shift): Result<Shift> {
        return try {
            val updatedShift = shift.copy(updatedAt = System.currentTimeMillis())
            shiftsCollection.document(shift.id)
                .set(updatedShift.toMap())
                .await()
            Result.Success(updatedShift)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_updating, e.message ?: ""), e)
        }
    }
    
    override suspend fun softDeleteShift(shiftId: String): Result<Unit> {
        return try {
            shiftsCollection.document(shiftId)
                .update(mapOf(
                    "isDeleted" to true,
                    "deletedAt" to System.currentTimeMillis()
                ))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_deleting, e.message ?: ""), e)
        }
    }
    
    override suspend fun restoreShift(shiftId: String): Result<Unit> {
        return try {
            shiftsCollection.document(shiftId)
                .update(mapOf(
                    "isDeleted" to false,
                    "deletedAt" to null
                ))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_restoring, e.message ?: ""), e)
        }
    }
    
    override suspend fun permanentDeleteShift(shiftId: String): Result<Unit> {
        return try {
            shiftsCollection.document(shiftId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_deleting, e.message ?: ""), e)
        }
    }
    
    override fun getShiftsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Shift>> = callbackFlow {
        val listener = shiftsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDeleted", false)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val shifts = snapshot?.documents?.mapNotNull { it.toShift() } ?: emptyList()
                trySend(shifts)
            }
        
        awaitClose { listener.remove() }
    }
    
    override fun getDeletedShifts(userId: String): Flow<List<Shift>> = callbackFlow {
        val listener = shiftsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDeleted", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val shifts = snapshot?.documents?.mapNotNull { it.toShift() }
                    ?.sortedByDescending { it.deletedAt }
                    ?: emptyList()
                trySend(shifts)
            }
        
        awaitClose { listener.remove() }
    }
    
    // ============ Helper Functions ============
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toShift(): Shift? {
        return try {
            Shift(
                id = id,
                userId = getString("userId") ?: "",
                date = getLong("date") ?: System.currentTimeMillis(),
                workedHours = getLong("workedHours")?.toInt() ?: 0,
                workedMinutes = getLong("workedMinutes")?.toInt() ?: 0,
                grossIncome = getDouble("grossIncome") ?: 0.0,
                tips = getDouble("tips") ?: 0.0,
                bonus = getDouble("bonus") ?: 0.0,
                ordersCount = getLong("ordersCount")?.toInt() ?: 0,
                kilometers = getDouble("kilometers") ?: 0.0,
                kilometersStart = getDouble("kilometersStart"),
                kilometersEnd = getDouble("kilometersEnd"),
                notes = getString("notes") ?: "",
                isDeleted = getBoolean("isDeleted") ?: false,
                deletedAt = getLong("deletedAt"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun Shift.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "date" to date,
        "workedHours" to workedHours,
        "workedMinutes" to workedMinutes,
        "grossIncome" to grossIncome,
        "tips" to tips,
        "bonus" to bonus,
        "ordersCount" to ordersCount,
        "kilometers" to kilometers,
        "kilometersStart" to kilometersStart,
        "kilometersEnd" to kilometersEnd,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "deletedAt" to deletedAt,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
