package com.example.ra_app.ui.risk

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ra_app.remote.risk.RiskServiceException
import com.example.ra_app.data.model.RiskAnalysis
import com.example.ra_app.data.repository.RiskAnalysisRepository
import com.example.ra_app.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RiskUiState {
    object Idle : RiskUiState()
    object Loading : RiskUiState()
    data class Success(val analysis: RiskAnalysis) : RiskUiState()
    data class Error(val message: String, val canRetry: Boolean = true) : RiskUiState()
}

class RiskViewModel(
    private val repository: RiskAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RiskUiState>(RiskUiState.Idle)
    val uiState: StateFlow<RiskUiState> = _uiState.asStateFlow()

    fun analyzeBitmap(context: Context, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = RiskUiState.Loading

            val photoPath = ImageUtils.saveBitmapToCache(context, bitmap)
            val base64Image = ImageUtils.bitmapToBase64(bitmap)

            val result = repository.analyzeAndSaveImage(base64Image, photoPath)
            if (result.isSuccess) {
                _uiState.value = RiskUiState.Success(result.getOrThrow())
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al analizar la imagen"
                val canRetry = (result.exceptionOrNull() as? RiskServiceException)?.canRetry ?: true
                _uiState.value = RiskUiState.Error(errorMsg, canRetry)
            }
        }
    }

    fun resetState() {
        _uiState.value = RiskUiState.Idle
    }

    val recentAnalyses = repository.getRecentAnalyses(20)

    class Factory(private val repository: RiskAnalysisRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RiskViewModel(repository) as T
        }
    }
}
