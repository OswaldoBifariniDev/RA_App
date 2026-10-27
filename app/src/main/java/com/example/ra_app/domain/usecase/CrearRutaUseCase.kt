package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class CrearRutaUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(nombre: String, descripcion: String): Resource<Ruta> {
        if (nombre.isBlank()) {
            return Resource.Error("El nombre es obligatorio")
        }
        return repository.crearRuta(nombre, descripcion)
    }
}
