package com.corpaliv.backend.registros

import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/registros")
class RegistroController(private val repo: RegistroRepository) {

    @GetMapping
    fun listar(): List<RegistroInteres> = repo.findAll(Sort.by("id"))

    @PostMapping
    fun crear(@RequestBody nuevo: RegistroNuevo): ResponseEntity<Any> {
        nuevo.validar()?.let { motivo -> return respuestaError(HttpStatus.BAD_REQUEST, motivo) }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nuevo.aRegistro()))
    }

    @PatchMapping("/{id}/estado")
    fun cambiarEstado(@PathVariable id: Int, @RequestBody cambio: CambioEstado): ResponseEntity<Any> {
        val estado = cambio.estado
        if (estado !in ESTADOS) {
            return respuestaError(HttpStatus.BAD_REQUEST, "El estado debe ser uno de: ${ESTADOS.joinToString()}.")
        }
        val registro = repo.findById(id).orElse(null)
            ?: return respuestaError(HttpStatus.NOT_FOUND, "Registro no encontrado")
        registro.estado = estado!!
        return ResponseEntity.ok(repo.save(registro))
    }

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Int): ResponseEntity<Any> {
        if (!repo.existsById(id)) return respuestaError(HttpStatus.NOT_FOUND, "Registro no encontrado")
        repo.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    private fun respuestaError(estado: HttpStatus, mensaje: String): ResponseEntity<Any> =
        ResponseEntity.status(estado).body(mapOf("error" to mensaje))
}
