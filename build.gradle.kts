// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //alias(libs.plugins.android.application) apply false
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.ra_app"
    compileSdk = 34 // o la versión que uses

    defaultConfig {
        applicationId = "com.example.ra_app"
        minSdk = 24 // ARCore requiere mínimo SDK 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Base de Datos (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Realidad Aumentada (ARCore)
    implementation(libs.google.arcore)

    // Navegación
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
}
