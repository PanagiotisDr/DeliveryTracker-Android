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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module για Firebase dependencies.
 * Παρέχει τα Firebase instances σε όλη την εφαρμογή.
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
 * Hilt module για Repository dependencies.
 * Συνδέει τα interfaces με τις υλοποιήσεις τους.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Παρέχει το AuthRepository.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, firestore)
    
    /**
     * Παρέχει το ShiftRepository.
     */
    @Provides
    @Singleton
    fun provideShiftRepository(
        firestore: FirebaseFirestore
    ): ShiftRepository = ShiftRepositoryImpl(firestore)
    
    /**
     * Παρέχει το ExpenseRepository.
     */
    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestore: FirebaseFirestore
    ): ExpenseRepository = ExpenseRepositoryImpl(firestore)
    
    /**
     * Παρέχει το UserSettingsRepository.
     */
    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        firestore: FirebaseFirestore
    ): UserSettingsRepository = UserSettingsRepositoryImpl(firestore)
    
    /**
     * Παρέχει το ExportRepository.
     */
    @Provides
    @Singleton
    fun provideExportRepository(
        @ApplicationContext context: Context,
        shiftRepository: ShiftRepository,
        expenseRepository: ExpenseRepository
    ): ExportRepository = ExportRepositoryImpl(context, shiftRepository, expenseRepository)
    
    /**
     * Παρέχει το BackupRepository.
     */
    @Provides
    @Singleton
    fun provideBackupRepository(
        @ApplicationContext context: Context,
        shiftRepository: ShiftRepository,
        expenseRepository: ExpenseRepository
    ): BackupRepository = BackupRepositoryImpl(context, shiftRepository, expenseRepository)
}

