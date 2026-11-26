package com.corpaliv.albalab.viewmodel

import androidx.lifecycle.ViewModel
import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.InstitucionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InstitucionalViewModel(
    private val repo: InstitucionalRepository = InstitucionalRepository()
) : ViewModel() {

    private val _programas = MutableStateFlow<UiState<List<Programa>>>(UiState.Cargando)
    val programas: StateFlow<UiState<List<Programa>>> = _programas

    private val _casosExito = MutableStateFlow<UiState<List<CasoExito>>>(UiState.Cargando)
    val casosExito: StateFlow<UiState<List<CasoExito>>> = _casosExito

    init {
        sondear(_programas) { repo.obtenerProgramas() }
        sondear(_casosExito) { repo.obtenerCasosExito() }
    }
}
