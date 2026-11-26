package com.corpaliv.backend.catalogo

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int
)

// Cuerpo de POST/PUT. Campos nullable porque Gson deja en null lo que falte en el JSON.
data class ProductoEntrada(
    val nombre: String?,
    val descripcion: String?,
    val precio: Int?
) {
    fun aProducto(id: Int): Producto? =
        if (nombre.isNullOrBlank() || precio == null || precio < 0) null
        else Producto(id, nombre.trim(), descripcion.orEmpty().trim(), precio)
}
