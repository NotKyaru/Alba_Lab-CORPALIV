package com.corpaliv.albalab.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // shortcut: IP fija del emulador; para un teléfono físico cámbiala por la IP del PC
    // (y agrégala en res/xml/network_security_config.xml).
    private const val HOST = "http://10.0.2.2"

    private fun crear(puerto: Int): Retrofit = Retrofit.Builder()
        .baseUrl("$HOST:$puerto/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val catalogoApi: CatalogoApi by lazy { crear(8081).create(CatalogoApi::class.java) }
    val institucionalApi: InstitucionalApi by lazy { crear(8082).create(InstitucionalApi::class.java) }
    val registrosApi: RegistrosApi by lazy { crear(8083).create(RegistrosApi::class.java) }
}
