package com.example.ra_app.remote.risk

import com.google.gson.annotations.SerializedName

data class RiskJsonResponse(
    @SerializedName("riesgo") val riesgo: String,
    @SerializedName("confianza") val confianza: Float,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("sugerencias") val sugerencias: List<String>
)

data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig
)

data class GeminiContent(
    @SerializedName("role") val role: String = "user",
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text") val text: String? = null,
    @SerializedName("inlineData") val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    @SerializedName("mimeType") val mimeType: String = "image/jpeg",
    @SerializedName("data") val data: String
)

data class GeminiGenerationConfig(
    @SerializedName("temperature") val temperature: Float = 0.2f,
    @SerializedName("responseMimeType") val responseMimeType: String = "application/json",
    @SerializedName("responseSchema") val responseSchema: GeminiResponseSchema
)

data class GeminiResponseSchema(
    @SerializedName("type") val type: String = "OBJECT",
    @SerializedName("properties") val properties: Map<String, GeminiSchemaProperty>,
    @SerializedName("required") val required: List<String>
)

data class GeminiSchemaProperty(
    @SerializedName("type") val type: String,
    @SerializedName("enum") val enumValues: List<String>? = null,
    @SerializedName("items") val items: GeminiSchemaItems? = null
)

data class GeminiSchemaItems(
    @SerializedName("type") val type: String = "STRING"
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContentResponse?
)

data class GeminiContentResponse(
    @SerializedName("parts") val parts: List<GeminiPartResponse>?
)

data class GeminiPartResponse(
    @SerializedName("text") val text: String?
)
