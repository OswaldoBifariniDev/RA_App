package com.example.ra_app.remote.risk

interface RiskVisionService {
    suspend fun analyzeImage(base64Image: String): Result<RiskJsonResponse>
}
