---
description: Android Native Development με Kotlin & Jetpack Compose (2026 Stack)
---

# 📱 Android Native Development Skill (2026)

## Επισκόπηση
Οδηγός για native Android development με το τελευταίο tech stack του 2026.

---

## 🛠️ Tech Stack Versions (Ιανουάριος 2026)

### Core
| Technology | Version | Notes |
|------------|---------|-------|
| **Kotlin** | 2.2.10 / 2.3.0 | KGP 2.2.10 για AGP 9.0 compatibility |
| **Compose BOM** | 2025.12.00 | Compose 1.10 + Material 3 1.4 |
| **Android Gradle Plugin** | 9.0 | Απαιτεί Gradle 9.3.0+ |
| **Target SDK** | 35 | Android 15 |
| **Min SDK** | 26 | Android 8.0 (Oreo) |

### Dependency Injection
| Technology | Version | Notes |
|------------|---------|-------|
| **Hilt** | 2.59 | Χρησιμοποιεί KSP (όχι KAPT) |
| **AndroidX Hilt** | 1.2.0 | hilt-navigation-compose |

### Firebase
| Technology | Version | Notes |
|------------|---------|-------|
| **Firebase BOM** | 34.8.0 | Χωρίς KTX modules από 34.0.0 |
| **Firestore** | Μέσω BOM | |
| **Firebase Auth** | Μέσω BOM | |
| **Firebase Analytics** | Μέσω BOM | |

### Επιπλέον Libraries
| Technology | Version | Purpose |
|------------|---------|---------|
| **Room** | 2.7.0 | Local database |
| **DataStore** | 1.1.0 | Preferences |
| **WorkManager** | 2.10.0 | Background tasks |
| **Navigation Compose** | 2.8.0 | Navigation |
| **Vico** | 2.0.0 | Charts |
| **Coil** | 3.0.0 | Image loading |

---

## 📦 libs.versions.toml (Version Catalog)

```toml
[versions]
# Core
kotlin = "2.2.10"
agp = "9.0.0"
ksp = "2.2.10-1.0.30"

# Compose
composeBom = "2025.12.00"

# AndroidX
coreKtx = "1.16.0"
lifecycleRuntimeKtx = "2.9.0"
activityCompose = "1.10.0"
navigationCompose = "2.8.0"

# Hilt
hilt = "2.59"
hiltNavigation = "1.2.0"

# Firebase
firebaseBom = "34.8.0"

# Room
room = "2.7.0"

# DataStore
datastore = "1.1.0"

# WorkManager
workManager = "2.10.0"

# Charts
vico = "2.0.0"

# Image Loading
coil = "3.0.0"

# Testing
junit = "5.11.0"
mockk = "1.14.0"
coroutinesTest = "1.10.0"

[libraries]
# AndroidX Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

# Compose
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigation" }

# Firebase
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# WorkManager
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# Charts
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }

# Coil
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }

# Testing
junit = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junit" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesTest" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
google-services = { id = "com.google.gms.google-services", version = "4.4.2" }
```

---

## 📁 Δομή Project (Clean Architecture)

```
app/
├── src/main/java/com/example/app/
│   ├── di/                      # Hilt Modules
│   │   ├── AppModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── FirebaseModule.kt
│   ├── domain/                  # Domain Layer
│   │   ├── model/               # Entities
│   │   ├── repository/          # Repository Interfaces
│   │   └── usecase/             # Use Cases
│   ├── data/                    # Data Layer
│   │   ├── local/               # Room DAOs, DataStore
│   │   ├── remote/              # Firebase Services
│   │   └── repository/          # Repository Implementations
│   └── presentation/            # Presentation Layer
│       ├── components/          # Reusable Composables
│       ├── screens/             # Screen + ViewModel
│       ├── navigation/          # NavGraph
│       └── theme/               # Colors, Type, Theme
├── src/main/res/
│   ├── values/strings.xml       # English
│   └── values-el/strings.xml    # Greek
└── build.gradle.kts
```

---

## 🎨 Theme Setup (Material 3)

### Color.kt
```kotlin
// Primary palette
val Primary = Color(0xFF00BFA5)        // Teal
val OnPrimary = Color(0xFF003731)
val PrimaryContainer = Color(0xFF70F7D9)

// Background (Dark Theme)
val Background = Color(0xFF0D1117)
val Surface = Color(0xFF161B22)
val SurfaceVariant = Color(0xFF21262D)

// Text
val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)

// Semantic
val Success = Color(0xFF3FB950)
val Warning = Color(0xFFD29922)
val Error = Color(0xFFF85149)
```

### Theme.kt
```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
```

---

## 🔧 Hilt Setup

### Application.kt
```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

### MainActivity.kt
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                NavGraph()
            }
        }
    }
}
```

### ViewModel Pattern
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val data = repository.getData()
                _uiState.update { it.copy(data = data, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
```

---

## 🔥 Firebase Setup

### FirebaseModule.kt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}
```

### Repository Pattern με Firebase
```kotlin
class MyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MyRepository {
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    
    override fun getItems(): Flow<List<MyItem>> = callbackFlow {
        val listener = firestore.collection("users/$userId/items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { 
                    it.toObject<MyItem>() 
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }
}
```

---

## ✅ Κανόνες Ανάπτυξης

1. **Zero Hardcoding**: Όλα τα strings στο `strings.xml`
2. **MVVM**: Κάθε Screen έχει αντίστοιχο ViewModel
3. **Clean Architecture**: Domain → Data → Presentation
4. **KSP over KAPT**: Για Hilt και Room
5. **StateFlow**: Για reactive UI state
6. **Error Handling**: Try/catch σε όλα τα async operations
7. **Σχόλια στα Ελληνικά**: Για εύκολη κατανόηση
