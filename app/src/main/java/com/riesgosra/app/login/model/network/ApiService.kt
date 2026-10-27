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
