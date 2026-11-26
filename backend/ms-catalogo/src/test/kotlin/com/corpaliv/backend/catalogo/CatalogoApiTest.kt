package com.corpaliv.backend.catalogo

import com.corpaliv.backend.common.JsonStore
import com.google.gson.Gson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.io.File
import kotlin.io.path.createTempDirectory

class CatalogoApiTest : StringSpec({

    val gson = Gson()

    // Cada test arranca con su propia copia de la semilla en un directorio temporal.
    fun conCatalogo(bloque: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        val archivo = File(createTempDirectory("catalogo").toFile(), "productos.json")
        application { modulo(JsonStore(archivo, "/seed/productos.json", Producto::class.java)) }
        bloque()
    }

    fun productos(json: String): List<Producto> = gson.fromJson(json, Array<Producto>::class.java).toList()

    "GET /productos devuelve la semilla" {
        conCatalogo {
            val respuesta = client.get("/productos")
            respuesta.status shouldBe HttpStatusCode.OK
            productos(respuesta.bodyAsText()).size shouldBe 4
        }
    }

    "GET /productos/{id} responde 404 si no existe" {
        conCatalogo {
            client.get("/productos/99").status shouldBe HttpStatusCode.NotFound
        }
    }

    "POST crea un producto con el siguiente id" {
        conCatalogo {
            val respuesta = client.post("/productos") {
                contentType(ContentType.Application.Json)
                setBody("""{"nombre": "Tazones", "descripcion": "Cerámica", "precio": 5000}""")
            }
            respuesta.status shouldBe HttpStatusCode.Created
            gson.fromJson(respuesta.bodyAsText(), Producto::class.java).id shouldBe 5
        }
    }

    "POST rechaza un producto sin nombre o con precio negativo" {
        conCatalogo {
            client.post("/productos") {
                contentType(ContentType.Application.Json)
                setBody("""{"precio": 1000}""")
            }.status shouldBe HttpStatusCode.BadRequest

            client.post("/productos") {
                contentType(ContentType.Application.Json)
                setBody("""{"nombre": "X", "precio": -1}""")
            }.status shouldBe HttpStatusCode.BadRequest
        }
    }

    "PUT cambia el precio y el siguiente GET ya lo refleja" {
        conCatalogo {
            client.put("/productos/1") {
                contentType(ContentType.Application.Json)
                setBody("""{"nombre": "Velas artesanales", "descripcion": "Hechas a mano", "precio": 9990}""")
            }.status shouldBe HttpStatusCode.OK

            val producto = gson.fromJson(client.get("/productos/1").bodyAsText(), Producto::class.java)
            producto.precio shouldBe 9990
        }
    }

    "DELETE elimina el producto y responde 404 la segunda vez" {
        conCatalogo {
            client.delete("/productos/2").status shouldBe HttpStatusCode.NoContent
            client.delete("/productos/2").status shouldBe HttpStatusCode.NotFound
            productos(client.get("/productos").bodyAsText()).size shouldBe 3
        }
    }
})
