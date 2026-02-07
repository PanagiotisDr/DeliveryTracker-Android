package com.deliverytracker.app.domain.repository

import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface για τις ρυθμίσεις χρήστη.
 */
interface UserSettingsRepository {
    
    /**
     * Επιστρέφει τις ρυθμίσεις του χρήστη ως Flow.
     */
    fun getUserSettings(userId: String): Flow<UserSettings?>
    
    /**
     * Ενημερώνει τις ρυθμίσεις του χρήστη.
     */
    suspend fun updateUserSettings(settings: UserSettings): Result<UserSettings>
    
    /**
     * Ενημερώνει μόνο το theme (άμεση αποθήκευση χωρίς Save button).
     */
    suspend fun updateThemeOnly(userId: String, theme: ThemeMode): Result<Unit>
    
    /**
     * Ενημερώνει μόνο το dynamicColor (άμεση αποθήκευση χωρίς Save button).
     */
    suspend fun updateDynamicColorOnly(userId: String, enabled: Boolean): Result<Unit>
    
    /**
     * Δημιουργεί default ρυθμίσεις για νέο χρήστη.
     */
    suspend fun createDefaultSettings(userId: String): Result<UserSettings>
}
