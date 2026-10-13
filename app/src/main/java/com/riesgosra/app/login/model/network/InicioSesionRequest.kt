package com.riesgosra.app.login.model.network

data class InicioSesionRequest(
    val identificadorUsuario: String,
    val contrasena: String
)