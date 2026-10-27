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
