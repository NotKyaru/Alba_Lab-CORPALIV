package com.corpaliv.albalab.navigation

import com.corpaliv.albalab.data.Producto

sealed class Pantalla(val titulo: String) {
    object Inicio : Pantalla("Inicio")
    object Programas : Pantalla("Programas")
    object Catalogo : Pantalla("Catálogo")
    object Impacto : Pantalla("Impacto")
    object Colaborar : Pantalla("Colaborar")
    object Contacto : Pantalla("Contacto")
    object Registros : Pantalla("Registros")
    // Pantalla con un dato asociado (el producto a mostrar).
    data class DetalleProducto(val producto: Producto) : Pantalla("Detalle de producto")
}
