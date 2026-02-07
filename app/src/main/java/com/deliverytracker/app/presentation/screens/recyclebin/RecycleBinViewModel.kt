package com.deliverytracker.app.presentation.screens.recyclebin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State για τον κάδο ανακύκλωσης.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
data class RecycleBinUiState(
    val isLoading: Boolean = true,
    val deletedShifts: List<Shift> = emptyList(),
    val deletedExpenses: List<Expense> = emptyList(),
    @StringRes val successMessageResId: Int? = null,  // Static success messages
    @StringRes val errorResId: Int? = null,           // Static errors
    val errorMessage: String? = null                   // Dynamic errors
)

/**
 * ViewModel για τον κάδο ανακύκλωσης.
 */
@HiltViewModel
class RecycleBinViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RecycleBinUiState())
    val uiState: StateFlow<RecycleBinUiState> = _uiState.asStateFlow()
    
    init {
        loadDeletedItems()
    }
    
    /**
     * Φορτώνει τα διαγραμμένα αντικείμενα.
     */
    private fun loadDeletedItems() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            shiftRepository.getDeletedShifts(userId)
                .catch { e -> 
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { shifts ->
                    _uiState.update { 
                        it.copy(isLoading = false, deletedShifts = shifts)
                    }
                }
        }
        
        viewModelScope.launch {
            expenseRepository.getDeletedExpenses(userId)
                .catch { e -> 
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { expenses ->
                    _uiState.update { 
                        it.copy(isLoading = false, deletedExpenses = expenses)
                    }
                }
        }
    }
    
    /**
     * Επαναφορά βάρδιας.
     */
    fun restoreShift(shiftId: String) {
        viewModelScope.launch {
            when (val result = shiftRepository.restoreShift(shiftId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(successMessageResId = R.string.msg_shift_restored) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    /**
     * Επαναφορά εξόδου.
     */
    fun restoreExpense(expenseId: String) {
        viewModelScope.launch {
            when (val result = expenseRepository.restoreExpense(expenseId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(successMessageResId = R.string.msg_expense_restored) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    /**
     * Οριστική διαγραφή βάρδιας.
     */
    fun permanentDeleteShift(shiftId: String) {
        viewModelScope.launch {
            when (val result = shiftRepository.permanentDeleteShift(shiftId)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            successMessageResId = R.string.msg_shift_deleted_permanently,
                            deletedShifts = _uiState.value.deletedShifts.filter { s -> s.id != shiftId }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    /**
     * Οριστική διαγραφή εξόδου.
     */
    fun permanentDeleteExpense(expenseId: String) {
        viewModelScope.launch {
            when (val result = expenseRepository.permanentDeleteExpense(expenseId)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            successMessageResId = R.string.msg_expense_deleted_permanently,
                            deletedExpenses = _uiState.value.deletedExpenses.filter { e -> e.id != expenseId }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null, successMessageResId = null) }
    }
}
