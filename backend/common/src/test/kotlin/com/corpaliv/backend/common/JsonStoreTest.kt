package com.corpaliv.backend.common

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.createTempDirectory

data class Item(val id: Int, val nombre: String)

class JsonStoreTest : StringSpec({

    fun nuevoArchivo() = File(createTempDirectory("jsonstore").toFile(), "data/items.json")

    "copia la semilla si el archivo no existe" {
        val archivo = nuevoArchivo()
        val store = JsonStore(archivo, "/seed/items.json", Item::class.java)

        archivo.exists() shouldBe true
        store.leer() shouldBe listOf(Item(1, "Item ficticio"))
    }

    "modificar guarda el cambio en el archivo" {
        val archivo = nuevoArchivo()
        val store = JsonStore(archivo, "/seed/items.json", Item::class.java)

        val tamano = store.modificar { it.add(Item(2, "Nuevo")); it.size }

        tamano shouldBe 2
        JsonStore(archivo, "/seed/items.json", Item::class.java).leer().last() shouldBe Item(2, "Nuevo")
    }

    "refleja una edición manual del archivo sin reiniciar" {
        val archivo = nuevoArchivo()
        val store = JsonStore(archivo, "/seed/items.json", Item::class.java)

        archivo.writeText("""[{"id": 1, "nombre": "Editado a mano"}]""")

        store.leer() shouldBe listOf(Item(1, "Editado a mano"))
    }

    "un archivo vacío se lee como lista vacía" {
        val archivo = nuevoArchivo()
        val store = JsonStore(archivo, "/seed/items.json", Item::class.java)

        archivo.writeText("")

        store.leer().shouldBeEmpty()
    }
})
