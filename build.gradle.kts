// Ρύθμιση Top-level build configuration για το DeliveryTracker Android
// Χρησιμοποιούμε Kotlin DSL για type-safe configuration

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
}
