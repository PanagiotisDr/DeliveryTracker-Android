package com.deliverytracker.app.presentation.screens.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.PaymentMethod
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * UI State για τη φόρμα εξόδου.
 */
data class ExpenseFormUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    
    // Form fields
    val dateText: String = "",
    val category: ExpenseCategory = ExpenseCategory.FUEL,
    val amount: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    
    // Για edit mode
    val existingExpense: Expense? = null
)

/**
 * ViewModel για τη φόρμα εξόδου.
 */
@HiltViewModel
class ExpenseFormViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val expenseId: String? = savedStateHandle["expenseId"]
    val isEditMode: Boolean = expenseId != null
    
    private val _uiState = MutableStateFlow(ExpenseFormUiState())
    val uiState: StateFlow<ExpenseFormUiState> = _uiState.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    init {
        if (isEditMode && expenseId != null) {
            loadExpense(expenseId)
        } else {
            // Default: σημερινή ημερομηνία
            _uiState.update { 
                it.copy(dateText = dateFormat.format(Date()))
            }
        }
    }
    
    /**
     * Φορτώνει έξοδο για επεξεργασία.
     */
    private fun loadExpense(expenseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = expenseRepository.getExpenseById(expenseId)) {
                is Result.Success -> {
                    val expense = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dateText = dateFormat.format(Date(expense.date)),
                            category = expense.category,
                            amount = if (expense.amount > 0) expense.amount.toString().replace(".", ",") else "",
                            paymentMethod = expense.paymentMethod,
                            notes = expense.notes,
                            existingExpense = expense
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    // ============ Input Handlers ============
    
    fun updateDate(value: String) {
        // Επιτρέπουμε μόνο αριθμούς και /
        val filtered = value.filter { it.isDigit() || it == '/' }
        if (filtered.length <= 10) {
            _uiState.update { it.copy(dateText = filtered) }
        }
    }
    
    fun updateCategory(category: ExpenseCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun updateAmount(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(amount = normalizeDecimal(value)) }
        }
    }
    
    fun updatePaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }
    
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }
    
    private fun normalizeDecimal(value: String): String {
        return value.replace(".", ",")
    }
    
    private fun isValidDecimal(value: String): Boolean {
        if (value.isEmpty()) return true
        return value.matches(Regex("^\\d*[.,]?\\d*$"))
    }
    
    private fun parseDecimal(value: String): Double {
        return value.replace(",", ".").toDoubleOrNull() ?: 0.0
    }
    
    /**
     * Αποθηκεύει το έξοδο.
     */
    fun saveExpense() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validation
            if (state.amount.isEmpty()) {
                _uiState.update { it.copy(error = "Συμπλήρωσε το ποσό") }
                return@launch
            }
            
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _uiState.update { it.copy(error = "Δεν είστε συνδεδεμένος") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            // Parse date - Ορίζουμε ώρα 12:00 noon για αποφυγή timezone bugs
            val date = try {
                val parsedDate = dateFormat.parse(state.dateText)
                if (parsedDate != null) {
                    val cal = Calendar.getInstance()
                    cal.time = parsedDate
                    cal.set(Calendar.HOUR_OF_DAY, 12)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    cal.timeInMillis
                } else {
                    System.currentTimeMillis()
                }
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
            
            val expense = Expense(
                id = state.existingExpense?.id ?: "",
                userId = userId,
                date = date,
                category = state.category,
                amount = parseDecimal(state.amount),
                paymentMethod = state.paymentMethod,
                notes = state.notes,
                createdAt = state.existingExpense?.createdAt ?: System.currentTimeMillis()
            )
            
            val result = if (isEditMode) {
                expenseRepository.updateExpense(expense)
            } else {
                expenseRepository.createExpense(expense)
            }
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
