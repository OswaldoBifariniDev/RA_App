package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class LoginRequest(
    @SerializedName("identificadorUsuario") val identificadorUsuario: String,
    @SerializedName("contrasena") val contrasena: String
)
