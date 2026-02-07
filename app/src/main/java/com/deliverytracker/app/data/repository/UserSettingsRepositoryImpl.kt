package com.deliverytracker.app.data.repository

import android.content.Context
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.model.UserSettings
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation του UserSettingsRepository.
 * Χρησιμοποιεί context.getString() για proper i18n error messages.
 */
@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : UserSettingsRepository {
    
    private val settingsCollection = firestore.collection("user_settings")
    
    override fun getUserSettings(userId: String): Flow<UserSettings?> = callbackFlow {
        // Αποφυγή crash αν το userId είναι κενό
        if (userId.isBlank()) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        
        val listener = settingsCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val settings = snapshot?.toUserSettings()
                trySend(settings)
            }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun updateUserSettings(settings: UserSettings): Result<UserSettings> {
        return try {
            val updatedSettings = settings.copy(updatedAt = System.currentTimeMillis())
            settingsCollection.document(settings.userId)
                .set(updatedSettings.toMap())
                .await()
            Result.Success(updatedSettings)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_settings_update, e.message ?: ""), e)
        }
    }
    
    override suspend fun createDefaultSettings(userId: String): Result<UserSettings> {
        return try {
            val defaultSettings = UserSettings(
                id = userId,
                userId = userId
            )
            settingsCollection.document(userId)
                .set(defaultSettings.toMap())
                .await()
            Result.Success(defaultSettings)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_settings_create, e.message ?: ""), e)
        }
    }
    
    /**
     * Ενημερώνει μόνο το theme στο Firebase (χωρίς να περιμένει Save button).
     * Χρησιμοποιείται για άμεση αλλαγή theme από το Settings.
     */
    override suspend fun updateThemeOnly(userId: String, theme: ThemeMode): Result<Unit> {
        return try {
            settingsCollection.document(userId)
                .update(
                    mapOf(
                        "theme" to theme.name,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_theme_save, e.message ?: ""), e)
        }
    }
    
    /**
     * Ενημερώνει μόνο το dynamicColor στο Firebase (χωρίς Save button).
     * Χρησιμοποιείται για άμεση ενεργοποίηση/απενεργοποίηση dynamic colors.
     */
    override suspend fun updateDynamicColorOnly(userId: String, enabled: Boolean): Result<Unit> {
        return try {
            settingsCollection.document(userId)
                .update(
                    mapOf(
                        "dynamicColor" to enabled,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.error_theme_save, e.message ?: ""), e)
        }
    }
    
    // ============ Helper Functions ============
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toUserSettings(): UserSettings? {
        if (!exists()) return null
        
        return try {
            UserSettings(
                id = id,
                userId = getString("userId") ?: id,
                theme = try {
                    ThemeMode.valueOf(getString("theme") ?: "SYSTEM")
                } catch (e: Exception) {
                    ThemeMode.SYSTEM
                },
                vatRate = getDouble("vatRate") ?: 0.24,
                monthlyEfkaAmount = getDouble("monthlyEfkaAmount") ?: 254.0,
                dailyGoal = getDouble("dailyGoal"),
                weeklyGoal = getDouble("weeklyGoal"),
                monthlyGoal = getDouble("monthlyGoal"),
                yearlyGoal = getDouble("yearlyGoal"),
                dynamicColor = getBoolean("dynamicColor") == true,
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun UserSettings.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "theme" to theme.name,
        "dynamicColor" to dynamicColor,
        "vatRate" to vatRate,
        "monthlyEfkaAmount" to monthlyEfkaAmount,
        "dailyGoal" to dailyGoal,
        "weeklyGoal" to weeklyGoal,
        "monthlyGoal" to monthlyGoal,
        "yearlyGoal" to yearlyGoal,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
