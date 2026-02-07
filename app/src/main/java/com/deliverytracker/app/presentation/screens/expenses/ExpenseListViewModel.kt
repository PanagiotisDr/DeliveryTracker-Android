package com.deliverytracker.app.presentation.screens.expenses

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State για τη λίστα εξόδων.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class ExpenseListUiState(
    val isLoading: Boolean = true,
    val expenses: List<Expense> = emptyList(),
    @StringRes val errorResId: Int? = null,          // Static errors
    val errorMessage: String? = null,                 // Dynamic errors
    @StringRes val successMessageResId: Int? = null  // Static success messages
)

/**
 * ViewModel για τη λίστα εξόδων.
 */
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
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
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            expenseRepository.getExpenses(userId)
                .catch { e ->
                    // Dynamic error με context
                    _uiState.update { 
                        it.copy(isLoading = false, errorMessage = e.message)
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
                    successMessageResId = R.string.msg_expense_deleted
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
                                errorMessage = result.message,
                                successMessageResId = null
                            )
                        }
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null, successMessageResId = null) }
    }
}
