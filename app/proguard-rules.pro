# ProGuard rules για το DeliveryTracker
# Κρατάμε τα models για Firebase serialization

# Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*

# Firebase Firestore - Κρατάμε τα data classes
-keep class com.deliverytracker.app.data.remote.dto.** { *; }
-keep class com.deliverytracker.app.domain.model.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose
-dontwarn androidx.compose.**
