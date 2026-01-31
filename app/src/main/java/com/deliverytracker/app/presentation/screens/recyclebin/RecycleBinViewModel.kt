package com.deliverytracker.app.presentation.screens.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.google.firebase.auth.FirebaseAuth
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
 */
data class RecycleBinUiState(
    val isLoading: Boolean = true,
    val deletedShifts: List<Shift> = emptyList(),
    val deletedExpenses: List<Expense> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)

/**
 * ViewModel για τον κάδο ανακύκλωσης.
 */
@HiltViewModel
class RecycleBinViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            shiftRepository.getDeletedShifts(userId)
                .catch { e -> 
                    _uiState.update { it.copy(error = "Σφάλμα: ${e.message}") }
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
                    _uiState.update { it.copy(error = "Σφάλμα: ${e.message}") }
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
                    _uiState.update { it.copy(successMessage = "Η βάρδια επαναφέρθηκε") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
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
                    _uiState.update { it.copy(successMessage = "Το έξοδο επαναφέρθηκε") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
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
                            successMessage = "Η βάρδια διαγράφηκε οριστικά",
                            deletedShifts = _uiState.value.deletedShifts.filter { s -> s.id != shiftId }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
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
                            successMessage = "Το έξοδο διαγράφηκε οριστικά",
                            deletedExpenses = _uiState.value.deletedExpenses.filter { e -> e.id != expenseId }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Result.Loading -> { /* Ignore */ }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
