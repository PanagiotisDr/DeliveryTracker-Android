package com.deliverytracker.app.presentation.screens.shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για τη λίστα βαρδιών.
 */
@HiltViewModel
class ShiftListViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ShiftListUiState())
    val uiState: StateFlow<ShiftListUiState> = _uiState.asStateFlow()
    
    init {
        loadShifts()
    }
    
    /**
     * Φορτώνει τις βάρδιες του χρήστη.
     */
    private fun loadShifts() {
        viewModelScope.launch {
            // Παίρνουμε το userId μέσω AuthRepository
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(error = "error_not_logged_in") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            shiftRepository.getShifts(userId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "error_loading"
                        )
                    }
                }
                .collect { shifts ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            shifts = shifts,
                            error = null
                        )
                    }
                }
        }
    }
    
    /**
     * Διαγράφει μια βάρδια (soft delete).
     * Χρησιμοποιεί optimistic update για instant UI feedback.
     */
    fun deleteShift(shiftId: String) {
        viewModelScope.launch {
            // OPTIMISTIC UPDATE: Αφαίρεσε από UI αμέσως
            val currentShifts = _uiState.value.shifts
            val deletedShift = currentShifts.find { it.id == shiftId }
            _uiState.update { 
                it.copy(
                    shifts = currentShifts.filter { shift -> shift.id != shiftId },
                    successMessage = "msg_shift_deleted"
                )
            }
            
            // Κάνε το actual delete στο background
            when (val result = shiftRepository.softDeleteShift(shiftId)) {
                is Result.Success -> {
                    // Ήδη ενημερωμένο το UI
                }
                is Result.Error -> {
                    // ROLLBACK: Επαναφορά αν αποτύχει
                    if (deletedShift != null) {
                        _uiState.update { 
                            it.copy(
                                shifts = (currentShifts).sortedByDescending { s -> s.date },
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
    
    /**
     * Καθαρίζει τα μηνύματα.
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

/**
 * UI State για τη λίστα βαρδιών.
 */
data class ShiftListUiState(
    val isLoading: Boolean = false,
    val shifts: List<Shift> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)
