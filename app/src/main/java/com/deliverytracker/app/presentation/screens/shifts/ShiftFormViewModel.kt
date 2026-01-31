package com.deliverytracker.app.presentation.screens.shifts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel για την προσθήκη/επεξεργασία βάρδιας.
 */
@HiltViewModel
class ShiftFormViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Αν υπάρχει shiftId, είμαστε σε edit mode
    private val shiftId: String? = savedStateHandle.get<String>("shiftId")
    val isEditMode = shiftId != null
    
    private val _uiState = MutableStateFlow(ShiftFormUiState())
    val uiState: StateFlow<ShiftFormUiState> = _uiState.asStateFlow()
    
    init {
        if (isEditMode && shiftId != null) {
            loadShift(shiftId)
        } else {
            // Νέα βάρδια - set default τιμές
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date())
            _uiState.update { it.copy(dateText = today) }
        }
    }
    
    /**
     * Φορτώνει υπάρχουσα βάρδια για επεξεργασία.
     */
    private fun loadShift(shiftId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = shiftRepository.getShiftById(shiftId)) {
                is Result.Success -> {
                    val shift = result.data
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dateText = dateFormat.format(Date(shift.date)),
                            workedHours = if (shift.workedHours > 0) shift.workedHours.toString() else "",
                            workedMinutes = if (shift.workedMinutes > 0) shift.workedMinutes.toString() else "",
                            grossIncome = if (shift.grossIncome > 0) shift.grossIncome.toString().replace(".", ",") else "",
                            tips = if (shift.tips > 0) shift.tips.toString().replace(".", ",") else "",
                            bonus = if (shift.bonus > 0) shift.bonus.toString().replace(".", ",") else "",
                            fuelCost = if (shift.fuelCost > 0) shift.fuelCost.toString().replace(".", ",") else "",
                            otherExpenses = if (shift.otherExpenses > 0) shift.otherExpenses.toString().replace(".", ",") else "",
                            ordersCount = if (shift.ordersCount > 0) shift.ordersCount.toString() else "",
                            kilometers = if (shift.kilometers > 0) shift.kilometers.toString().replace(".", ",") else "",
                            notes = shift.notes,
                            existingShift = shift
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
    
    fun updateDate(date: String) {
        _uiState.update { it.copy(dateText = date) }
    }
    
    fun updateWorkedHours(value: String) {
        // Μόνο αριθμοί 0-23
        if (value.isEmpty() || (value.matches(Regex("^\\d{1,2}$")) && value.toIntOrNull()?.let { it in 0..23 } == true)) {
            _uiState.update { it.copy(workedHours = value) }
        }
    }
    
    fun updateWorkedMinutes(value: String) {
        // Μόνο αριθμοί 0-59
        if (value.isEmpty() || (value.matches(Regex("^\\d{1,2}$")) && value.toIntOrNull()?.let { it in 0..59 } == true)) {
            _uiState.update { it.copy(workedMinutes = value) }
        }
    }
    
    /**
     * Κανονικοποιεί decimal input - δέχεται τελεία ή κόμμα.
     */
    private fun normalizeDecimal(value: String): String {
        // Επιτρέπουμε αριθμούς, τελεία ή κόμμα
        return value.replace(".", ",")
    }
    
    private fun isValidDecimal(value: String): Boolean {
        if (value.isEmpty()) return true
        // Επιτρέπουμε αριθμούς με μία τελεία ή κόμμα
        return value.matches(Regex("^\\d*[.,]?\\d*$"))
    }
    
    fun updateGrossIncome(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(grossIncome = normalizeDecimal(value)) }
        }
    }
    
    fun updateTips(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(tips = normalizeDecimal(value)) }
        }
    }
    
    fun updateBonus(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(bonus = normalizeDecimal(value)) }
        }
    }
    
    fun updateFuelCost(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(fuelCost = normalizeDecimal(value)) }
        }
    }
    
    fun updateOtherExpenses(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(otherExpenses = normalizeDecimal(value)) }
        }
    }
    
    fun updateOrdersCount(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*$"))) {
            _uiState.update { it.copy(ordersCount = value) }
        }
    }
    
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }
    
    fun updateKilometers(value: String) {
        if (isValidDecimal(value)) {
            _uiState.update { it.copy(kilometers = normalizeDecimal(value)) }
        }
    }
    
    /**
     * Μετατρέπει string με κόμμα σε Double.
     */
    private fun parseDecimal(value: String): Double {
        return value.replace(",", ".").toDoubleOrNull() ?: 0.0
    }
    
    /**
     * Αποθηκεύει τη βάρδια.
     */
    fun saveShift() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validation
            if (state.workedHours.isEmpty() && state.workedMinutes.isEmpty()) {
                _uiState.update { it.copy(error = "Συμπλήρωσε τις ώρες εργασίας") }
                return@launch
            }
            
            if (state.grossIncome.isEmpty()) {
                _uiState.update { it.copy(error = "Συμπλήρωσε τα μικτά έσοδα") }
                return@launch
            }
            
            // Παίρνουμε το userId απευθείας από το FirebaseAuth
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _uiState.update { it.copy(error = "Δεν είστε συνδεδεμένος") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            // Parse date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = try {
                dateFormat.parse(state.dateText)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
            
            val shift = Shift(
                id = state.existingShift?.id ?: "",
                userId = userId,
                date = date,
                workedHours = state.workedHours.toIntOrNull() ?: 0,
                workedMinutes = state.workedMinutes.toIntOrNull() ?: 0,
                grossIncome = parseDecimal(state.grossIncome),
                tips = parseDecimal(state.tips),
                bonus = parseDecimal(state.bonus),
                fuelCost = parseDecimal(state.fuelCost),
                otherExpenses = parseDecimal(state.otherExpenses),
                ordersCount = state.ordersCount.toIntOrNull() ?: 0,
                kilometers = parseDecimal(state.kilometers),
                notes = state.notes,
                createdAt = state.existingShift?.createdAt ?: System.currentTimeMillis()
            )
            
            val result = if (isEditMode) {
                shiftRepository.updateShift(shift)
            } else {
                shiftRepository.createShift(shift)
            }
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isSaved = true)
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
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State για τη φόρμα βάρδιας.
 */
data class ShiftFormUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    
    // Form fields
    val dateText: String = "",
    val workedHours: String = "",
    val workedMinutes: String = "",
    val grossIncome: String = "",
    val tips: String = "",
    val bonus: String = "",           // Νέο field
    val fuelCost: String = "",
    val otherExpenses: String = "",
    val ordersCount: String = "",
    val kilometers: String = "",      // Νέο field
    val notes: String = "",
    
    // Για edit mode
    val existingShift: Shift? = null
)
