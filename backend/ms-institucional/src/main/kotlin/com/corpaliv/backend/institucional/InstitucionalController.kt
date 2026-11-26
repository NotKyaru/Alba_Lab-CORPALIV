package com.corpaliv.backend.institucional

import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class InstitucionalController(
    private val programas: ProgramaRepository,
    private val casos: CasoExitoRepository
) {

    @GetMapping("/programas")
    fun listarProgramas(): List<Programa> = programas.findAll(Sort.by("id"))

    @PutMapping("/programas/{id}")
    fun actualizarPrograma(@PathVariable id: Int, @RequestBody entrada: ProgramaEntrada): ResponseEntity<Any> {
        val programa = entrada.aPrograma(id) ?: return respuestaError(HttpStatus.BAD_REQUEST, "nombre y descripcion son obligatorios")
        if (!programas.existsById(id)) return respuestaError(HttpStatus.NOT_FOUND, "Programa no encontrado")
        return ResponseEntity.ok(programas.save(programa))
    }

    @GetMapping("/casos-exito")
    fun listarCasos(): List<CasoExito> = casos.findAll(Sort.by("id"))

    @PutMapping("/casos-exito/{id}")
    fun actualizarCaso(@PathVariable id: Int, @RequestBody entrada: CasoExitoEntrada): ResponseEntity<Any> {
        val caso = entrada.aCasoExito(id)
            ?: return respuestaError(HttpStatus.BAD_REQUEST, "titulo, descripcion e indicador son obligatorios")
        if (!casos.existsById(id)) return respuestaError(HttpStatus.NOT_FOUND, "Caso de éxito no encontrado")
        return ResponseEntity.ok(casos.save(caso))
    }

    private fun respuestaError(estado: HttpStatus, mensaje: String): ResponseEntity<Any> =
        ResponseEntity.status(estado).body(mapOf("error" to mensaje))
}
