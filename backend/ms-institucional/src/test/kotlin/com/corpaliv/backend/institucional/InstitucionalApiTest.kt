package com.corpaliv.backend.institucional

import com.corpaliv.backend.common.JsonStore
import com.google.gson.Gson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory

class InstitucionalApiTest : StringSpec({

    val gson = Gson()

    // Cada test arranca con su propia copia de las semillas en un directorio temporal.
    fun conInstitucional(bloque: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        val dir = createTempDirectory("institucional").toFile()
        application {
            modulo(
                JsonStore(dir.resolve("programas.json"), "/seed/programas.json", Programa::class.java),
                JsonStore(dir.resolve("casos-exito.json"), "/seed/casos-exito.json", CasoExito::class.java)
            )
        }
        bloque()
    }

    "GET /programas devuelve los 5 programas de la semilla" {
        conInstitucional {
            val respuesta = client.get("/programas")
            respuesta.status shouldBe HttpStatusCode.OK
            gson.fromJson(respuesta.bodyAsText(), Array<Programa>::class.java).size shouldBe 5
        }
    }

    "GET /casos-exito devuelve los 3 casos de la semilla" {
        conInstitucional {
            gson.fromJson(client.get("/casos-exito").bodyAsText(), Array<CasoExito>::class.java).size shouldBe 3
        }
    }

    "PUT /programas/{id} actualiza y el siguiente GET lo refleja" {
        conInstitucional {
            client.put("/programas/1") {
                contentType(ContentType.Application.Json)
                setBody("""{"nombre": "Formación laboral", "descripcion": "Nueva descripción"}""")
            }.status shouldBe HttpStatusCode.OK

            val programas = gson.fromJson(client.get("/programas").bodyAsText(), Array<Programa>::class.java)
            programas.first { it.id == 1 }.nombre shouldBe "Formación laboral"
        }
    }

    "PUT /casos-exito/{id} cambia el indicador" {
        conInstitucional {
            client.put("/casos-exito/1") {
                contentType(ContentType.Application.Json)
                setBody("""{"titulo": "Integración laboral", "descripcion": "Texto", "indicador": "150 personas formadas"}""")
            }.status shouldBe HttpStatusCode.OK

            val casos = gson.fromJson(client.get("/casos-exito").bodyAsText(), Array<CasoExito>::class.java)
            casos.first { it.id == 1 }.indicador shouldBe "150 personas formadas"
        }
    }

    "PUT rechaza datos incompletos y responde 404 si el id no existe" {
        conInstitucional {
            client.put("/programas/1") {
                contentType(ContentType.Application.Json)
                setBody("""{"nombre": "Sin descripción"}""")
            }.status shouldBe HttpStatusCode.BadRequest

            client.put("/casos-exito/99") {
                contentType(ContentType.Application.Json)
                setBody("""{"titulo": "X", "descripcion": "Y", "indicador": "Z"}""")
            }.status shouldBe HttpStatusCode.NotFound
        }
    }
})
