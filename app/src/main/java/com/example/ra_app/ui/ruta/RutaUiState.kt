package com.example.ra_app.ui.ruta

import com.example.ra_app.domain.model.Ruta

sealed class RutaUiState {
    object Idle : RutaUiState()
    object Loading : RutaUiState()
    data class Success<T>(val data: T) : RutaUiState()
    data class Error(val message: String, val cachedRutas: List<Ruta> = emptyList()) : RutaUiState()
}
