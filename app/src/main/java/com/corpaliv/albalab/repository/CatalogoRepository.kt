package com.corpaliv.albalab.repository

import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.remote.CatalogoApi
import com.corpaliv.albalab.data.remote.RetrofitInstance

// La api se recibe por parámetro para poder reemplazarla por un mock en las pruebas.
class CatalogoRepository(private val api: CatalogoApi = RetrofitInstance.catalogoApi) {
    suspend fun obtenerProductos(): List<Producto> = api.obtenerProductos()
}
