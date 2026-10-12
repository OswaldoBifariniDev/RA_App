package com.riesgosra.app.model.network

data class InicioSesionRequest(
    val identificadorUsuario: String,
    val contrasena: String
)