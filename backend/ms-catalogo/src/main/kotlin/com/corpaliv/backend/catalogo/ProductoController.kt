package com.corpaliv.backend.catalogo

import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/productos")
class ProductoController(private val repo: ProductoRepository) {

    @GetMapping
    fun listar(): List<Producto> = repo.findAll(Sort.by("id"))

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Int): ResponseEntity<Any> =
        repo.findById(id).map<ResponseEntity<Any>> { ResponseEntity.ok(it) }.orElse(noEncontrado())

    @PostMapping
    fun crear(@RequestBody entrada: ProductoEntrada): ResponseEntity<Any> {
        val producto = entrada.aProducto() ?: return datosInvalidos()
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(producto))
    }

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Int, @RequestBody entrada: ProductoEntrada): ResponseEntity<Any> {
        val producto = entrada.aProducto(id) ?: return datosInvalidos()
        if (!repo.existsById(id)) return noEncontrado()
        return ResponseEntity.ok(repo.save(producto))
    }

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Int): ResponseEntity<Any> {
        if (!repo.existsById(id)) return noEncontrado()
        repo.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    private fun noEncontrado(): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Producto no encontrado"))

    private fun datosInvalidos(): ResponseEntity<Any> = ResponseEntity.badRequest()
        .body(mapOf("error" to "nombre es obligatorio y precio debe ser un número mayor o igual a 0"))
}
