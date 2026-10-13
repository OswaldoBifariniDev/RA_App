plugins {
    alias(libs.plugins.android.application)
<<<<<<< HEAD
}

android {
    namespace = "com.example.ra_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.riesgosra_vinculacion"
=======
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.riesgosra.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ra_app"
>>>>>>> 1f1102b7d66b4213af6f1d5544c86a04b3600622
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

<<<<<<< HEAD
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
=======
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
>>>>>>> 1f1102b7d66b4213af6f1d5544c86a04b3600622
    }
}

dependencies {
<<<<<<< HEAD
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
=======
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)

    // Base de Datos (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Realidad Aumentada (ARCore)
    implementation(libs.google.arcore)

    // Navegación
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
>>>>>>> 1f1102b7d66b4213af6f1d5544c86a04b3600622
}
