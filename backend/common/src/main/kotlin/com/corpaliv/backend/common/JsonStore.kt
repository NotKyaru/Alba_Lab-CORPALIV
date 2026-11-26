package com.corpaliv.backend.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/**
 * Lista de [T] guardada en un archivo JSON. La comparten los 3 microservicios.
 *
 * Relee el archivo en cada operación: si alguien lo edita a mano, el siguiente
 * GET ya devuelve el dato nuevo (base del "tiempo real" de la app).
 */
class JsonStore<T>(
    private val archivo: File,
    semilla: String,
    clase: Class<T>
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val tipo = TypeToken.getParameterized(MutableList::class.java, clase).type
    private val mutex = Mutex() // evita que dos requests escriban el archivo al mismo tiempo

    init {
        if (!archivo.exists()) {
            // Primera ejecución: copia los datos ficticios versionados en resources/seed.
            val recurso = requireNotNull(javaClass.getResource(semilla)) { "Semilla no encontrada: $semilla" }
            archivo.absoluteFile.parentFile.mkdirs()
            archivo.writeText(recurso.readText())
        }
    }

    suspend fun leer(): List<T> = mutex.withLock { cargar() }

    /** Aplica [cambio] sobre la lista, guarda el archivo y devuelve lo que retorne [cambio]. */
    suspend fun <R> modificar(cambio: (MutableList<T>) -> R): R = mutex.withLock {
        val lista = cargar()
        cambio(lista).also { archivo.writeText(gson.toJson(lista)) }
    }

    // Un archivo vacío se trata como lista vacía en vez de fallar.
    private fun cargar(): MutableList<T> = gson.fromJson(archivo.readText(), tipo) ?: mutableListOf()
}
