package com.deliverytracker.app.presentation.screens.export

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.repository.BackupRepository
import com.deliverytracker.app.domain.repository.ExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel για την οθόνη εξαγωγής δεδομένων.
 * Χρησιμοποιεί @StringRes για proper i18n.
 */
@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportRepository: ExportRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()
    
    /**
     * Εξαγωγή βαρδιών σε CSV.
     */
    fun exportShiftsToCsv(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessageResId = null, successMessageArg = null) }
            
            when (val result = exportRepository.exportShiftsToCsv(startDate, endDate)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            successMessageResId = R.string.msg_file_saved,
                            successMessageArg = result.data,
                            lastExportPath = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Εξαγωγή εξόδων σε CSV.
     */
    fun exportExpensesToCsv(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessageResId = null, successMessageArg = null) }
            
            when (val result = exportRepository.exportExpensesToCsv(startDate, endDate)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            successMessageResId = R.string.msg_file_saved,
                            successMessageArg = result.data,
                            lastExportPath = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Εξαγωγή αναφοράς.
     */
    fun exportReport(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessageResId = null, successMessageArg = null) }
            
            when (val result = exportRepository.exportReportToPdf(startDate, endDate)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            successMessageResId = R.string.msg_report_saved,
                            successMessageArg = result.data,
                            lastExportPath = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Δημιουργία backup.
     */
    fun createBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessageResId = null, successMessageArg = null) }
            
            when (val result = backupRepository.createBackup()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            successMessageResId = R.string.msg_file_saved,
                            successMessageArg = result.data,
                            lastExportPath = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessageResId = null, successMessageArg = null) }
    }
}

/**
 * UI State για την οθόνη εξαγωγής.
 * Χρησιμοποιεί @StringRes για success messages.
 */
data class ExportUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    @StringRes val successMessageResId: Int? = null,
    val successMessageArg: String? = null,
    val lastExportPath: String? = null
)
