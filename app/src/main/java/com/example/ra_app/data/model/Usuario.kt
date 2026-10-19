package com.example.ra_app.data.model

data class Usuario(
    val idUsuario: String = "",
    val nombreApellido: String = "",
    val numeroIdentificacion: String = "",
    val correoElectronico: String = "",
    val contrasena: String = "" // Antes de persistirla, almacenar un hash seguro.
)
