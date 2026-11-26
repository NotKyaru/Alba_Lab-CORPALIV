package com.corpaliv.albalab.data.model

// Estados posibles de una pantalla que carga datos del backend
// (mismo patrón que ResultadoLogin de la guía 5).
sealed class UiState<out T> {
    object Cargando : UiState<Nothing>()
    data class Exito<T>(val datos: T) : UiState<T>()
    data class Error(val mensaje: String) : UiState<Nothing>()
}
