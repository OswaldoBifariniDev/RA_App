package com.riesgosra.app.model.network

import com.riesgosra.app.model.data.UsuarioAutenticado

data class InicioSesionResponse(
    val autenticado: Boolean,
    val mensaje: String,
    val token: String?,
    val usuario: UsuarioAutenticado?
)