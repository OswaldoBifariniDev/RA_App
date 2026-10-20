plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.riesgosra.app"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.ra_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val riesgosApiUrl = providers.gradleProperty("riesgosApiUrl")
            .getOrElse("http://127.0.0.1:3000/")
        require(riesgosApiUrl.endsWith("/") && !riesgosApiUrl.contains('"'))
        buildConfigField("String", "RIESGOS_API_URL", "\"$riesgosApiUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.zxing.embedded)
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
}
