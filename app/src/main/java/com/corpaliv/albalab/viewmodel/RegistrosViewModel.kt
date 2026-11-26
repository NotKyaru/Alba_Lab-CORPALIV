package com.corpaliv.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.RegistrosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegistrosViewModel(private val repo: RegistrosRepository = RegistrosRepository()) : ViewModel() {

    private val _registros = MutableStateFlow<UiState<List<RegistroInteres>>>(UiState.Cargando)
    val registros: StateFlow<UiState<List<RegistroInteres>>> = _registros

    init {
        sondear(_registros) { repo.obtenerRegistros() }
    }

    /** Envía el registro al backend. Llama a [onExito] o a [onError] con un mensaje para el usuario. */
    fun enviar(registro: RegistroInteres, onExito: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val creado = repo.enviarRegistro(registro)
                // Se muestra al tiro, sin esperar al próximo sondeo ni hacer otra petición.
                (_registros.value as? UiState.Exito)?.let { _registros.value = UiState.Exito(it.datos + creado) }
                onExito()
            } catch (e: IOException) {
                onError("Sin conexión con el servidor. Intenta de nuevo.")
            } catch (e: HttpException) {
                onError("El servidor rechazó la solicitud (código ${e.code()}).")
            }
        }
    }
}
