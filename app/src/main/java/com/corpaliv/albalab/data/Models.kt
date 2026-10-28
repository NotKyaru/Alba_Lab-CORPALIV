package com.corpaliv.albalab.data

// modelos de datos (data class)

data class Programa(
    val nombre: String,
    val descripcion: String
)

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precio: Int
)

data class CasoExito(
    val titulo: String,
    val descripcion: String,
    val indicador: String
)

data class RegistroInteres(
    val nombre: String,
    val correo: String,
    val modalidad: String,
    val mensaje: String
)