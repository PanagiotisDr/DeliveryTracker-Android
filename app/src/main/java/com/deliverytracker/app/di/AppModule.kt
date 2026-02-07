package com.deliverytracker.app.di

import android.content.Context
import com.deliverytracker.app.data.repository.AuthRepositoryImpl
import com.deliverytracker.app.data.repository.BackupRepositoryImpl
import com.deliverytracker.app.data.repository.ExpenseRepositoryImpl
import com.deliverytracker.app.data.repository.ExportRepositoryImpl
import com.deliverytracker.app.data.repository.ShiftRepositoryImpl
import com.deliverytracker.app.data.repository.UserSettingsRepositoryImpl
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.BackupRepository
import com.deliverytracker.app.domain.repository.ExpenseRepository
import com.deliverytracker.app.domain.repository.ExportRepository
import com.deliverytracker.app.domain.repository.ShiftRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module για Firebase dependencies.
 * Παρέχει τα Firebase instances σε όλη την εφαρμογή.
 *
 * Χρησιμοποιεί @Provides γιατί τα Firebase instances δημιουργούνται
 * μέσω factory methods (getInstance()) — δεν υποστηρίζουν constructor injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    /**
     * Παρέχει το FirebaseAuth instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Παρέχει το FirebaseFirestore instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

/**
 * Hilt module για Repository bindings.
 * Συνδέει τα domain interfaces με τις data layer υλοποιήσεις τους.
 *
 * Χρησιμοποιεί @Binds αντί @Provides γιατί:
 * - Τα implementations έχουν ήδη @Inject constructor
 * - Το @Binds είναι πιο αποδοτικό (δεν δημιουργεί factory class)
 * - Είναι πιο καθαρό — δηλώνει μόνο "ποια κλάση υλοποιεί ποιο interface"
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Συνδέει AuthRepositoryImpl → AuthRepository
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    
    /**
     * Συνδέει ShiftRepositoryImpl → ShiftRepository
     */
    @Binds
    @Singleton
    abstract fun bindShiftRepository(impl: ShiftRepositoryImpl): ShiftRepository
    
    /**
     * Συνδέει ExpenseRepositoryImpl → ExpenseRepository
     */
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository
    
    /**
     * Συνδέει UserSettingsRepositoryImpl → UserSettingsRepository
     */
    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository
    
    /**
     * Συνδέει ExportRepositoryImpl → ExportRepository
     */
    @Binds
    @Singleton
    abstract fun bindExportRepository(impl: ExportRepositoryImpl): ExportRepository
    
    /**
     * Συνδέει BackupRepositoryImpl → BackupRepository
     */
    @Binds
    @Singleton
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository
}
