package com.deliverytracker.app.data.repository

import android.content.Context
import android.os.Environment
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.ExpenseCategory
import com.deliverytracker.app.domain.model.PaymentMethod
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.BackupRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του BackupRepository.
 * Δημιουργεί JSON backups όλων των δεδομένων του χρήστη.
 */
@Singleton
class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository
) : BackupRepository {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    
    /**
     * Δημιουργεί backup όλων των δεδομένων σε JSON.
     */
    override suspend fun createBackup(): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.Error("Δεν είστε συνδεδεμένος")
            
            // Λήψη όλων των δεδομένων
            val shifts = shiftRepository.getShifts(userId, includeDeleted = false).first()
            val expenses = expenseRepository.getExpenses(userId, includeDeleted = false).first()
            
            // Δημιουργία JSON
            val backup = JSONObject().apply {
                put("version", 1)
                put("createdAt", System.currentTimeMillis())
                put("userId", userId)
                put("shifts", shiftsToJson(shifts))
                put("expenses", expensesToJson(expenses))
            }
            
            // Αποθήκευση σε αρχείο
            val backupDir = getBackupDirectory()
            val fileName = "backup_${dateFormat.format(Date())}.json"
            val file = File(backupDir, fileName)
            
            file.writeText(backup.toString(2))
            
            Result.Success(file.absolutePath)
        } catch (e: Exception) {
            Result.Error("Σφάλμα δημιουργίας backup: ${e.message}")
        }
    }
    
    /**
     * Επαναφέρει δεδομένα από backup.
     */
    override suspend fun restoreBackup(backupPath: String): Result<Int> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.Error("Δεν είστε συνδεδεμένος")
            
            val file = File(backupPath)
            if (!file.exists()) {
                return Result.Error("Το αρχείο backup δεν βρέθηκε")
            }
            
            val json = JSONObject(file.readText())
            var restoredCount = 0
            
            // Επαναφορά βαρδιών
            val shiftsArray = json.optJSONArray("shifts")
            if (shiftsArray != null) {
                for (i in 0 until shiftsArray.length()) {
                    val shiftJson = shiftsArray.getJSONObject(i)
                    val shift = jsonToShift(shiftJson, userId)
                    shiftRepository.createShift(shift)
                    restoredCount++
                }
            }
            
            // Επαναφορά εξόδων
            val expensesArray = json.optJSONArray("expenses")
            if (expensesArray != null) {
                for (i in 0 until expensesArray.length()) {
                    val expenseJson = expensesArray.getJSONObject(i)
                    val expense = jsonToExpense(expenseJson, userId)
                    expenseRepository.createExpense(expense)
                    restoredCount++
                }
            }
            
            Result.Success(restoredCount)
        } catch (e: Exception) {
            Result.Error("Σφάλμα επαναφοράς: ${e.message}")
        }
    }
    
    /**
     * Επιστρέφει τα διαθέσιμα backups.
     */
    override suspend fun getAvailableBackups(): Result<List<String>> {
        return try {
            val backupDir = getBackupDirectory()
            val files = backupDir.listFiles { file -> 
                file.extension == "json" && file.name.startsWith("backup_")
            }?.sortedByDescending { it.lastModified() }?.map { it.absolutePath }
                ?: emptyList()
            
            Result.Success(files)
        } catch (e: Exception) {
            Result.Error("Σφάλμα ανάγνωσης backups: ${e.message}")
        }
    }
    
    // ============ Helper Methods ============
    
    private fun getBackupDirectory(): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "DeliveryTracker/Backups")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    private fun shiftsToJson(shifts: List<Shift>): JSONArray {
        return JSONArray().apply {
            shifts.forEach { shift ->
                put(JSONObject().apply {
                    put("date", shift.date)
                    put("workedHours", shift.workedHours)
                    put("workedMinutes", shift.workedMinutes)
                    put("grossIncome", shift.grossIncome)
                    put("tips", shift.tips)
                    put("bonus", shift.bonus)
                    put("ordersCount", shift.ordersCount)
                    put("kilometers", shift.kilometers)
                    put("notes", shift.notes)
                })
            }
        }
    }
    
    private fun expensesToJson(expenses: List<Expense>): JSONArray {
        return JSONArray().apply {
            expenses.forEach { expense ->
                put(JSONObject().apply {
                    put("date", expense.date)
                    put("amount", expense.amount)
                    put("category", expense.category.name)
                    put("paymentMethod", expense.paymentMethod.name)
                    put("notes", expense.notes)
                })
            }
        }
    }
    
    private fun jsonToShift(json: JSONObject, userId: String): Shift {
        return Shift(
            userId = userId,
            date = json.getLong("date"),
            workedHours = json.optInt("workedHours", 0),
            workedMinutes = json.optInt("workedMinutes", 0),
            grossIncome = json.optDouble("grossIncome", 0.0),
            tips = json.optDouble("tips", 0.0),
            bonus = json.optDouble("bonus", 0.0),
            ordersCount = json.optInt("ordersCount", 0),
            kilometers = json.optDouble("kilometers", 0.0),
            notes = json.optString("notes", "")
        )
    }
    
    private fun jsonToExpense(json: JSONObject, userId: String): Expense {
        return Expense(
            userId = userId,
            date = json.getLong("date"),
            amount = json.optDouble("amount", 0.0),
            category = try { 
                ExpenseCategory.valueOf(json.optString("category", "OTHER"))
            } catch (e: Exception) { 
                ExpenseCategory.OTHER 
            },
            paymentMethod = try {
                PaymentMethod.valueOf(json.optString("paymentMethod", "CASH"))
            } catch (e: Exception) {
                PaymentMethod.CASH
            },
            notes = json.optString("notes", "")
        )
    }
}
