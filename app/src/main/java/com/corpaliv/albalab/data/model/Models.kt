package com.corpaliv.albalab.data.model

// Modelos de datos (data class). Los campos coinciden con el JSON de los microservicios.
// id y estado van al final con valor por defecto: los asigna el backend, no la app.

data class Programa(
    val nombre: String,
    val descripcion: String,
    val id: Int = 0
)

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val id: Int = 0
)

data class CasoExito(
    val titulo: String,
    val descripcion: String,
    val indicador: String,
    val id: Int = 0
)

data class RegistroInteres(
    val nombre: String,
    val correo: String,
    val modalidad: String,
    val mensaje: String,
    val id: Int = 0,
    val estado: String = "Pendiente"
)
