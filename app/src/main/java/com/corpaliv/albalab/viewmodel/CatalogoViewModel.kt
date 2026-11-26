package com.corpaliv.albalab.viewmodel

import androidx.lifecycle.ViewModel
import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CatalogoViewModel(private val repo: CatalogoRepository = CatalogoRepository()) : ViewModel() {

    private val _productos = MutableStateFlow<UiState<List<Producto>>>(UiState.Cargando)
    val productos: StateFlow<UiState<List<Producto>>> = _productos

    init {
        sondear(_productos) { repo.obtenerProductos() }
    }
}
