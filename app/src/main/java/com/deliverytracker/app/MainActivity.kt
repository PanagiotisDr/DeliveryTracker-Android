package com.deliverytracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.repository.UserSettingsRepository
import com.deliverytracker.app.presentation.components.BottomNavBar
import com.deliverytracker.app.presentation.navigation.NavGraph
import com.deliverytracker.app.presentation.navigation.Screen
import com.deliverytracker.app.presentation.theme.DeliveryTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
                val currentUserState by authRepository.currentUserState.collectAsState(initial = Result.Loading)
                val currentUser = (currentUserState as? Result.Success)?.data
                val coroutineScope = rememberCoroutineScope()
                
                // Αν είναι logged in αλλά δεν έχουμε φορτώσει ακόμα τον user, δείξε loading
                if (isLoggedIn) {
                    when (val state = currentUserState) {
                        is Result.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is Result.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = state.message)
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                authRepository.logout()
                                            }
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.auth_logout))
                                    }
                                }
                            }
                        }
                        is Result.Success -> {
                            val hasPin = currentUser?.hasPin ?: false
                            renderAppScaffold(
                                navController = navController,
                                isLoggedIn = isLoggedIn,
                                hasPin = hasPin
                            )
                        }
                    }
                } else {
                    val hasPin = currentUser?.hasPin ?: false
                    renderAppScaffold(
                        navController = navController,
                        isLoggedIn = isLoggedIn,
                        hasPin = hasPin
                    )
                }
            }
        }
    }
}

@Composable
private fun renderAppScaffold(
    navController: androidx.navigation.NavHostController,
    isLoggedIn: Boolean,
    hasPin: Boolean
) {
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
