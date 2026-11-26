package com.corpaliv.backend.registros

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

// Mismas opciones que listaModalidades en la app.
val MODALIDADES = listOf("Donación", "Alianza empresarial", "Voluntariado", "Compra de productos", "Capacitaciones")
val ESTADOS = listOf("Pendiente", "Contactado")

@Entity
class RegistroInteres(
    @field:Id @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var nombre: String,
    var correo: String,
    var modalidad: String,
    @field:Column(length = 2000)
    var mensaje: String,
    var estado: String = "Pendiente"
)

interface RegistroRepository : JpaRepository<RegistroInteres, Int>

// Cuerpo del POST. Campos nullable para validar a mano lo que falte en el JSON.
data class RegistroNuevo(
    val nombre: String?,
    val correo: String?,
    val modalidad: String?,
    val mensaje: String?
) {
    /** Devuelve el motivo del rechazo, o null si el registro es válido. */
    fun validar(): String? = when {
        nombre.isNullOrBlank() -> "El nombre es obligatorio."
        correo.isNullOrBlank() || !correo.contains("@") -> "El correo es obligatorio y debe contener @."
        modalidad !in MODALIDADES -> "La modalidad debe ser una de: ${MODALIDADES.joinToString()}."
        mensaje.isNullOrBlank() -> "El mensaje es obligatorio."
        // Límites de las columnas: un texto más largo haría fallar la base de datos con un 500.
        nombre.length > 255 || correo.length > 255 -> "El nombre y el correo no pueden superar los 255 caracteres."
        mensaje.length > 2000 -> "El mensaje no puede superar los 2000 caracteres."
        else -> null
    }

    // Solo se llama después de validar(), por eso los !! son seguros.
    fun aRegistro() = RegistroInteres(
        nombre = nombre!!.trim(), correo = correo!!.trim(), modalidad = modalidad!!, mensaje = mensaje!!.trim()
    )
}

data class CambioEstado(val estado: String?)
