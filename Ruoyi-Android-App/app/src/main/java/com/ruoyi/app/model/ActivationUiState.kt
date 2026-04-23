package com.ruoyi.app.model

sealed interface ActivationUiState {
    object Idle : ActivationUiState
    object Loading : ActivationUiState
    data class Success(val message: String) : ActivationUiState
    data class Error(val message: String) : ActivationUiState
}
