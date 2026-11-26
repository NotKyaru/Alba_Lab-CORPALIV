package com.corpaliv.backend.institucional

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper

@SpringBootApplication
class InstitucionalApplication {

    // Primera ejecución: carga los datos ficticios de resources/seed si las tablas están vacías.
    @Bean
    fun cargarSemilla(programas: ProgramaRepository, casos: CasoExitoRepository, json: JsonMapper) = ApplicationRunner {
        if (programas.count() == 0L) {
            ClassPathResource("seed/programas.json").inputStream.use {
                programas.saveAll(json.readValue(it, object : TypeReference<List<Programa>>() {}))
            }
        }
        if (casos.count() == 0L) {
            ClassPathResource("seed/casos-exito.json").inputStream.use {
                casos.saveAll(json.readValue(it, object : TypeReference<List<CasoExito>>() {}))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<InstitucionalApplication>(*args)
}
