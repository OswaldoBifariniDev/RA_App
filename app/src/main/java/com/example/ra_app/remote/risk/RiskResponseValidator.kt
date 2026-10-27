package com.example.ra_app.remote.risk

class RiskServiceException(message: String, val canRetry: Boolean, val httpCode: Int? = null) : Exception(message)

object RiskResponseValidator {
    val categories = listOf("electrico", "estructural", "quimico", "obstruccion", "incendio", "sismo", "inundacion", "sin_riesgo", "no_evaluable")
    fun isValid(value: RiskJsonResponse?): Boolean = value != null &&
        value.riesgo in categories &&
        value.confianza.isFinite() && value.confianza in 0f..1f &&
        !value.descripcion.isNullOrBlank() && !value.sugerencias.isNullOrEmpty() &&
        value.sugerencias.all { !it.isNullOrBlank() }
}
