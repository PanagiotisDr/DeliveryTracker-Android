// Module-level build configuration για την Android εφαρμογή
// Περιλαμβάνει Compose, Hilt, Firebase integration
// AGP 9.0 compatible - χρήση built-in Kotlin support

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    // Σημείωση: Το kotlin.android δεν χρειάζεται πλέον στο AGP 9.0
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.deliverytracker.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deliverytracker.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            // Δεν βάζουμε applicationIdSuffix γιατί το Firebase google-services.json
            // έχει μόνο το base package name
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Νέο Kotlin compiler options DSL
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose BOM - Εξασφαλίζει συμβατότητα μεταξύ Compose libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Firebase - ΧΩΡΙΣ -ktx suffix (deprecated στις νέες εκδόσεις)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)

    // Vico Charts - Για γραφήματα στατιστικών
    implementation(libs.vico.compose.m3)

    // Lottie Animations - Για premium animations
    implementation(libs.lottie.compose)

    // Accompanist - System UI & Loading effects
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.placeholder)

    // DataStore - Για τοπικές ρυθμίσεις
    implementation(libs.datastore.preferences)
    
    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
