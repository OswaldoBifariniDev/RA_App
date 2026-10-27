package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class HealthCheckUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(): Resource<String> {
        return repository.pruebaConexion()
    }
}
