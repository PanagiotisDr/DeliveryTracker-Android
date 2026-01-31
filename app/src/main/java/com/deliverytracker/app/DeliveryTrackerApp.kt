package com.deliverytracker.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class για το DeliveryTracker.
 * Αρχικοποιεί το Hilt Dependency Injection framework.
 */
@HiltAndroidApp
class DeliveryTrackerApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Εδώ μπορούμε να προσθέσουμε αρχικοποίηση για logging, analytics κλπ.
    }
}
