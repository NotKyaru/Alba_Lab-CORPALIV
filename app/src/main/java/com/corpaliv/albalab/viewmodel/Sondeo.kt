package com.corpaliv.albalab.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corpaliv.albalab.data.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException
import java.io.IOException

// Cada cuánto se vuelve a consultar el backend.
const val INTERVALO_SONDEO_MS = 3_000L

private const val TAG = "Sondeo"

/**
 * "Tiempo real" por sondeo: consulta el backend cada [INTERVALO_SONDEO_MS] y publica
 * el resultado en [destino]. StateFlow ignora un valor igual al anterior, así que la
 * pantalla solo se redibuja cuando un dato cambió en el servidor.
 *
 * shortcut: sondeo fijo cada 3 s; si se necesita menos latencia o consumo, pasar a SSE.
 */
fun <T> ViewModel.sondear(destino: MutableStateFlow<UiState<T>>, cargar: suspend () -> T) {
    viewModelScope.launch {
        while (isActive) {
            destino.value = try {
                UiState.Exito(cargar())
            } catch (e: IOException) {
                Log.w(TAG, "Sin conexión con el backend", e) // visible en Logcat con el filtro "Sondeo"
                UiState.Error("Sin conexión con el servidor. Reintentando…")
            } catch (e: HttpException) {
                Log.w(TAG, "El backend respondió ${e.code()}", e)
                UiState.Error("El servidor respondió con un error (${e.code()}). Reintentando…")
            }
            delay(INTERVALO_SONDEO_MS)
        }
    }
}
