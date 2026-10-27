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
