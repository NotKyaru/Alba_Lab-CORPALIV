package com.corpaliv.backend.catalogo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

// Base de datos en memoria para las pruebas; @Transactional deshace los cambios de cada prueba.
@SpringBootTest(properties = ["spring.datasource.url=jdbc:h2:mem:catalogo-test;DB_CLOSE_DELAY=-1"])
@AutoConfigureMockMvc
@Transactional
class ProductoControllerTest(@Autowired private val mvc: MockMvc) {

    @Test
    fun `GET productos devuelve la semilla`() {
        mvc.get("/productos").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(4) }
            jsonPath("$[0].nombre") { value("Velas artesanales") }
        }
    }

    @Test
    fun `GET de un id inexistente responde 404`() {
        mvc.get("/productos/999").andExpect { status { isNotFound() } }
    }

    @Test
    fun `POST crea un producto`() {
        mvc.post("/productos") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre": "Tazones", "descripcion": "Cerámica", "precio": 5000}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.nombre") { value("Tazones") }
        }
    }

    @Test
    fun `POST rechaza un producto sin nombre o con precio negativo`() {
        mvc.post("/productos") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"precio": 1000}"""
        }.andExpect { status { isBadRequest() } }

        mvc.post("/productos") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre": "X", "precio": -1}"""
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun `PUT cambia el precio y el siguiente GET ya lo refleja`() {
        mvc.put("/productos/1") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre": "Velas artesanales", "descripcion": "Hechas a mano", "precio": 9990}"""
        }.andExpect { status { isOk() } }

        mvc.get("/productos/1").andExpect { jsonPath("$.precio") { value(9990) } }
    }

    @Test
    fun `DELETE elimina el producto y responde 404 la segunda vez`() {
        mvc.delete("/productos/2").andExpect { status { isNoContent() } }
        mvc.delete("/productos/2").andExpect { status { isNotFound() } }
        mvc.get("/productos").andExpect { jsonPath("$.length()") { value(3) } }
    }
}
