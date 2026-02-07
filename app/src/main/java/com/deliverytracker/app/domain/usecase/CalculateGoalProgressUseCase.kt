package com.deliverytracker.app.domain.usecase

import javax.inject.Inject

/**
 * Αποτέλεσμα υπολογισμού προόδου στόχων.
 */
data class GoalProgress(
    val dailyProgress: Float = 0f,
    val monthlyProgress: Float = 0f
)

/**
 * UseCase: Υπολογίζει την πρόοδο προς τους ημερήσιους και μηνιαίους στόχους.
 * Εξάγει τη λογική υπολογισμού progress από το DashboardViewModel.
 */
class CalculateGoalProgressUseCase @Inject constructor() {
    
    /**
     * @param todayIncome Σημερινό καθαρό εισόδημα
     * @param monthIncome Μηνιαίο καθαρό εισόδημα
     * @param dailyGoal Ημερήσιος στόχος (null αν δεν έχει οριστεί)
     * @param monthlyGoal Μηνιαίος στόχος (null αν δεν έχει οριστεί)
     * @return Ποσοστά προόδου (0f..1f)
     */
    operator fun invoke(
        todayIncome: Double,
        monthIncome: Double,
        dailyGoal: Double?,
        monthlyGoal: Double?
    ): GoalProgress {
        val dailyProgress = if (dailyGoal != null && dailyGoal > 0) {
            (todayIncome / dailyGoal).toFloat().coerceIn(0f, 1f)
        } else 0f
        
        val monthlyProgress = if (monthlyGoal != null && monthlyGoal > 0) {
            (monthIncome / monthlyGoal).toFloat().coerceIn(0f, 1f)
        } else 0f
        
        return GoalProgress(
            dailyProgress = dailyProgress,
            monthlyProgress = monthlyProgress
        )
    }
}
