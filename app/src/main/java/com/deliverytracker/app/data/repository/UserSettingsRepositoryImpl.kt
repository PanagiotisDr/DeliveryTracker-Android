package com.deliverytracker.app.data.repository

import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.model.UserSettings
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation του UserSettingsRepository.
 */
@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserSettingsRepository {
    
    private val settingsCollection = firestore.collection("user_settings")
    
    override fun getUserSettings(userId: String): Flow<UserSettings?> = callbackFlow {
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
            Result.Error("Σφάλμα ενημέρωσης ρυθμίσεων: ${e.message}", e)
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
            Result.Error("Σφάλμα δημιουργίας ρυθμίσεων: ${e.message}", e)
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
