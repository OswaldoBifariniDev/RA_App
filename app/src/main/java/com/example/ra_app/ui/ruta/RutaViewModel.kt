package com.example.ra_app.ui.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ra_app.domain.model.Ruta
import com.example.ra_app.domain.usecase.CrearRutaUseCase
import com.example.ra_app.domain.usecase.GetRutasUseCase
import com.example.ra_app.domain.usecase.HealthCheckUseCase
import com.example.ra_app.domain.usecase.LoginUseCase
import com.example.ra_app.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val getRutasUseCase: GetRutasUseCase,
    private val loginUseCase: LoginUseCase,
    private val crearRutaUseCase: CrearRutaUseCase,
    private val healthCheckUseCase: HealthCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RutaUiState>(RutaUiState.Idle)
    val uiState: StateFlow<RutaUiState> = _uiState.asStateFlow()

    val localRutas: StateFlow<List<Ruta>> = getRutasUseCase.getLocalRutas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun login(identificador: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = loginUseCase(identificador, contrasena)) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al iniciar sesión")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun crearRuta(nombre: String, descripcion: String) {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = crearRutaUseCase(nombre, descripcion)) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al crear ruta")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun syncRutas() {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = getRutasUseCase.syncRutas()) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error al sincronizar", result.data.orEmpty())
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun probarConexion() {
        viewModelScope.launch {
            _uiState.value = RutaUiState.Loading
            when (val result = healthCheckUseCase()) {
                is Resource.Success -> {
                    _uiState.value = RutaUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RutaUiState.Error(result.message ?: "Error de conexión")
                }
                is Resource.Loading -> {
                    _uiState.value = RutaUiState.Loading
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RutaUiState.Idle
    }
}
