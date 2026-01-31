package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Result

/**
 * Repository interface για backup και restore.
 * Επιτρέπει export/import όλων των δεδομένων σε JSON.
 */
interface BackupRepository {
    
    /**
     * Δημιουργεί backup όλων των δεδομένων του χρήστη σε JSON.
     * @return Result με το path του αρχείου backup
     */
    suspend fun createBackup(): Result<String>
    
    /**
     * Επαναφέρει δεδομένα από backup αρχείο.
     * @param backupPath Το path του backup αρχείου
     * @return Result με τον αριθμό εγγραφών που επαναφέρθηκαν
     */
    suspend fun restoreBackup(backupPath: String): Result<Int>
    
    /**
     * Επιστρέφει λίστα με τα διαθέσιμα backups.
     * @return Result με λίστα paths
     */
    suspend fun getAvailableBackups(): Result<List<String>>
}
