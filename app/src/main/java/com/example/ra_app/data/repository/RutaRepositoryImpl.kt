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
                    Resource.Error(body?.mensaje ?: "Error de autenticación")
                }
            } else {
                Resource.Error("Error en el servidor (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message ?: "Sin conexión"}")
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
                Resource.Error("Error de autorización (${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
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
            Resource.Error("Conexión fallida: ${e.message}")
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
            Resource.Error("Sincronización fallida: ${e.message}")
        }
    }
}
