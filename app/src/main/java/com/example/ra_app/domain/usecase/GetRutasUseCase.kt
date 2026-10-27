package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRutasUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    fun getLocalRutas(): Flow<List<Ruta>> {
        return repository.getLocalRutas()
    }

    suspend fun syncRutas(): Resource<List<Ruta>> {
        return repository.syncRutas()
    }
}
