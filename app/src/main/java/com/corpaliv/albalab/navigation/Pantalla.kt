package com.corpaliv.albalab.navigation

// navegacion de la app basada en una "sealed class" + "when" (sealed classes)

sealed class Pantalla(val titulo: String) {
    object Inicio : Pantalla("Inicio")
    object Programas : Pantalla("Programas")
    object Catalogo : Pantalla("Catálogo")
    object Impacto : Pantalla("Impacto")
    object Colaborar : Pantalla("Colaborar")
    object Contacto : Pantalla("Contacto")
    object Registros : Pantalla("Registros")
}