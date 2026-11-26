package com.corpaliv.backend.registros

// Mismas opciones que listaModalidades en la app.
val MODALIDADES = listOf("Donación", "Alianza empresarial", "Voluntariado", "Compra de productos", "Capacitaciones")
val ESTADOS = listOf("Pendiente", "Contactado")

data class RegistroInteres(
    val id: Int,
    val nombre: String,
    val correo: String,
    val modalidad: String,
    val mensaje: String,
    val estado: String
)

// Cuerpo del POST. Campos nullable porque Gson deja en null lo que falte en el JSON.
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
        else -> null
    }

    // Solo se llama después de validar(), por eso los !! son seguros.
    fun aRegistro(id: Int) =
        RegistroInteres(id, nombre!!.trim(), correo!!.trim(), modalidad!!, mensaje!!.trim(), "Pendiente")
}

data class CambioEstado(val estado: String?)
