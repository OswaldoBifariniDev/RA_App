package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class LoginResponse(
    @SerializedName("autenticado") val autenticado: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("usuario") val usuario: UsuarioDto? = null
)
