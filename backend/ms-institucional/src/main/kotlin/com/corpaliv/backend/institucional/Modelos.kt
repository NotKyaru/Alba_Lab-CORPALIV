package com.corpaliv.backend.institucional

data class Programa(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

data class CasoExito(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val indicador: String
)

// Cuerpos de PUT. Campos nullable porque Gson deja en null lo que falte en el JSON.
data class ProgramaEntrada(val nombre: String?, val descripcion: String?) {
    fun aPrograma(id: Int): Programa? =
        if (nombre.isNullOrBlank() || descripcion.isNullOrBlank()) null
        else Programa(id, nombre.trim(), descripcion.trim())
}

data class CasoExitoEntrada(val titulo: String?, val descripcion: String?, val indicador: String?) {
    fun aCasoExito(id: Int): CasoExito? =
        if (titulo.isNullOrBlank() || descripcion.isNullOrBlank() || indicador.isNullOrBlank()) null
        else CasoExito(id, titulo.trim(), descripcion.trim(), indicador.trim())
}
