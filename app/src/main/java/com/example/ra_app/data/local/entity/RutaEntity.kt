package com.example.ra_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ra_app.domain.model.Ruta

@Entity(tableName = "rutas")
data class RutaEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val createdAt: String
) {
    fun toDomain(): Ruta {
        return Ruta(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(ruta: Ruta): RutaEntity {
            return RutaEntity(
                id = ruta.id,
                nombre = ruta.nombre,
                descripcion = ruta.descripcion,
                createdAt = ruta.createdAt
            )
        }
    }
}
