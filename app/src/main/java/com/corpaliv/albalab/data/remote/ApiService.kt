package com.corpaliv.albalab.data.remote

import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.model.RegistroInteres
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Una interfaz por microservicio. Retrofit genera la implementación.

interface CatalogoApi { // ms-catalogo, puerto 8081
    @GET("productos")
    suspend fun obtenerProductos(): List<Producto>
}

interface InstitucionalApi { // ms-institucional, puerto 8082
    @GET("programas")
    suspend fun obtenerProgramas(): List<Programa>

    @GET("casos-exito")
    suspend fun obtenerCasosExito(): List<CasoExito>
}

interface RegistrosApi { // ms-registros, puerto 8083
    @GET("registros")
    suspend fun obtenerRegistros(): List<RegistroInteres>

    @POST("registros")
    suspend fun enviarRegistro(@Body registro: RegistroInteres): RegistroInteres
}
