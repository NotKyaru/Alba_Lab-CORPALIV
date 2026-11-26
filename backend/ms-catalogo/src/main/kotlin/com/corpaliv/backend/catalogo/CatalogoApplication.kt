package com.corpaliv.backend.catalogo

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper

@SpringBootApplication
class CatalogoApplication {

    // Primera ejecución: carga los productos ficticios de resources/seed si la tabla está vacía.
    @Bean
    fun cargarSemilla(repo: ProductoRepository, json: JsonMapper) = ApplicationRunner {
        if (repo.count() == 0L) {
            ClassPathResource("seed/productos.json").inputStream.use {
                repo.saveAll(json.readValue(it, object : TypeReference<List<Producto>>() {}))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CatalogoApplication>(*args)
}
