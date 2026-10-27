package com.example.ra_app.data.repository

import com.example.ra_app.data.local.dao.RiskAnalysisDao
import com.example.ra_app.data.model.RiskAnalysis
import com.example.ra_app.data.model.RiskCategory
import com.example.ra_app.remote.risk.GeminiRiskService
import com.example.ra_app.remote.risk.RiskJsonResponse
import com.example.ra_app.remote.risk.RiskVisionService
import kotlinx.coroutines.flow.Flow

class RiskAnalysisRepository(
    private val visionService: RiskVisionService = GeminiRiskService(),
    private val dao: RiskAnalysisDao
) {

    suspend fun analyzeAndSaveImage(
        base64Image: String,
        photoPath: String? = null
    ): Result<RiskAnalysis> {
        // Attempt 1
        var result = visionService.analyzeImage(base64Image)

        // Retry 1 time if result is failure and error is due to invalid JSON/parse failure
        if (result.isFailure) {
            val errorMsg = result.exceptionOrNull()?.message ?: ""
            if (errorMsg.contains("JSON", ignoreCase = true) || errorMsg.contains("incompleta", ignoreCase = true)) {
                result = visionService.analyzeImage(base64Image)
            }
        }

        // Nunca registrar un fallo del proveedor como un resultado "sin riesgo".
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val finalJsonResponse = result.getOrThrow()

        val suggestionsJson = RiskAnalysis.createSuggestionsJson(finalJsonResponse.sugerencias)
        val entity = RiskAnalysis(
            category = finalJsonResponse.riesgo,
            confidence = finalJsonResponse.confianza,
            description = finalJsonResponse.descripcion,
            suggestionsJson = suggestionsJson,
            timestamp = System.currentTimeMillis(),
            photoPath = photoPath,
            isSynced = false
        )

        val id = dao.insert(entity)
        val savedEntity = entity.copy(id = id)

        return Result.success(savedEntity)
    }

    fun getRecentAnalyses(limit: Int = 20): Flow<List<RiskAnalysis>> {
        return dao.getRecent(limit)
    }

    suspend fun getAnalysisById(id: Long): RiskAnalysis? {
        return dao.getById(id)
    }
}
