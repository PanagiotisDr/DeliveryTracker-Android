package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface για τα έξοδα.
 * Υλοποιείται στο Data layer με Firestore.
 */
interface ExpenseRepository {
    
    /**
     * Επιστρέφει τα έξοδα του χρήστη ως Flow (real-time updates).
     */
    fun getExpenses(userId: String, includeDeleted: Boolean = false): Flow<List<Expense>>
    
    /**
     * Επιστρέφει ένα συγκεκριμένο έξοδο.
     */
    suspend fun getExpenseById(expenseId: String): Result<Expense>
    
    /**
     * Δημιουργεί νέο έξοδο.
     */
    suspend fun createExpense(expense: Expense): Result<Expense>
    
    /**
     * Ενημερώνει υπάρχον έξοδο.
     */
    suspend fun updateExpense(expense: Expense): Result<Expense>
    
    /**
     * Soft delete - μετακινεί στον κάδο.
     */
    suspend fun softDeleteExpense(expenseId: String): Result<Unit>
    
    /**
     * Επαναφορά από τον κάδο.
     */
    suspend fun restoreExpense(expenseId: String): Result<Unit>
    
    /**
     * Μόνιμη διαγραφή.
     */
    suspend fun permanentDeleteExpense(expenseId: String): Result<Unit>
    
    /**
     * Επιστρέφει έξοδα για συγκεκριμένη χρονική περίοδο.
     */
    fun getExpensesByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>>
    
    /**
     * Επιστρέφει έξοδα ανά κατηγορία.
     */
    fun getExpensesByCategory(
        userId: String,
        category: ExpenseCategory
    ): Flow<List<Expense>>
    
    /**
     * Επιστρέφει τα διαγραμμένα έξοδα.
     */
    fun getDeletedExpenses(userId: String): Flow<List<Expense>>
}
