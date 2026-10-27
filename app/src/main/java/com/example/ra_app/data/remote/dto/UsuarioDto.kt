package com.example.ra_app.data.remote.dto

import com.example.ra_app.domain.model.User
import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class UsuarioDto(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("identificadorUsuario") val identificadorUsuario: String,
    @SerializedName("rolUsuario") val rolUsuario: String? = null
) {
    fun toDomain(token: String? = null): User {
        return User(
            id = idUsuario,
            nombre = identificadorUsuario,
            email = identificadorUsuario,
            token = token
        )
    }
}
