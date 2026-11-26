package com.corpaliv.backend.registros

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper

@SpringBootApplication
class RegistrosApplication {

    // Primera ejecución: carga el registro ficticio de resources/seed si la tabla está vacía.
    @Bean
    fun cargarSemilla(repo: RegistroRepository, json: JsonMapper) = ApplicationRunner {
        if (repo.count() == 0L) {
            ClassPathResource("seed/registros.json").inputStream.use {
                repo.saveAll(json.readValue(it, object : TypeReference<List<RegistroInteres>>() {}))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<RegistrosApplication>(*args)
}
