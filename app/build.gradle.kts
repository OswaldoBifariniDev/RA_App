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
        val claveGemini = providers.environmentVariable("RIESGORA_GEMINI_API_KEY").orNull
            ?: providers.exec {
                commandLine("powershell.exe", "-NoProfile", "-ExecutionPolicy", "RemoteSigned", "-File",
                    rootProject.file("backend_analisis/leer_clave_compilacion.ps1").absolutePath)
            }.standardOutput.asText.get().trim()
        require(claveGemini.length in 20..512 && claveGemini.all { it.code in 33..126 }) {
            "Configura el acceso privado a Gemini antes de compilar."
        }
        buildConfigField("String", "GEMINI_API_KEY", groovy.json.JsonOutput.toJson(claveGemini))

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
