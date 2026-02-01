package com.deliverytracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.deliverytracker.app.presentation.components.BottomNavBar
import com.deliverytracker.app.presentation.navigation.NavGraph
import com.deliverytracker.app.presentation.navigation.Screen
import com.deliverytracker.app.presentation.theme.DeliveryTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Η κύρια Activity της εφαρμογής.
 * Χρησιμοποιεί Jetpack Compose με Bottom Navigation για το UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Εγκατάσταση Splash Screen πριν το super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // Παρακολούθηση theme settings
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userSettings by userSettingsRepository.getUserSettings(userId ?: "")
                .collectAsState(initial = null)
            
            // Υπολογισμός dark theme
            val systemDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (userSettings?.theme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM, null -> systemDarkTheme
            }
            
            DeliveryTrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                
                // Έλεγχος αν ο χρήστης είναι συνδεδεμένος
                val isLoggedIn = authRepository.isLoggedIn
                
                // Έλεγχος αν ο χρήστης έχει PIN
                val currentUser by authRepository.currentUser.collectAsState(initial = null)
                
                // Αν είναι logged in αλλά δεν έχουμε φορτώσει ακόμα τον user, δείξε loading
                if (isLoggedIn && currentUser == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val hasPin = currentUser?.hasPin ?: false
                    
                    // Βρίσκουμε το τρέχον route για να ξέρουμε αν πρέπει να δείξουμε το bottom nav
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    // Δείχνουμε το bottom nav μόνο στις main screens (όχι auth screens)
                    val showBottomNav = isLoggedIn && currentRoute in listOf(
                        Screen.Dashboard.route,
                        Screen.ShiftList.route,
                        Screen.ExpenseList.route,
                        Screen.Statistics.route,
                        Screen.Settings.route
                    )
                    
                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavBar(
                                    currentRoute = currentRoute ?: "",
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            // Pop up όλα μέχρι το start destination για να μην χτίζεται stack
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Αποφυγή πολλαπλών instances της ίδιας οθόνης
                                            launchSingleTop = true
                                            // Restore state όταν επιστρέφουμε σε ένα tab
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        NavGraph(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            hasPin = hasPin,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

