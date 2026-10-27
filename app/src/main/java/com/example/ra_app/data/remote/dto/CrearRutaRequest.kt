package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class CrearRutaRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String
)
