package com.corpaliv.backend.institucional

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

// Base de datos en memoria para las pruebas; @Transactional deshace los cambios de cada prueba.
@SpringBootTest(properties = ["spring.datasource.url=jdbc:h2:mem:institucional-test;DB_CLOSE_DELAY=-1"])
@AutoConfigureMockMvc
@Transactional
class InstitucionalControllerTest(@Autowired private val mvc: MockMvc) {

    @Test
    fun `GET programas devuelve los 5 programas de la semilla`() {
        mvc.get("/programas").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(5) }
        }
    }

    @Test
    fun `GET casos-exito devuelve los 3 casos de la semilla`() {
        mvc.get("/casos-exito").andExpect { jsonPath("$.length()") { value(3) } }
    }

    @Test
    fun `PUT programas actualiza y el siguiente GET lo refleja`() {
        mvc.put("/programas/1") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre": "Formación laboral", "descripcion": "Nueva descripción"}"""
        }.andExpect { status { isOk() } }

        mvc.get("/programas").andExpect { jsonPath("$[0].nombre") { value("Formación laboral") } }
    }

    @Test
    fun `PUT casos-exito cambia el indicador`() {
        mvc.put("/casos-exito/1") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"titulo": "Integración laboral", "descripcion": "Texto", "indicador": "150 personas formadas"}"""
        }.andExpect { status { isOk() } }

        mvc.get("/casos-exito").andExpect { jsonPath("$[0].indicador") { value("150 personas formadas") } }
    }

    @Test
    fun `PUT rechaza datos incompletos y responde 404 si el id no existe`() {
        mvc.put("/programas/1") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre": "Sin descripción"}"""
        }.andExpect { status { isBadRequest() } }

        mvc.put("/casos-exito/99") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"titulo": "X", "descripcion": "Y", "indicador": "Z"}"""
        }.andExpect { status { isNotFound() } }
    }
}
