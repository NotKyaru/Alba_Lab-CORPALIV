package com.corpaliv.backend.catalogo

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class Producto(
    @field:Id @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var nombre: String,
    @field:Column(length = 1000)
    var descripcion: String,
    var precio: Int
)

interface ProductoRepository : JpaRepository<Producto, Int>

// Cuerpo de POST/PUT. Campos nullable para validar a mano lo que falte en el JSON.
data class ProductoEntrada(
    val nombre: String?,
    val descripcion: String?,
    val precio: Int?
) {
    fun aProducto(id: Int? = null): Producto? =
        if (nombre.isNullOrBlank() || precio == null || precio < 0) null
        else Producto(id, nombre.trim(), descripcion.orEmpty().trim(), precio)
}
