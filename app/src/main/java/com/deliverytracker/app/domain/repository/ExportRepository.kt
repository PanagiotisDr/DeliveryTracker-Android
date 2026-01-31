package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Result

/**
 * Repository interface για εξαγωγή δεδομένων.
 * Επιτρέπει export σε CSV και PDF.
 */
interface ExportRepository {
    
    /**
     * Εξαγωγή βαρδιών σε CSV.
     * @param startDate Αρχή περιόδου (Unix timestamp)
     * @param endDate Τέλος περιόδου (Unix timestamp)
     * @return Result με το path του αρχείου
     */
    suspend fun exportShiftsToCsv(startDate: Long, endDate: Long): Result<String>
    
    /**
     * Εξαγωγή εξόδων σε CSV.
     * @param startDate Αρχή περιόδου (Unix timestamp)
     * @param endDate Τέλος περιόδου (Unix timestamp)
     * @return Result με το path του αρχείου
     */
    suspend fun exportExpensesToCsv(startDate: Long, endDate: Long): Result<String>
    
    /**
     * Εξαγωγή αναφοράς σε PDF.
     * Περιλαμβάνει βάρδιες, έξοδα και σύνοψη.
     * @param startDate Αρχή περιόδου (Unix timestamp)
     * @param endDate Τέλος περιόδου (Unix timestamp)  
     * @return Result με το path του αρχείου
     */
    suspend fun exportReportToPdf(startDate: Long, endDate: Long): Result<String>
}
