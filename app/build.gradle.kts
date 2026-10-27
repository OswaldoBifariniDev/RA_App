import java.util.Properties





plugins {


    alias(libs.plugins.android.application)


    alias(libs.plugins.kotlin.android)


    alias(libs.plugins.ksp)


    alias(libs.plugins.hilt.android)


}





val localProperties = Properties()


val localPropertiesFile = rootProject.file("local.properties")


if (localPropertiesFile.exists()) {


    localPropertiesFile.inputStream().use { inputStream ->


        localProperties.load(inputStream)


    }


}


val geminiKey = (localProperties.getProperty("GEMINI_API_KEY")


    ?: providers.environmentVariable("GEMINI_API_KEY").orNull ?: "").trim()


require(geminiKey.all { it.isLetterOrDigit() || it == '_' || it == '-' || it == '.' }) { "Formato de clave Gemini no válido" }


val geminiModel = providers.gradleProperty("geminiModel").getOrElse("gemini-3.5-flash")


require(geminiModel.matches(Regex("[a-zA-Z0-9._-]+")))





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


            .getOrElse("http://10.0.2.2:3000/")


        require(riesgosApiUrl.endsWith("/") && !riesgosApiUrl.contains('"'))


        buildConfigField("String", "RIESGOS_API_URL", "\"$riesgosApiUrl\"")


        // El catÃ¡logo existente estÃ¡ en Apache/MySQL; la autenticaciÃ³n estÃ¡ en PHP/PostgreSQL.


        val riesgosCatalogoUrl = providers.gradleProperty("riesgosCatalogoUrl")


            .getOrElse("http://10.0.2.2:8000/")


        require(riesgosCatalogoUrl.endsWith("/") && !riesgosCatalogoUrl.contains('"'))


        buildConfigField("String", "RIESGOS_CATALOGO_URL", "\"$riesgosCatalogoUrl\"")





        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")


        buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")





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


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    testImplementation(libs.junit)





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





    // NavegaciÃ³n


    implementation(libs.androidx.navigation.fragment)


    implementation(libs.androidx.navigation.ui)





    // Retrofit & OkHttp & Gson


    implementation(libs.retrofit)


    implementation(libs.retrofit.converter.gson)


    implementation(libs.okhttp)


    implementation(libs.okhttp.logging)


    implementation(libs.gson)





    // CameraX


    implementation(libs.androidx.camera.core)


    implementation(libs.androidx.camera.camera2)


    implementation(libs.androidx.camera.lifecycle)


    implementation(libs.androidx.camera.view)





    // Exif


    implementation(libs.androidx.exifinterface)





    // Lifecycle


    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    implementation(libs.androidx.lifecycle.runtime.ktx)





    // Hilt


    implementation(libs.hilt.android)


    ksp(libs.hilt.compiler)


}