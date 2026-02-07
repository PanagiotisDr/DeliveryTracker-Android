package com.deliverytracker.app.data.repository

import android.content.Context
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.PaymentMethod
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.ExpenseRepository
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
 * Firestore implementation του ExpenseRepository.
 * Χρησιμοποιεί context.getString() για proper i18n error messages.
 */
@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : ExpenseRepository {
    
    private val expensesCollection = firestore.collection("expenses")
    
    override fun getExpenses(userId: String, includeDeleted: Boolean): Flow<List<Expense>> = callbackFlow {
        // Server-side filtering με composite index
        // Απαιτεί το firestore.indexes.json να έχει deployed
        val query = if (includeDeleted) {
            expensesCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
        } else {
            expensesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isDeleted", false)
                .orderBy("date", Query.Direction.DESCENDING)
        }
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            val expenses = snapshot?.documents?.mapNotNull { doc ->
                doc.toExpense()
            } ?: emptyList()
            
            // Πλέον δεν χρειάζεται client-side filtering/sorting
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
                Result.Error(context.getString(R.string.error_expense_not_found))
            }
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_loading_data, e.message ?: ""), e)
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
            Result.Error(context.getString(R.string.error_creating, e.message ?: ""), e)
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
            Result.Error(context.getString(R.string.error_updating, e.message ?: ""), e)
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
            Result.Error(context.getString(R.string.error_deleting, e.message ?: ""), e)
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
            Result.Error(context.getString(R.string.error_restoring, e.message ?: ""), e)
        }
    }
    
    override suspend fun permanentDeleteExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection.document(expenseId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_deleting, e.message ?: ""), e)
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
    
    override fun getDeletedExpenses(userId: String): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDeleted", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { it.toExpense() }
                    ?.sortedByDescending { it.deletedAt }
                    ?: emptyList()
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
                date = getLong("date") ?: System.currentTimeMillis(),
                paymentMethod = try {
                    PaymentMethod.valueOf(getString("paymentMethod") ?: "CASH")
                } catch (e: Exception) {
                    PaymentMethod.CASH
                },
                notes = getString("notes") ?: "",
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
        "date" to date,
        "paymentMethod" to paymentMethod.name,
        "notes" to notes,
        "shiftId" to shiftId,
        "receiptUrl" to receiptUrl,
        "isDeleted" to isDeleted,
        "deletedAt" to deletedAt,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
