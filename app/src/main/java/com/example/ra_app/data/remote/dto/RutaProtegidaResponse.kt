package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class RutaProtegidaResponse(
    @SerializedName("autorizado") val autorizado: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("rutas") val rutas: List<RutaDto>? = null
)
