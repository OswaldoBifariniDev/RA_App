package com.example.ra_app.data.repository
import com.example.ra_app.data.local.dao.RiskAnalysisDao
import com.example.ra_app.data.model.RiskAnalysis
import com.example.ra_app.remote.risk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RiskAnalysisRepositoryTest {
    private class MemoryDao : RiskAnalysisDao {
        val saved = mutableListOf<RiskAnalysis>()
        override suspend fun insert(analysis: RiskAnalysis): Long { saved.add(analysis); return 7L }
        override fun getRecent(limit: Int): Flow<List<RiskAnalysis>> = flowOf(saved)
        override suspend fun getRecentList(limit: Int): List<RiskAnalysis> = saved
        override suspend fun getPendingSync(): List<RiskAnalysis> = saved
        override suspend fun getById(id: Long): RiskAnalysis? = saved.firstOrNull()
    }
    @Test fun errorDelProveedorNoSeGuardaComoSinRiesgo() = runBlocking {
        val dao = MemoryDao()
        val service = object : RiskVisionService {
            override suspend fun analyzeImage(base64Image: String): Result<RiskJsonResponse> =
                Result.failure(Exception("Error en el servicio (404)"))
        }
        val result = RiskAnalysisRepository(service, dao).analyzeAndSaveImage("test")
        assertTrue(result.isFailure)
        assertTrue(dao.saved.isEmpty())
    }
    @Test fun resultadoValidoSeGuardaEnHistorial() = runBlocking {
        val dao = MemoryDao()
        val service = object : RiskVisionService {
            override suspend fun analyzeImage(base64Image: String): Result<RiskJsonResponse> =
                Result.success(RiskJsonResponse("electrico", .8f, "Cable expuesto", listOf("Aislar el área")))
        }
        val result = RiskAnalysisRepository(service, dao).analyzeAndSaveImage("test")
        assertTrue(result.isSuccess)
        assertEquals(1, dao.saved.size)
        assertEquals(7L, result.getOrThrow().id)
    }
}
