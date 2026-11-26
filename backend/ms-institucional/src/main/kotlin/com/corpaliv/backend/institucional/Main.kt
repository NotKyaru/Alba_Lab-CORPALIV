package com.corpaliv.backend.institucional

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
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.io.File

const val PUERTO = 8082

fun main() {
    val programas = JsonStore(File("data/programas.json"), "/seed/programas.json", Programa::class.java)
    val casos = JsonStore(File("data/casos-exito.json"), "/seed/casos-exito.json", CasoExito::class.java)
    // 0.0.0.0: acepta conexiones locales (emulador o teléfono vía adb reverse) y de la red Wi-Fi.
    embeddedServer(Netty, port = PUERTO, host = "0.0.0.0") { modulo(programas, casos) }.start(wait = true)
}

fun Application.modulo(programas: JsonStore<Programa>, casos: JsonStore<CasoExito>) {
    install(ContentNegotiation) { gson() }

    routing {
        get("/programas") {
            call.respond(programas.leer())
        }

        put("/programas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
            val actualizado = call.receive<ProgramaEntrada>().aPrograma(id)
                ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError("nombre y descripcion son obligatorios"))
            val existia = programas.modificar { lista ->
                val i = lista.indexOfFirst { it.id == id }
                if (i >= 0) lista[i] = actualizado
                i >= 0
            }
            if (existia) call.respond(actualizado)
            else call.respond(HttpStatusCode.NotFound, mensajeError("Programa no encontrado"))
        }

        get("/casos-exito") {
            call.respond(casos.leer())
        }

        put("/casos-exito/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError("id inválido"))
            val actualizado = call.receive<CasoExitoEntrada>().aCasoExito(id)
                ?: return@put call.respond(HttpStatusCode.BadRequest, mensajeError("titulo, descripcion e indicador son obligatorios"))
            val existia = casos.modificar { lista ->
                val i = lista.indexOfFirst { it.id == id }
                if (i >= 0) lista[i] = actualizado
                i >= 0
            }
            if (existia) call.respond(actualizado)
            else call.respond(HttpStatusCode.NotFound, mensajeError("Caso de éxito no encontrado"))
        }
    }
}

private fun mensajeError(mensaje: String) = mapOf("error" to mensaje)
