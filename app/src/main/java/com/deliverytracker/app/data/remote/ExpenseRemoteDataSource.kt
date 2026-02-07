package com.deliverytracker.app.data.remote

import com.deliverytracker.app.domain.model.ExpenseCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote Data Source για Expenses.
 * Αφαιρεί τη λογική Firestore από το Repository, επιτρέποντας
 * εύκολη αντικατάσταση με Room για offline support στο μέλλον.
 */
@Singleton
class ExpenseRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val expensesCollection = firestore.collection("expenses")
    
    /**
     * Παρακολουθεί τα έξοδα του χρήστη σε realtime.
     */
    fun observeExpenses(userId: String, includeDeleted: Boolean): Flow<List<ExpenseDto>> = callbackFlow {
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
                doc.toObject(ExpenseDto::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            
            trySend(expenses)
        }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Λαμβάνει ένα έξοδο με ID.
     */
    suspend fun getExpenseById(expenseId: String): ExpenseDto? {
        val doc = expensesCollection.document(expenseId).get().await()
        return doc.toObject(ExpenseDto::class.java)?.copy(id = doc.id)
    }
    
    /**
     * Δημιουργεί νέο έξοδο.
     */
    suspend fun createExpense(expense: ExpenseDto): ExpenseDto {
        val docRef = expensesCollection.document()
        val newExpense = expense.copy(
            id = docRef.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        docRef.set(newExpense).await()
        return newExpense
    }
    
    /**
     * Ενημερώνει υπάρχον έξοδο.
     */
    suspend fun updateExpense(expense: ExpenseDto) {
        expensesCollection.document(expense.id)
            .set(expense.copy(updatedAt = System.currentTimeMillis()))
            .await()
    }
    
    /**
     * Διαγράφει έξοδο (soft delete).
     */
    suspend fun softDeleteExpense(expenseId: String) {
        expensesCollection.document(expenseId)
            .update(mapOf(
                "isDeleted" to true,
                "deletedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            ))
            .await()
    }
}

/**
 * DTO για Expense - χρησιμοποιείται από το Firestore.
 */
data class ExpenseDto(
    val id: String = "",
    val userId: String = "",
    val date: Long = 0,
    val category: String = ExpenseCategory.OTHER.name,
    val amount: Double = 0.0,
    val paymentMethod: String = "CASH",
    val notes: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
