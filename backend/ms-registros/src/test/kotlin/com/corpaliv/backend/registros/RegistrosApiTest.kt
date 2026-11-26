package com.corpaliv.backend.registros

import com.corpaliv.backend.common.JsonStore
import com.google.gson.Gson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.io.File
import kotlin.io.path.createTempDirectory

class RegistrosApiTest : StringSpec({

    val gson = Gson()

    // Cada test arranca con su propia copia de la semilla en un directorio temporal.
    fun conRegistros(bloque: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        val archivo = File(createTempDirectory("registros").toFile(), "registros.json")
        application { modulo(JsonStore(archivo, "/seed/registros.json", RegistroInteres::class.java)) }
        bloque()
    }

    suspend fun ApplicationTestBuilder.enviar(json: String): HttpResponse = client.post("/registros") {
        contentType(ContentType.Application.Json)
        setBody(json)
    }

    "POST guarda un registro válido como Pendiente y aparece en el GET" {
        conRegistros {
            val respuesta = enviar(
                """{"nombre": "Ana Ficticia", "correo": "ana@example.com", "modalidad": "Voluntariado", "mensaje": "Quiero ayudar"}"""
            )
            respuesta.status shouldBe HttpStatusCode.Created
            val creado = gson.fromJson(respuesta.bodyAsText(), RegistroInteres::class.java)
            creado.id shouldBe 2
            creado.estado shouldBe "Pendiente"

            gson.fromJson(client.get("/registros").bodyAsText(), Array<RegistroInteres>::class.java).size shouldBe 2
        }
    }

    "POST rechaza datos incompletos o inválidos con un motivo claro" {
        conRegistros {
            enviar("""{"correo": "a@example.com", "modalidad": "Voluntariado", "mensaje": "Hola"}""").let {
                it.status shouldBe HttpStatusCode.BadRequest
                it.bodyAsText() shouldContain "nombre"
            }
            enviar("""{"nombre": "Ana", "correo": "sin-arroba", "modalidad": "Voluntariado", "mensaje": "Hola"}""")
                .status shouldBe HttpStatusCode.BadRequest
            enviar("""{"nombre": "Ana", "correo": "a@example.com", "modalidad": "Otra", "mensaje": "Hola"}""")
                .status shouldBe HttpStatusCode.BadRequest
            enviar("""{"nombre": "Ana", "correo": "a@example.com", "modalidad": "Voluntariado", "mensaje": " "}""")
                .status shouldBe HttpStatusCode.BadRequest

            // Nada de lo rechazado quedó guardado.
            gson.fromJson(client.get("/registros").bodyAsText(), Array<RegistroInteres>::class.java).size shouldBe 1
        }
    }

    "PATCH cambia el estado a Contactado y rechaza estados desconocidos" {
        conRegistros {
            val ok = client.patch("/registros/1/estado") {
                contentType(ContentType.Application.Json)
                setBody("""{"estado": "Contactado"}""")
            }
            ok.status shouldBe HttpStatusCode.OK
            gson.fromJson(ok.bodyAsText(), RegistroInteres::class.java).estado shouldBe "Contactado"

            client.patch("/registros/1/estado") {
                contentType(ContentType.Application.Json)
                setBody("""{"estado": "Archivado"}""")
            }.status shouldBe HttpStatusCode.BadRequest

            client.patch("/registros/99/estado") {
                contentType(ContentType.Application.Json)
                setBody("""{"estado": "Contactado"}""")
            }.status shouldBe HttpStatusCode.NotFound
        }
    }

    "DELETE elimina el registro y responde 404 la segunda vez" {
        conRegistros {
            client.delete("/registros/1").status shouldBe HttpStatusCode.NoContent
            client.delete("/registros/1").status shouldBe HttpStatusCode.NotFound
        }
    }
})
