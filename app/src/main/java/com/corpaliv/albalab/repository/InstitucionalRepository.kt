package com.corpaliv.albalab.repository

import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.remote.InstitucionalApi
import com.corpaliv.albalab.data.remote.RetrofitInstance

class InstitucionalRepository(private val api: InstitucionalApi = RetrofitInstance.institucionalApi) {
    suspend fun obtenerProgramas(): List<Programa> = api.obtenerProgramas()
    suspend fun obtenerCasosExito(): List<CasoExito> = api.obtenerCasosExito()
}
