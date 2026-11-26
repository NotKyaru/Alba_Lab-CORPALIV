package com.corpaliv.backend.institucional

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class Programa(
    @field:Id @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var nombre: String,
    @field:Column(length = 1000)
    var descripcion: String
)

@Entity
class CasoExito(
    @field:Id @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var titulo: String,
    @field:Column(length = 1000)
    var descripcion: String,
    var indicador: String
)

interface ProgramaRepository : JpaRepository<Programa, Int>

interface CasoExitoRepository : JpaRepository<CasoExito, Int>

// Cuerpos de PUT. Campos nullable para validar a mano lo que falte en el JSON.
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
