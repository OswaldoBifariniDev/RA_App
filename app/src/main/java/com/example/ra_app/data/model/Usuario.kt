package com.example.ra_app.data.model

data class Usuario(
    val idUsuario: String = "", // aqyui va el id interno d la db
    val nombreApellido: String = "",
    val numeroIdentificacion: String = "", // cedula o Pasaporte
    val correoElectronico: String = "",
    val contrasena: String = "" // lo ideal seria usar encroptacion
)