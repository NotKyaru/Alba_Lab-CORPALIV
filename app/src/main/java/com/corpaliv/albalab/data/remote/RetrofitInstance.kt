package com.corpaliv.albalab.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // 127.0.0.1 del dispositivo se redirige al PC con "adb reverse" (backend/conectar-dispositivo.ps1).
    // Funciona igual en el emulador y en un teléfono por USB, sin depender de la red ni de la IP del PC.
    private const val HOST = "http://127.0.0.1"

    private fun crear(puerto: Int): Retrofit = Retrofit.Builder()
        .baseUrl("$HOST:$puerto/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val catalogoApi: CatalogoApi by lazy { crear(8081).create(CatalogoApi::class.java) }
    val institucionalApi: InstitucionalApi by lazy { crear(8082).create(InstitucionalApi::class.java) }
    val registrosApi: RegistrosApi by lazy { crear(8083).create(RegistrosApi::class.java) }
}
