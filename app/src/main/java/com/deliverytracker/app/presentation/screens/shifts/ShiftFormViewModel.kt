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
        return value.replace(".", ",")
    }
    
    /**
     * Ελέγχει αν είναι έγκυρο decimal με max δεκαδικά ψηφία.
     * @param maxDecimals Μέγιστος αριθμός δεκαδικών ψηφίων (2 για €, 1 για km)
     */
    private fun isValidDecimal(value: String, maxDecimals: Int = 2): Boolean {
        if (value.isEmpty()) return true
        // Regex: αριθμοί με μία τελεία/κόμμα και max X δεκαδικά
        val pattern = "^\\d*[.,]?\\d{0,$maxDecimals}$"
        return value.matches(Regex(pattern))
    }
    
    fun updateGrossIncome(value: String) {
        // Max 2 δεκαδικά για ευρώ
        if (isValidDecimal(value, 2)) {
            _uiState.update { it.copy(grossIncome = normalizeDecimal(value)) }
        }
    }
    
    fun updateTips(value: String) {
        // Max 2 δεκαδικά για ευρώ
        if (isValidDecimal(value, 2)) {
            _uiState.update { it.copy(tips = normalizeDecimal(value)) }
        }
    }
    
    fun updateBonus(value: String) {
        // Max 2 δεκαδικά για ευρώ
        if (isValidDecimal(value, 2)) {
            _uiState.update { it.copy(bonus = normalizeDecimal(value)) }
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
        // Max 1 δεκαδικό για χιλιόμετρα
        if (isValidDecimal(value, 1)) {
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
     * Αποθηκεύει τη βάρδια με αυστηρό validation.
     */
    fun saveShift() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // ========== STRICT VALIDATION ==========
            
            // 1. Έλεγχος μηδενικών εσόδων
            val grossIncome = parseDecimal(state.grossIncome)
            val tips = parseDecimal(state.tips)
            val bonus = parseDecimal(state.bonus)
            val totalIncome = grossIncome + tips + bonus
            
            if (totalIncome <= 0) {
                _uiState.update { it.copy(error = "error_zero_income") }
                return@launch
            }
            
            // 2. Έλεγχος διάρκειας - αν έχουμε έσοδα, πρέπει να έχουμε διάρκεια
            val hours = state.workedHours.toIntOrNull() ?: 0
            val minutes = state.workedMinutes.toIntOrNull() ?: 0
            val totalMinutes = hours * 60 + minutes
            
            if (totalMinutes == 0 && totalIncome > 0) {
                _uiState.update { it.copy(error = "error_zero_duration") }
                return@launch
            }
            
            // 3. Έλεγχος μέγιστης διάρκειας (24 ώρες = 1440 λεπτά)
            if (totalMinutes > 1440) {
                _uiState.update { it.copy(error = "error_over_24_hours") }
                return@launch
            }
            
            // 4. Έλεγχος μελλοντικής ημερομηνίας
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Σημαντικό: Ορίζουμε ρητά το timezone για να αποφύγουμε offset bugs
            dateFormat.timeZone = java.util.TimeZone.getDefault()
            
            val shiftDate = try {
                // Parse την ημερομηνία και στη συνέχεια ορίζουμε ώρα στις 12:00 noon
                // για να αποφύγουμε day boundary bugs με timezones
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
            
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis
            
            if (shiftDate > todayStart) {
                _uiState.update { it.copy(error = "error_future_date") }
                return@launch
            }
            
            // 5. Παραγγελίες υποχρεωτικές αν υπάρχουν έσοδα
            val ordersCount = state.ordersCount.toIntOrNull() ?: 0
            
            if (ordersCount == 0 && totalIncome > 0) {
                _uiState.update { it.copy(error = "error_zero_orders") }
                return@launch
            }
            
            // 6. Χιλιόμετρα υποχρεωτικά αν υπάρχουν παραγγελίες
            val kilometers = parseDecimal(state.kilometers)
            
            if (kilometers <= 0 && ordersCount > 0) {
                _uiState.update { it.copy(error = "error_zero_km") }
                return@launch
            }
            
            // ========== END VALIDATION ==========
            
            // Παίρνουμε το userId απευθείας από το FirebaseAuth
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _uiState.update { it.copy(error = "Δεν είστε συνδεδεμένος") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            val shift = Shift(
                id = state.existingShift?.id ?: "",
                userId = userId,
                date = shiftDate,
                workedHours = hours,
                workedMinutes = minutes,
                grossIncome = grossIncome,
                tips = tips,
                bonus = bonus,
                ordersCount = ordersCount,
                kilometers = kilometers,
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
    val bonus: String = "",
    val ordersCount: String = "",
    val kilometers: String = "",
    val notes: String = "",
    
    // Για edit mode
    val existingShift: Shift? = null
)
