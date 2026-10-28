package com.example.ra_app.data.remote.dto

data class RegistroRequest(
    val nombreApellido: String,
    val numeroIdentificacion: String,
    val correoElectronico: String,
    val contrasena: String
)

data class RegistroResponse(val registrado: Boolean, val mensaje: String, val idUsuario: Int? = null)
