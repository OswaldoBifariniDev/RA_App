package com.riesgosra.app.login.model.network

import com.riesgosra.app.login.model.data.UsuarioAutenticado

data class InicioSesionResponse(
    val autenticado: Boolean,
    val mensaje: String,
    val token: String?,
    val usuario: UsuarioAutenticado?
)