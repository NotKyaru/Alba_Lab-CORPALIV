package com.corpaliv.backend.catalogo

import com.corpaliv.backend.common.JsonStore
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File

const val PUERTO = 8081

fun main() {
    val store = JsonStore(File("data/productos.json"), "/seed/productos.json", Producto::class.java)
    // 0.0.0.0: acepta conexiones del emulador (10.0.2.2) y de teléfonos en la misma red Wi-Fi.
    embeddedServer(Netty, port = PUERTO, host = "0.0.0.0") { modulo(store) }.start(wait = true)
}

fun Application.modulo(store: JsonStore<Producto>) {
    install(ContentNegotiation) { gson() }

    routing {
        route("/productos") {
            get {
                call.respond(store.leer())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
                val producto = store.leer().find { it.id == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound, mensajeError("Producto no encontrado"))
                call.respond(producto)
            }

            post {
                val entrada = call.receive<ProductoEntrada>()
                val creado = store.modificar { lista ->
                    entrada.aProducto((lista.maxOfOrNull { it.id } ?: 0) + 1)?.also { lista.add(it) }
                } ?: return@post call.respond(HttpStatusCode.BadRequest, mensajeError(DATOS_INVALIDOS))
                call.respond(HttpStatusCode.Created, creado)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
                val actualizado = call.receive<ProductoEntrada>().aProducto(id)
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError(DATOS_INVALIDOS))
                val existia = store.modificar { lista ->
                    val i = lista.indexOfFirst { it.id == id }
                    if (i >= 0) lista[i] = actualizado
                    i >= 0
                }
                if (existia) call.respond(actualizado)
                else call.respond(HttpStatusCode.NotFound, mensajeError("Producto no encontrado"))
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
                val borrado = store.modificar { lista -> lista.removeIf { it.id == id } }
                if (borrado) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound, mensajeError("Producto no encontrado"))
            }
        }
    }
}

private const val DATOS_INVALIDOS = "nombre es obligatorio y precio debe ser un número mayor o igual a 0"

private fun mensajeError(mensaje: String) = mapOf("error" to mensaje)
