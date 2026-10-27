package com.example.ra_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// SUPUESTO JSON: los nombres SerializedName son el contrato esperado; confirmar con Node.js.
data class HealthCheckResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)
