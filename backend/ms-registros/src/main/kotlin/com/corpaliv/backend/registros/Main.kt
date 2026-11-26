package com.corpaliv.backend.registros

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
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File

const val PUERTO = 8083

fun main() {
    val store = JsonStore(File("data/registros.json"), "/seed/registros.json", RegistroInteres::class.java)
    // 0.0.0.0: acepta conexiones locales (emulador o teléfono vía adb reverse) y de la red Wi-Fi.
    embeddedServer(Netty, port = PUERTO, host = "0.0.0.0") { modulo(store) }.start(wait = true)
}

fun Application.modulo(store: JsonStore<RegistroInteres>) {
    install(ContentNegotiation) { gson() }

    routing {
        route("/registros") {
            get {
                call.respond(store.leer())
            }

            post {
                val nuevo = call.receive<RegistroNuevo>()
                nuevo.validar()?.let { motivo ->
                    return@post call.respond(HttpStatusCode.BadRequest, mensajeError(motivo))
                }
                val creado = store.modificar { lista ->
                    nuevo.aRegistro((lista.maxOfOrNull { it.id } ?: 0) + 1).also { lista.add(it) }
                }
                call.respond(HttpStatusCode.Created, creado)
            }

            patch("/{id}/estado") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
                val estado = call.receive<CambioEstado>().estado
                if (estado !in ESTADOS) {
                    return@patch call.respond(
                        HttpStatusCode.BadRequest,
                        mensajeError("El estado debe ser uno de: ${ESTADOS.joinToString()}.")
                    )
                }
                val actualizado = store.modificar { lista ->
                    val i = lista.indexOfFirst { it.id == id }
                    if (i >= 0) lista[i] = lista[i].copy(estado = estado!!)
                    lista.getOrNull(i)
                } ?: return@patch call.respond(HttpStatusCode.NotFound, mensajeError("Registro no encontrado"))
                call.respond(actualizado)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
                val borrado = store.modificar { lista -> lista.removeIf { it.id == id } }
                if (borrado) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound, mensajeError("Registro no encontrado"))
            }
        }
    }
}

private fun mensajeError(mensaje: String) = mapOf("error" to mensaje)
