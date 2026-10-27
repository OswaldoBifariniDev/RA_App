# API de pruebas conectada al teléfono

La copia del colaborador es PHP con PostgreSQL. El original descargado se conserva; la copia integrada está en backend/api dentro de RA_App.

## Probar ahora

Mantener el computador encendido y ambos equipos en la misma red Wi-Fi. El APK actualizado ya está instalado en el teléfono TECNO y se abrió la pantalla de login.

Cuenta demo del paquete:

- Usuario: jostin_prueba@riesgosra.local
- Contraseña: Prueba123!

API actual: http://192.168.33.221:8000/. Desde el navegador del teléfono, prueba_conexion.php muestra CONEXION CORRECTA.

## Reiniciar el backend

Ejecutar backend/iniciar_backend.ps1 desde PowerShell. Inicia únicamente la base de pruebas separada y el servidor PHP de esta integración. compilar.ps1 compila la app y ejecuta las pruebas.

PostgreSQL de pruebas escucha solo en 127.0.0.1:5433 y conserva datos en backend/.local/postgres. No se modificó el servidor existente del puerto 5432 ni Apache. PHP habilita pdo_pgsql solo para su proceso y escucha en la IP Wi-Fi del computador, puerto 8000. Sus registros están en backend/.local. La configuración privada database.php y los archivos .local están excluidos de Git.

La regla de firewall RiesgosRA pruebas PHP 8000 red local permite PHP, puerto 8000, IP 192.168.33.221 y origen en la subred local. PostgreSQL no está expuesto al teléfono. Si cambia la IP del computador, actualizar gradle.properties (riesgosApiUrl), iniciar_backend.ps1 y permitir_telefono.ps1; actualizar la regla con la IP nueva y volver a compilar e instalar.

## Verificado

- assembleDebug y testDebugUnitTest correctos: 3 pruebas, 0 errores y 0 fallos.
- APK instalado por USB en TECNO KJ6.
- Solicitudes desde el propio teléfono por Wi-Fi: conexión HTTP 200, login demo HTTP 200, ruta protegida con token HTTP 200, sin token HTTP 401 y contraseña incorrecta HTTP 401.
- La pantalla de login fue abierta; las comprobaciones HTTP se realizaron desde el teléfono sin introducir las credenciales en la pantalla.

## Contrato real y límites

- POST login.php recibe identificadorUsuario y contrasena; devuelve autenticado, mensaje, token y usuario.
- GET ruta_protegida.php valida la sesión; NO lista registros.
- GET ruta_crear.php valida el permiso crear_ruta; NO crea registros.
- GET prueba_conexion.php devuelve texto plano.
- El token es una credencial aleatoria almacenada como hash en PostgreSQL, caduca en una hora y se envía como Bearer; no es JWT.

El cliente se adaptó a estos nombres, métodos y respuestas. La creación y sincronización de rutas requieren implementar esos endpoints en el backend; el cliente muestra un mensaje explícito en lugar de fabricar resultados. Esta instalación y la cuenta demo son para pruebas locales.

# Código completo por archivo

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\build.gradle.kts

```kotlin
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
val geminiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

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
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")

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

    // Navegación
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
```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\build.gradle.kts

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\gradle\libs.versions.toml

```toml
[versions]
agp = "8.13.2"
kotlin = "1.9.22"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.6.1"
material = "1.10.0"
activityKtx = "1.8.0"
constraintlayout = "2.1.4"
zxingEmbedded = "4.3.0"
room = "2.6.1"          # Para la Base de Datos Local
ksp = "1.9.22-1.0.17"   # Procesador de anotaciones para Room
arcore = "1.41.0"       # Para Realidad Aumentada
navigation = "2.7.7"    # Para navegar entre pantallas
retrofit = "2.9.0"
okhttp = "4.12.0"
camerax = "1.3.1"
exifinterface = "1.3.7"
gson = "2.10.1"
lifecycle = "2.7.0"
hilt = "2.50"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity-ktx = { group = "androidx.activity", name = "activity-ktx", version.ref = "activityKtx" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
zxing-embedded = { group = "com.journeyapps", name = "zxing-android-embedded", version.ref = "zxingEmbedded" }
# Base de Datos (Room)
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# Realidad Aumentada
google-arcore = { group = "com.google.ar", name = "core", version.ref = "arcore" }

# Navegación
androidx-navigation-fragment = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigation" }
androidx-navigation-ui = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigation" }

# Retrofit & OkHttp
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }

# CameraX
androidx-camera-core = { group = "androidx.camera", name = "camera-core", version.ref = "camerax" }
androidx-camera-camera2 = { group = "androidx.camera", name = "camera-camera2", version.ref = "camerax" }
androidx-camera-lifecycle = { group = "androidx.camera", name = "camera-lifecycle", version.ref = "camerax" }
androidx-camera-view = { group = "androidx.camera", name = "camera-view", version.ref = "camerax" }

# Exif
androidx-exifinterface = { group = "androidx.exifinterface", name = "exifinterface", version.ref = "exifinterface" }

# Lifecycle
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\App.kt

```kotlin
package com.example.ra_app
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class App : Application()

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\local\AppDatabase.kt

```kotlin
package com.example.ra_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ra_app.data.local.dao.RiskAnalysisDao
import com.example.ra_app.data.local.dao.RutaDao
import com.example.ra_app.data.local.entity.RutaEntity
import com.example.ra_app.data.model.RiskAnalysis

@Database(entities = [RiskAnalysis::class, RutaEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun riskAnalysisDao(): RiskAnalysisDao
    abstract fun rutaDao(): RutaDao

}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\local\dao\RutaDao.kt

```kotlin
package com.example.ra_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ra_app.data.local.entity.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RutaDao {
    @androidx.room.Transaction
    open suspend fun replaceAll(rutas: List<RutaEntity>) {
        clearRutas()
        insertAll(rutas)
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRuta(ruta: RutaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(rutas: List<RutaEntity>)

    @Query("SELECT * FROM rutas ORDER BY id DESC")
    abstract fun getAllRutas(): Flow<List<RutaEntity>>

    @Query("DELETE FROM rutas")
    abstract suspend fun clearRutas()
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\local\entity\RutaEntity.kt

```kotlin
package com.example.ra_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ra_app.domain.model.Ruta

@Entity(tableName = "rutas")
data class RutaEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val createdAt: String
) {
    fun toDomain(): Ruta {
        return Ruta(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(ruta: Ruta): RutaEntity {
            return RutaEntity(
                id = ruta.id,
                nombre = ruta.nombre,
                descripcion = ruta.descripcion,
                createdAt = ruta.createdAt
            )
        }
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\local\TokenManager.kt

```kotlin
package com.example.ra_app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\ApiService.kt

```kotlin
package com.example.ra_app.data.remote
import com.example.ra_app.data.remote.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/** Contrato verificado del paquete PHP PostgreSQL entregado por el colaborador. */
interface ApiService {
    @POST("login.php") suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    // Este endpoint valida la sesión; NO devuelve una lista de rutas.
    @GET("ruta_protegida.php") suspend fun checkRutaProtegida(): Response<RutaProtegidaResponse>
    // El backend actual solo valida el permiso crear_ruta; NO inserta un registro.
    @GET("ruta_crear.php") suspend fun checkCrearRutaPermission(): Response<RutaProtegidaResponse>
    // El health check real devuelve texto plano, no JSON.
    @GET("prueba_conexion.php") suspend fun pruebaConexion(): Response<ResponseBody>
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\AuthInterceptor.kt

```kotlin
package com.example.ra_app.data.remote
import com.example.ra_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val protected = request.url.encodedPath in setOf("/ruta_protegida.php", "/ruta_crear.php")
        val builder = request.newBuilder().removeHeader("Authorization")
        if (protected) tokenManager.getToken()?.takeIf { it.isNotBlank() }?.let {
            builder.header("Authorization", "Bearer $it")
        }
        return chain.proceed(builder.build())
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\CrearRutaRequest.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class CrearRutaRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\HealthCheckResponse.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class HealthCheckResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\LoginRequest.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class LoginRequest(
    @SerializedName("identificadorUsuario") val identificadorUsuario: String,
    @SerializedName("contrasena") val contrasena: String
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\LoginResponse.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class LoginResponse(
    @SerializedName("autenticado") val autenticado: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("usuario") val usuario: UsuarioDto? = null
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\RutaDto.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.example.ra_app.data.local.entity.RutaEntity
import com.example.ra_app.domain.model.Ruta
import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class RutaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    fun toDomain(): Ruta {
        return Ruta(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt ?: ""
        )
    }

    fun toEntity(): RutaEntity {
        return RutaEntity(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt ?: ""
        )
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\RutaProtegidaResponse.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class RutaProtegidaResponse(
    @SerializedName("autorizado") val autorizado: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("rutas") val rutas: List<RutaDto>? = null
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\remote\dto\UsuarioDto.kt

```kotlin
package com.example.ra_app.data.remote.dto

import com.example.ra_app.domain.model.User
import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class UsuarioDto(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("identificadorUsuario") val identificadorUsuario: String,
    @SerializedName("rolUsuario") val rolUsuario: String? = null
) {
    fun toDomain(token: String? = null): User {
        return User(
            id = idUsuario,
            nombre = identificadorUsuario,
            email = identificadorUsuario,
            token = token
        )
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\data\repository\RutaRepositoryImpl.kt

```kotlin
package com.example.ra_app.data.repository

import com.example.ra_app.data.local.TokenManager
import com.example.ra_app.data.local.dao.RutaDao
import com.example.ra_app.data.remote.ApiService
import com.example.ra_app.data.remote.dto.LoginRequest
import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.model.User
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RutaRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val rutaDao: RutaDao,
    private val tokenManager: TokenManager
) : RutaRepository {

    override suspend fun login(identificadorUsuario: String, contrasena: String): Resource<User> {
        return try {
            val response = apiService.login(LoginRequest(identificadorUsuario, contrasena))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.autenticado && !body.token.isNullOrBlank()) {
                    body.token?.let { tokenManager.saveToken(it) }
                    val user = body.usuario?.toDomain(body.token)
                        ?: User(id = 0, nombre = identificadorUsuario, email = identificadorUsuario, token = body.token)
                    Resource.Success(user)
                } else {
                    Resource.Error(body?.mensaje ?: "Error de autenticaciÃ³n")
                }
            } else {
                Resource.Error("Error en el servidor (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message ?: "Sin conexiÃ³n"}")
        }
    }

    override suspend fun checkRutaProtegida(): Resource<String> {
        return try {
            val response = apiService.checkRutaProtegida()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.autorizado) {
                    Resource.Success(body.mensaje)
                } else {
                    Resource.Error(body?.mensaje ?: "Acceso no autorizado")
                }
            } else {
                Resource.Error("Error de autorizaciÃ³n (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("Error de conexiÃ³n: ${e.message}")
        }
    }

    override suspend fun crearRuta(nombre: String, descripcion: String): Resource<Ruta> {
        // El paquete PHP solo verifica permisos. No fabricar una creación ni cachearla.
        return try {
            val response = apiService.checkCrearRutaPermission()
            if (!response.isSuccessful) Resource.Error("Error de autorización (${response.code()})")
            else Resource.Error("La API de pruebas valida el permiso, pero todavía no implementa la creación de rutas.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("No se pudo verificar el permiso: ${e.message}")
        }
    }

    override suspend fun pruebaConexion(): Resource<String> {
        return try {
            val response = apiService.pruebaConexion()
            if (response.isSuccessful && response.body() != null) {
                val text = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    response.body()!!.use { it.string() }
                }
                Resource.Success(text)
            } else {
                Resource.Error("Respuesta de error (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("ConexiÃ³n fallida: ${e.message}")
        }
    }

    override fun getLocalRutas(): Flow<List<Ruta>> {
        return rutaDao.getAllRutas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncRutas(): Resource<List<Ruta>> {
        return try {
            val response = apiService.checkRutaProtegida()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.autorizado) return Resource.Error("Acceso no autorizado")
                val dtos = body.rutas ?: return Resource.Error("Esta API solo valida la sesión; no implementa un endpoint para listar rutas.")
                rutaDao.replaceAll(dtos.map { it.toEntity() })
                Resource.Success(dtos.map { it.toDomain() })
            } else {
                Resource.Error("Error al sincronizar (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("SincronizaciÃ³n fallida: ${e.message}")
        }
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\model\Ruta.kt

```kotlin
package com.example.ra_app.domain.model

data class Ruta(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val createdAt: String
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\model\User.kt

```kotlin
package com.example.ra_app.domain.model

data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String? = null
)

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\repository\RutaRepository.kt

```kotlin
package com.example.ra_app.domain.repository

import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.model.User
import com.example.ra_app.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface RutaRepository {

    suspend fun login(identificadorUsuario: String, contrasena: String): Resource<User>

    suspend fun checkRutaProtegida(): Resource<String>

    suspend fun crearRuta(nombre: String, descripcion: String): Resource<Ruta>

    suspend fun pruebaConexion(): Resource<String>

    fun getLocalRutas(): Flow<List<Ruta>>

    suspend fun syncRutas(): Resource<List<Ruta>>
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\usecase\CrearRutaUseCase.kt

```kotlin
package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class CrearRutaUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(nombre: String, descripcion: String): Resource<Ruta> {
        if (nombre.isBlank()) {
            return Resource.Error("El nombre es obligatorio")
        }
        return repository.crearRuta(nombre, descripcion)
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\usecase\GetRutasUseCase.kt

```kotlin
package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRutasUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    fun getLocalRutas(): Flow<List<Ruta>> {
        return repository.getLocalRutas()
    }

    suspend fun syncRutas(): Resource<List<Ruta>> {
        return repository.syncRutas()
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\usecase\HealthCheckUseCase.kt

```kotlin
package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class HealthCheckUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(): Resource<String> {
        return repository.pruebaConexion()
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\usecase\LoginUseCase.kt

```kotlin
package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.User
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(identificador: String, contrasena: String): Resource<User> {
        if (identificador.isBlank() || contrasena.isBlank()) {
            return Resource.Error("Los campos no pueden estar vacíos")
        }
        return repository.login(identificador, contrasena)
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\domain\util\Resource.kt

```kotlin
package com.example.ra_app.domain.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\di\DatabaseModule.kt

```kotlin
package com.example.ra_app.di

import android.content.Context
import androidx.room.Room
import com.example.ra_app.data.local.AppDatabase
import com.example.ra_app.data.local.dao.RutaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Version 1 contained risk_analyses only; preserve existing user data.
    private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `rutas` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "risk_analysis_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideRutaDao(database: AppDatabase): RutaDao {
        return database.rutaDao()
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\di\NetworkModule.kt

```kotlin
package com.example.ra_app.di

import com.riesgosra.app.BuildConfig
import com.example.ra_app.data.remote.ApiService
import com.example.ra_app.data.remote.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .followRedirects(false)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.RIESGOS_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\di\RepositoryModule.kt

```kotlin
package com.example.ra_app.di

import com.example.ra_app.data.repository.RutaRepositoryImpl
import com.example.ra_app.domain.repository.RutaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRutaRepository(
        rutaRepositoryImpl: RutaRepositoryImpl
    ): RutaRepository
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\ui\ruta\RutaUiState.kt

```kotlin
package com.example.ra_app.ui.ruta

import com.example.ra_app.domain.model.Ruta

sealed class RutaUiState {
    object Idle : RutaUiState()
    object Loading : RutaUiState()
    data class Success<T>(val data: T) : RutaUiState()
    data class Error(val message: String, val cachedRutas: List<Ruta> = emptyList()) : RutaUiState()
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\example\ra_app\ui\ruta\RutaViewModel.kt

```kotlin
package com.example.ra_app.ui.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.usecase.CrearRutaUseCase
import com.example.ra_app.domain.usecase.GetRutasUseCase
import com.example.ra_app.domain.usecase.HealthCheckUseCase
import com.example.ra_app.domain.usecase.LoginUseCase
import com.example.ra_app.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val getRutasUseCase: GetRutasUseCase,
    private val loginUseCase: LoginUseCase,
    private val crearRutaUseCase: CrearRutaUseCase,
    private val healthCheckUseCase: HealthCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RutaUiState>(RutaUiState.Idle)
    val uiState: StateFlow<RutaUiState> = _uiState.asStateFlow()

    val localRutas: StateFlow<List<Ruta>> = getRutasUseCase.getLocalRutas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun login(identificador: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = loginUseCase(identificador, contrasena)) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al iniciar sesión")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun crearRuta(nombre: String, descripcion: String) {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = crearRutaUseCase(nombre, descripcion)) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al crear ruta")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun syncRutas() {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = getRutasUseCase.syncRutas()) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al sincronizar", result.data.orEmpty())
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun probarConexion() {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = healthCheckUseCase()) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error de conexión")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RutaUiState.Idle
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\riesgosra\app\login\model\network\ApiService.kt

```kotlin
package com.riesgosra.app.login.model.network

import com.example.ra_app.data.remote.ApiService as BackendApi
import com.example.ra_app.data.remote.dto.LoginRequest
import com.riesgosra.app.login.model.data.UsuarioAutenticado
import retrofit2.HttpException
import javax.inject.Inject

/** Adaptador del presenter existente al cliente Retrofit central, sin URL PHP duplicada. */
class ApiService @Inject constructor(private val backend: BackendApi) {
    suspend fun iniciarSesion(request: InicioSesionRequest): InicioSesionResponse {
        val response = backend.login(LoginRequest(request.identificadorUsuario, request.contrasena))
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: error("La API devolvió una respuesta vacía.")
        return InicioSesionResponse(
            autenticado = body.autenticado,
            mensaje = body.mensaje,
            token = body.token,
            usuario = body.usuario?.let {
                UsuarioAutenticado(it.idUsuario, it.identificadorUsuario, it.rolUsuario.orEmpty())
            }
        )
    }

    suspend fun accederRutaProtegida(): Pair<Boolean, String> {
        val response = backend.checkRutaProtegida()
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: error("La API devolvió una respuesta vacía.")
        return body.autorizado to body.mensaje
    }
}

```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\riesgosra\app\login\model\data\SesionManager.kt

```kotlin
package com.riesgosra.app.login.model.data

import android.content.Context

class SesionManager(context: Context) {

    companion object {
        private const val NOMBRE_PREFERENCIAS = "auth_prefs"
        private const val CLAVE_TOKEN = "jwt_token"
    }

    private val preferencias = context.getSharedPreferences(
        NOMBRE_PREFERENCIAS,
        Context.MODE_PRIVATE
    )

    fun guardarToken(token: String) {
        preferencias.edit()
            .putString(CLAVE_TOKEN, token)
            .apply()
    }

    fun obtenerToken(): String? {
        return preferencias.getString(CLAVE_TOKEN, null)
    }

    fun cerrarSesion() {
        preferencias.edit()
            .remove(CLAVE_TOKEN)
            .apply()
    }
}
```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\riesgosra\app\login\presenter\LoginPresenter.kt

```kotlin
package com.riesgosra.app.login.presenter

import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.login.model.network.ApiService
import com.riesgosra.app.login.model.network.InicioSesionRequest
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException
import android.util.Log

class LoginPresenter(
    private val view: LoginView,
    private val apiService: ApiService,
    private val sesionManager: SesionManager,
    private val mainExecutor: Executor
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun iniciarSesion(
        identificadorUsuario: String,
        contrasena: String
    ) {
        view.limpiarErrores()

        val identificadorLimpio = identificadorUsuario.trim()
        var formularioValido = true

        if (identificadorLimpio.isEmpty()) {
            view.mostrarErrorIdentificador(
                "Ingrese su correo electrónico o cédula."
            )
            formularioValido = false
        }

        if (contrasena.isEmpty()) {
            view.mostrarErrorContrasena("Ingrese su contraseña.")
            formularioValido = false
        }

        if (!formularioValido) return

        // No conserva un token anterior si las credenciales nuevas fallan.
        sesionManager.cerrarSesion()
        view.mostrarCargando()

        val request = InicioSesionRequest(
            identificadorUsuario = identificadorLimpio,
            contrasena = contrasena
        )

        scope.launch {
            try {
                val respuesta = apiService.iniciarSesion(request)
                mainExecutor.execute callback@{
                    if (!respuesta.autenticado) {
                        view.ocultarCargando()
                        sesionManager.cerrarSesion()
                        view.mostrarMensaje(respuesta.mensaje)
                        return@callback
                    }

                    val token = respuesta.token
                    if (token.isNullOrBlank()) {
                        view.ocultarCargando()
                        sesionManager.cerrarSesion()
                        view.mostrarError("La API no devolvió un token válido.")
                        return@callback
                    }

                    sesionManager.guardarToken(token)
                    verificarAccesoProtegido()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "Error al iniciar sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError(mensajeError(exception))
                }
            }
        }
    }

    private fun verificarAccesoProtegido() {
        scope.launch {
            try {
                val resultado = apiService.accederRutaProtegida()
                mainExecutor.execute {
                    view.ocultarCargando()
                    if (resultado.first) {
                        view.mostrarInicioSesionExitoso()
                        view.mostrarAccesoAutorizado()
                        view.abrirPantallaPrincipal()
                    } else {
                        sesionManager.cerrarSesion()
                        view.mostrarMensaje(resultado.second)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "No fue posible verificar la sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError(mensajeError(exception))
                }
            }
        }
    }

    private fun mensajeError(error: Exception): String = when (error) {
        is HttpException -> when (error.code()) {
            401 -> "Credenciales incorrectas o sesión vencida."
            403 -> "La API rechazó el acceso a esta cuenta."
            404 -> "El servidor respondió, pero no existe la ruta solicitada. Verifica la URL de la API."
            else -> "La API respondió con un error HTTP ${error.code()}."
        }
        is ConnectException -> "No se pudo conectar al servidor. Comprueba que el backend esté iniciado y que la dirección y el puerto sean correctos."
        is SocketTimeoutException -> "El servidor tardó demasiado en responder. Revisa la red y el backend."
        is UnknownHostException -> "No se encontró el servidor. Revisa la dirección de la API y la conexión."
        is JsonParseException -> "El servidor respondió con un JSON incompatible con el login."
        is IOException -> "Error de comunicación con el servidor. Revisa la red y la URL de la API."
        else -> "La respuesta de la API no pudo procesarse. Revisa el contrato del login."
    }

    fun destruir() {
        scope.cancel()
    }
}
```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\app\src\main\java\com\riesgosra\app\login\view\login\LoginActivity.kt

```kotlin
package com.riesgosra.app.login.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.riesgosra.app.R
import com.riesgosra.app.login.view.auth.RegistroActivity
import com.riesgosra.app.login.view.main.MainActivity
import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.login.model.network.ApiService
import com.riesgosra.app.login.presenter.LoginPresenter
import com.riesgosra.app.login.presenter.LoginView
import java.util.concurrent.Executor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var inputIdentificador: TextInputLayout
    private lateinit var inputContrasena: TextInputLayout

    @Inject lateinit var apiService: ApiService
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        configurarVistas()
        configurarPresenter()
        configurarEventos()
        configurarInsets()
    }

    private fun configurarVistas() {

        inputIdentificador =
            findViewById(R.id.input_identificador)

        inputContrasena =
            findViewById(R.id.input_contrasena)
    }

    private fun configurarPresenter() {



        val sesionManager =
            SesionManager(this)

        val mainExecutor = Executor { runnable ->
            runOnUiThread(runnable)
        }

        presenter = LoginPresenter(
            view = this,
            apiService = apiService,
            sesionManager = sesionManager,
            mainExecutor = mainExecutor
        )
    }

    private fun configurarEventos() {

        val txtIdentificador =
            findViewById<TextInputEditText>(
                R.id.txt_identificador
            )

        val txtContrasena =
            findViewById<TextInputEditText>(
                R.id.txt_contrasena
            )

        val btnIniciarSesion =
            findViewById<MaterialButton>(
                R.id.btn_iniciar_sesion
            )

        txtIdentificador.doAfterTextChanged {
            inputIdentificador.error = null
        }

        txtContrasena.doAfterTextChanged {
            inputContrasena.error = null
        }

        btnIniciarSesion.setOnClickListener {

            presenter.iniciarSesion(
                identificadorUsuario =
                    txtIdentificador.text
                        ?.toString()
                        .orEmpty(),

                contrasena =
                    txtContrasena.text
                        ?.toString()
                        .orEmpty()
            )
        }

        findViewById<MaterialButton>(R.id.btn_registrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    override fun mostrarCargando() {

        cambiarEstadoBoton(
            habilitado = false,
            texto = "Validando..."
        )
    }

    override fun ocultarCargando() {

        cambiarEstadoBoton(
            habilitado = true,
            texto = getString(
                R.string.btn_iniciar_sesion
            )
        )
    }

    override fun limpiarErrores() {

        inputIdentificador.error = null
        inputContrasena.error = null
    }

    override fun mostrarErrorIdentificador(
        mensaje: String
    ) {

        inputIdentificador.error = mensaje
    }

    override fun mostrarErrorContrasena(
        mensaje: String
    ) {

        inputContrasena.error = mensaje
    }

    override fun mostrarInicioSesionExitoso() {

        Toast.makeText(
            this,
            "Inicio de sesión correcto.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarAccesoAutorizado() {

        Toast.makeText(
            this,
            "Acceso autorizado a la API.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarMensaje(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarError(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun abrirPantallaPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun cambiarEstadoBoton(
        habilitado: Boolean,
        texto: String
    ) {

        val btnIniciarSesion =
            findViewById<MaterialButton>(
                R.id.btn_iniciar_sesion
            )

        btnIniciarSesion.isEnabled =
            habilitado

        btnIniciarSesion.text =
            texto
    }

    private fun configurarInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.layout_principal)
        ) { vista, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            vista.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    override fun onDestroy() {

        if (::presenter.isInitialized) {
            presenter.destruir()
        }

        super.onDestroy()
    }
}
```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\compilar.ps1

```powershell
$ErrorActionPreference = 'Stop'
$previousJavaHome = $env:JAVA_HOME
$previousJavaOptions = $env:JAVA_TOOL_OPTIONS
$previousGradleHome = $env:GRADLE_USER_HOME
try {
    $env:JAVA_HOME = Join-Path $env:USERPROFILE '.jdks\ms-17.0.20.1'
    if (!(Test-Path "$env:JAVA_HOME\bin\java.exe")) { throw 'No se encontro Java 17. Ajustar JAVA_HOME en este script.' }
    $env:JAVA_TOOL_OPTIONS = "$previousJavaOptions -Djdk.net.unixdomain.tmpdir=C:\Windows\Temp".Trim()
    $env:GRADLE_USER_HOME = 'C:\Users\Public\RA_GradleCache'
    Push-Location 'C:\Users\Public\RA_AndroidProject'
    try {
        & .\gradlew.bat -p 'C:\Users\Public\RA_AndroidProject' :app:assembleDebug :app:testDebugUnitTest --console=plain
        $buildExitCode = $LASTEXITCODE
    } finally { Pop-Location }
} finally {
    $env:GRADLE_USER_HOME = $previousGradleHome
    $env:JAVA_HOME = $previousJavaHome
    $env:JAVA_TOOL_OPTIONS = $previousJavaOptions
}
exit $buildExitCode


```

## C:\Users\USERÇ\Desktop\Copy-Proyecto_Vinculacion\RA_App\backend\iniciar_backend.ps1

```powershell
$ErrorActionPreference = 'Stop'
$backend = 'C:\Users\Public\RA_AndroidProject\backend'
$pgBin = 'C:\Program Files\PostgreSQL\18\bin'
& "$pgBin\pg_ctl.exe" -D "$backend\.local\postgres" status | Out-Null
if ($LASTEXITCODE -ne 0) {
    & "$pgBin\pg_ctl.exe" -D "$backend\.local\postgres" -l "$backend\.local\postgres.log" -o '-p 5433 -h 127.0.0.1' start -w
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar PostgreSQL de pruebas.' }
}
$listener = Get-NetTCPConnection -LocalAddress 192.168.33.221 -LocalPort 8000 -State Listen -ErrorAction SilentlyContinue
if (!$listener) {
    Start-Process -FilePath 'C:\xampp\php\php.exe' -ArgumentList @('-d','extension=pdo_pgsql','-S','192.168.33.221:8000','-t',"$backend\api") -WorkingDirectory "$backend\api" -WindowStyle Hidden -RedirectStandardOutput "$backend\.local\php.stdout.log" -RedirectStandardError "$backend\.local\php.stderr.log"
    Start-Sleep -Seconds 1
}
$response = Invoke-WebRequest 'http://192.168.33.221:8000/prueba_conexion.php' -TimeoutSec 8
Write-Output $response.Content

```

