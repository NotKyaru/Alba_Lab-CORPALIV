package com.corpaliv.backend.registros

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

// Base de datos en memoria para las pruebas; @Transactional deshace los cambios de cada prueba.
@SpringBootTest(properties = ["spring.datasource.url=jdbc:h2:mem:registros-test;DB_CLOSE_DELAY=-1"])
@AutoConfigureMockMvc
@Transactional
class RegistroControllerTest(@Autowired private val mvc: MockMvc) {

    private fun enviar(json: String): ResultActionsDsl = mvc.post("/registros") {
        contentType = MediaType.APPLICATION_JSON
        content = json
    }

    @Test
    fun `POST guarda un registro valido como Pendiente y aparece en el GET`() {
        enviar("""{"nombre": "Ana Ficticia", "correo": "ana@example.com", "modalidad": "Donación", "mensaje": "Quiero ayudar"}""")
            .andExpect {
                status { isCreated() }
                jsonPath("$.estado") { value("Pendiente") }
                jsonPath("$.modalidad") { value("Donación") }
            }

        mvc.get("/registros").andExpect { jsonPath("$.length()") { value(2) } }
    }

    @Test
    fun `POST rechaza datos incompletos o invalidos con un motivo claro`() {
        enviar("""{"correo": "a@example.com", "modalidad": "Voluntariado", "mensaje": "Hola"}""").andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value(containsString("nombre")) }
        }
        enviar("""{"nombre": "Ana", "correo": "sin-arroba", "modalidad": "Voluntariado", "mensaje": "Hola"}""")
            .andExpect { status { isBadRequest() } }
        enviar("""{"nombre": "Ana", "correo": "a@example.com", "modalidad": "Otra", "mensaje": "Hola"}""")
            .andExpect { status { isBadRequest() } }
        enviar("""{"nombre": "Ana", "correo": "a@example.com", "modalidad": "Voluntariado", "mensaje": " "}""")
            .andExpect { status { isBadRequest() } }
        enviar("""{"nombre": "${"x".repeat(300)}", "correo": "a@example.com", "modalidad": "Voluntariado", "mensaje": "Hola"}""")
            .andExpect { status { isBadRequest() } }

        // Nada de lo rechazado quedó guardado.
        mvc.get("/registros").andExpect { jsonPath("$.length()") { value(1) } }
    }

    @Test
    fun `PATCH cambia el estado a Contactado y rechaza estados desconocidos`() {
        mvc.patch("/registros/1/estado") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"estado": "Contactado"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.estado") { value("Contactado") }
        }

        mvc.patch("/registros/1/estado") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"estado": "Archivado"}"""
        }.andExpect { status { isBadRequest() } }

        mvc.patch("/registros/99/estado") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"estado": "Contactado"}"""
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun `DELETE elimina el registro y responde 404 la segunda vez`() {
        mvc.delete("/registros/1").andExpect { status { isNoContent() } }
        mvc.delete("/registros/1").andExpect { status { isNotFound() } }
    }
}
