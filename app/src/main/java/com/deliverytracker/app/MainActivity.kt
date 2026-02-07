package com.deliverytracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deliverytracker.app.domain.model.ThemeMode
import com.deliverytracker.app.presentation.MainViewModel
import com.deliverytracker.app.presentation.components.BottomNavBar
import com.deliverytracker.app.presentation.navigation.Dashboard
import com.deliverytracker.app.presentation.navigation.ExpenseList
import com.deliverytracker.app.presentation.navigation.NavGraph
import com.deliverytracker.app.presentation.navigation.Settings
import com.deliverytracker.app.presentation.navigation.ShiftList
import com.deliverytracker.app.presentation.navigation.Statistics
import com.deliverytracker.app.presentation.theme.DeliveryTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Η κύρια Activity της εφαρμογής.
 * Χρησιμοποιεί MainViewModel αντί field injection για auth/theme state.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Αντικατάσταση @Inject field injection με proper ViewModel
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Εγκατάσταση Splash Screen πριν το super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val mainState by viewModel.uiState.collectAsState()
            
            // Υπολογισμός dark theme
            val systemDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (mainState.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDarkTheme
            }
            
            DeliveryTrackerTheme(
                darkTheme = isDarkTheme,
                dynamicColor = mainState.dynamicColor
            ) {
                val navController = rememberNavController()
                
                // Αν φορτώνει ακόμα, δείξε loading
                if (mainState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Βρίσκουμε το τρέχον destination για bottom nav visibility
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    // Δείχνουμε bottom nav μόνο στις main screens
                    // hasRoute(KClass) — type-safe route matching
                    val showBottomNav = mainState.isLoggedIn && currentDestination?.let { dest ->
                        dest.hasRoute(Dashboard::class) ||
                        dest.hasRoute(ShiftList::class) ||
                        dest.hasRoute(ExpenseList::class) ||
                        dest.hasRoute(Statistics::class) ||
                        dest.hasRoute(Settings::class)
                    } == true
                    
                    // Μετατροπή current destination σε string ID για BottomNavBar
                    val currentRouteForNav = when {
                        currentDestination?.hasRoute(Dashboard::class) == true -> "dashboard"
                        currentDestination?.hasRoute(ShiftList::class) == true -> "shifts"
                        currentDestination?.hasRoute(ExpenseList::class) == true -> "expenses"
                        currentDestination?.hasRoute(Statistics::class) == true -> "statistics"
                        currentDestination?.hasRoute(Settings::class) == true -> "settings"
                        else -> ""
                    }
                    
                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavBar(
                                    currentRoute = currentRouteForNav,
                                    onNavigate = { route ->
                                        // Μετατροπή string route → type-safe destination
                                        val destination: Any = when (route) {
                                            "dashboard" -> Dashboard
                                            "shifts" -> ShiftList
                                            "expenses" -> ExpenseList
                                            "statistics" -> Statistics
                                            "settings" -> Settings
                                            else -> Dashboard
                                        }
                                        navController.navigate(destination) {
                                            // Pop up μέχρι start — αποφυγή stack buildup
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        NavGraph(
                            navController = navController,
                            isLoggedIn = mainState.isLoggedIn,
                            hasPin = mainState.hasPin,
                            onSignOut = { viewModel.signOut() },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}
