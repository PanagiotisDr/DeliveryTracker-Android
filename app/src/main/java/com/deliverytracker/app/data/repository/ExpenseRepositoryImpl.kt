package com.deliverytracker.app.data.repository

import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation του ExpenseRepository.
 */
@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExpenseRepository {
    
    private val expensesCollection = firestore.collection("expenses")
    
    override fun getExpenses(userId: String, includeDeleted: Boolean): Flow<List<Expense>> = callbackFlow {
        // Απλό query χωρίς composite index
        val query = expensesCollection
            .whereEqualTo("userId", userId)
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            var expenses = snapshot?.documents?.mapNotNull { doc ->
                doc.toExpense()
            } ?: emptyList()
            
            // Client-side filtering και sorting
            if (!includeDeleted) {
                expenses = expenses.filter { !it.isDeleted }
            }
            expenses = expenses.sortedByDescending { it.date }
            
            trySend(expenses)
        }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun getExpenseById(expenseId: String): Result<Expense> {
        return try {
            val doc = expensesCollection.document(expenseId).get().await()
            val expense = doc.toExpense()
            if (expense != null) {
                Result.Success(expense)
            } else {
                Result.Error("Δεν βρέθηκε το έξοδο")
            }
        } catch (e: Exception) {
            Result.Error("Σφάλμα φόρτωσης: ${e.message}", e)
        }
    }
    
    override suspend fun createExpense(expense: Expense): Result<Expense> {
        return try {
            val docRef = expensesCollection.document()
            val newExpense = expense.copy(
                id = docRef.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(newExpense.toMap()).await()
            Result.Success(newExpense)
        } catch (e: Exception) {
            Result.Error("Σφάλμα δημιουργίας: ${e.message}", e)
        }
    }
    
    override suspend fun updateExpense(expense: Expense): Result<Expense> {
        return try {
            val updatedExpense = expense.copy(updatedAt = System.currentTimeMillis())
            expensesCollection.document(expense.id)
                .set(updatedExpense.toMap())
                .await()
            Result.Success(updatedExpense)
        } catch (e: Exception) {
            Result.Error("Σφάλμα ενημέρωσης: ${e.message}", e)
        }
    }
    
    override suspend fun softDeleteExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection.document(expenseId)
                .update(mapOf(
                    "isDeleted" to true,
                    "deletedAt" to System.currentTimeMillis()
                ))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Σφάλμα διαγραφής: ${e.message}", e)
        }
    }
    
    override suspend fun restoreExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection.document(expenseId)
                .update(mapOf(
                    "isDeleted" to false,
                    "deletedAt" to null
                ))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Σφάλμα επαναφοράς: ${e.message}", e)
        }
    }
    
    override suspend fun permanentDeleteExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection.document(expenseId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Σφάλμα διαγραφής: ${e.message}", e)
        }
    }
    
    override fun getExpensesByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection
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
                
                val expenses = snapshot?.documents?.mapNotNull { it.toExpense() } ?: emptyList()
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    override fun getExpensesByCategory(
        userId: String,
        category: ExpenseCategory
    ): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDeleted", false)
            .whereEqualTo("category", category.name)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { it.toExpense() } ?: emptyList()
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    // ============ Helper Functions ============
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toExpense(): Expense? {
        return try {
            Expense(
                id = id,
                userId = getString("userId") ?: "",
                amount = getDouble("amount") ?: 0.0,
                category = try {
                    ExpenseCategory.valueOf(getString("category") ?: "OTHER")
                } catch (e: Exception) {
                    ExpenseCategory.OTHER
                },
                description = getString("description") ?: "",
                date = getLong("date") ?: System.currentTimeMillis(),
                shiftId = getString("shiftId"),
                receiptUrl = getString("receiptUrl"),
                isDeleted = getBoolean("isDeleted") ?: false,
                deletedAt = getLong("deletedAt"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun Expense.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "amount" to amount,
        "category" to category.name,
        "description" to description,
        "date" to date,
        "shiftId" to shiftId,
        "receiptUrl" to receiptUrl,
        "isDeleted" to isDeleted,
        "deletedAt" to deletedAt,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
