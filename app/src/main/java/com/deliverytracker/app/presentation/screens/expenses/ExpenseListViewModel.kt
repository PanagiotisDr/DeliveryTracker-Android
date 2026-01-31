package com.deliverytracker.app.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State για τη λίστα εξόδων.
 */
data class ExpenseListUiState(
    val isLoading: Boolean = true,
    val expenses: List<Expense> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel για τη λίστα εξόδων.
 */
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    /**
     * Φορτώνει τα έξοδα του χρήστη.
     */
    private fun loadExpenses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            expenseRepository.getExpenses(userId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = "Σφάλμα φόρτωσης: ${e.message}")
                    }
                }
                .collect { expenses ->
                    _uiState.update { 
                        it.copy(isLoading = false, expenses = expenses)
                    }
                }
        }
    }
    
    /**
     * Διαγράφει ένα έξοδο (soft delete).
     */
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            // OPTIMISTIC UPDATE
            val currentExpenses = _uiState.value.expenses
            val deletedExpense = currentExpenses.find { it.id == expenseId }
            _uiState.update { 
                it.copy(
                    expenses = currentExpenses.filter { exp -> exp.id != expenseId },
                    successMessage = "Το έξοδο διαγράφηκε"
                )
            }
            
            when (val result = expenseRepository.softDeleteExpense(expenseId)) {
                is Result.Success -> { /* Ήδη ενημερωμένο */ }
                is Result.Error -> {
                    // ROLLBACK
                    if (deletedExpense != null) {
                        _uiState.update { 
                            it.copy(
                                expenses = (currentExpenses).sortedByDescending { e -> e.date },
                                error = result.message,
                                successMessage = null
                            )
                        }
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
