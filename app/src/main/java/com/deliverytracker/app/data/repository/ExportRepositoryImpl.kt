package com.deliverytracker.app.data.repository

import android.content.Context
import android.os.Environment
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Expense
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.Shift
import com.deliverytracker.app.domain.repository.ExportRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του ExportRepository.
 * Χρησιμοποιεί context.getString() για proper i18n.
 */
@Singleton
class ExportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shiftRepository: ShiftRepository,
    private val expenseRepository: ExpenseRepository
) : ExportRepository {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fileNameDateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
    
    /**
     * Εξαγωγή βαρδιών σε CSV.
     */
    override suspend fun exportShiftsToCsv(startDate: Long, endDate: Long): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.Error(context.getString(R.string.error_not_logged_in))
            
            // Λήψη βαρδιών
            val shifts = shiftRepository.getShiftsByDateRange(userId, startDate, endDate).first()
            
            if (shifts.isEmpty()) {
                return Result.Error(context.getString(R.string.error_no_shifts_period))
            }
            
            // Δημιουργία φακέλου export
            val exportDir = getExportDirectory()
            val fileName = "shifts_${fileNameDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)
            
            // Εγγραφή CSV
            FileWriter(file).use { writer ->
                // Header - localized
                writer.append(context.getString(R.string.export_header_shifts))
                writer.append("\n")
                
                // Data rows
                shifts.forEach { shift ->
                    writer.append(buildShiftCsvRow(shift))
                    writer.append("\n")
                }
            }
            
            Result.Success(file.absolutePath)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_export, e.message ?: ""))
        }
    }
    
    /**
     * Εξαγωγή εξόδων σε CSV.
     */
    override suspend fun exportExpensesToCsv(startDate: Long, endDate: Long): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.Error(context.getString(R.string.error_not_logged_in))
            
            // Λήψη εξόδων
            val expenses = expenseRepository.getExpensesByDateRange(userId, startDate, endDate).first()
            
            if (expenses.isEmpty()) {
                return Result.Error(context.getString(R.string.error_no_expenses_period))
            }
            
            // Δημιουργία φακέλου export
            val exportDir = getExportDirectory()
            val fileName = "expenses_${fileNameDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)
            
            // Εγγραφή CSV
            FileWriter(file).use { writer ->
                // Header - localized
                writer.append(context.getString(R.string.export_header_expenses))
                writer.append("\n")
                
                // Data rows
                expenses.forEach { expense ->
                    writer.append(buildExpenseCsvRow(expense))
                    writer.append("\n")
                }
            }
            
            Result.Success(file.absolutePath)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_export, e.message ?: ""))
        }
    }
    
    /**
     * Εξαγωγή αναφοράς σε text report.
     */
    override suspend fun exportReportToPdf(startDate: Long, endDate: Long): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.Error(context.getString(R.string.error_not_logged_in))
            
            // Λήψη δεδομένων
            val shifts = shiftRepository.getShiftsByDateRange(userId, startDate, endDate).first()
            val expenses = expenseRepository.getExpensesByDateRange(userId, startDate, endDate).first()
            
            // Υπολογισμοί
            val totalGross = shifts.sumOf { it.grossIncome }
            val totalTips = shifts.sumOf { it.tips }
            val totalBonus = shifts.sumOf { it.bonus }
            val totalNet = shifts.sumOf { it.netIncome }
            val totalExpenses = expenses.sumOf { it.amount }
            val totalOrders = shifts.sumOf { it.ordersCount }
            val totalKm = shifts.sumOf { it.kilometers }
            val totalHours = shifts.sumOf { it.hoursWorked }
            
            // Δημιουργία φακέλου export
            val exportDir = getExportDirectory()
            val fileName = "report_${fileNameDateFormat.format(Date())}.txt"
            val file = File(exportDir, fileName)
            
            // Εγγραφή αναφοράς - localized
            FileWriter(file).use { writer ->
                writer.append("═══════════════════════════════════════\n")
                writer.append("      ${context.getString(R.string.report_footer)}\n")
                writer.append("═══════════════════════════════════════\n\n")
                
                writer.append(context.getString(
                    R.string.report_period,
                    dateFormat.format(Date(startDate)),
                    dateFormat.format(Date(endDate))
                ))
                writer.append("\n\n")
                
                writer.append("📊 ${context.getString(R.string.report_summary)}\n")
                writer.append("───────────────────────────────────────\n")
                writer.append("${context.getString(R.string.report_gross_income)}      ${String.format("%.2f", totalGross)}€\n")
                writer.append("${context.getString(R.string.report_tips)}     ${String.format("%.2f", totalTips)}€\n")
                writer.append("Bonus:            ${String.format("%.2f", totalBonus)}€\n")
                writer.append("${context.getString(R.string.report_net_income)}     ${String.format("%.2f", totalNet)}€\n\n")
                
                writer.append("💸 ${context.getString(R.string.report_expenses_title)}\n")
                writer.append("───────────────────────────────────────\n")
                writer.append("${context.getString(R.string.report_total_expenses)}    ${String.format("%.2f", totalExpenses)}€\n\n")
                
                writer.append("🎯 ${context.getString(R.string.report_profit_title)}\n")
                writer.append("───────────────────────────────────────\n")
                writer.append("${context.getString(R.string.report_profit)}           ${String.format("%.2f", totalNet - totalExpenses)}€\n\n")
                
                writer.append("📈 ${context.getString(R.string.report_stats_title)}\n")
                writer.append("───────────────────────────────────────\n")
                writer.append("${context.getString(R.string.report_shifts_count)}          ${shifts.size}\n")
                writer.append("${context.getString(R.string.report_orders_count)}      $totalOrders\n")
                writer.append("${context.getString(R.string.report_km_count)}       ${String.format("%.1f", totalKm)} km\n")
                writer.append("${context.getString(R.string.report_hours_count)}             ${String.format("%.1f", totalHours)} h\n\n")
                
                if (totalHours > 0) {
                    writer.append("📊 AVERAGES\n")
                    writer.append("───────────────────────────────────────\n")
                    writer.append("${context.getString(R.string.report_avg_per_hour)}            ${String.format("%.2f", totalNet / totalHours)}€\n")
                    if (totalOrders > 0) {
                        writer.append("${context.getString(R.string.report_avg_per_order)}     ${String.format("%.2f", totalNet / totalOrders)}€\n")
                    }
                }
                
                writer.append("\n═══════════════════════════════════════\n")
                writer.append(context.getString(
                    R.string.report_created_at,
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                ))
                writer.append("\n")
            }
            
            Result.Success(file.absolutePath)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_report_create, e.message ?: ""))
        }
    }
    
    /**
     * Επιστρέφει τον φάκελο εξαγωγής.
     */
    private fun getExportDirectory(): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "DeliveryTracker")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Δημιουργεί μια γραμμή CSV για βάρδια.
     */
    private fun buildShiftCsvRow(shift: Shift): String {
        return listOf(
            dateFormat.format(Date(shift.date)),
            shift.workedHours.toString(),
            shift.workedMinutes.toString(),
            String.format("%.2f", shift.grossIncome),
            String.format("%.2f", shift.tips),
            String.format("%.2f", shift.bonus),
            String.format("%.2f", shift.netIncome),
            shift.ordersCount.toString(),
            String.format("%.1f", shift.kilometers),
            "\"${shift.notes.replace("\"", "\"\"")}\""
        ).joinToString(",")
    }
    
    /**
     * Δημιουργεί μια γραμμή CSV για έξοδο.
     * Χρησιμοποιεί localized category name.
     */
    private fun buildExpenseCsvRow(expense: Expense): String {
        return listOf(
            dateFormat.format(Date(expense.date)),
            context.getString(expense.category.displayNameResId),
            String.format("%.2f", expense.amount),
            expense.paymentMethod.name,
            "\"${expense.notes.replace("\"", "\"\"")}\""
        ).joinToString(",")
    }
}
