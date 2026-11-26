package com.corpaliv.albalab.repository

import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.remote.RegistrosApi
import com.corpaliv.albalab.data.remote.RetrofitInstance

class RegistrosRepository(private val api: RegistrosApi = RetrofitInstance.registrosApi) {
    suspend fun obtenerRegistros(): List<RegistroInteres> = api.obtenerRegistros()

    // Si el backend rechaza el registro (400), Retrofit lanza HttpException.
    suspend fun enviarRegistro(registro: RegistroInteres): RegistroInteres = api.enviarRegistro(registro)
}
