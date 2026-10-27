package com.example.ra_app.data.remote.dto

import com.example.ra_app.data.local.entity.RutaEntity
import com.example.ra_app.domain.model.Ruta
import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class RutaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    fun toDomain(): Ruta {
        return Ruta(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt ?: ""
        )
    }

    fun toEntity(): RutaEntity {
        return RutaEntity(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt ?: ""
        )
    }
}
