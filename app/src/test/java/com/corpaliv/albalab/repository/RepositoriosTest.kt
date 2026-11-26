package com.corpaliv.albalab.repository

import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.remote.CatalogoApi
import com.corpaliv.albalab.data.remote.InstitucionalApi
import com.corpaliv.albalab.data.remote.RegistrosApi
import com.corpaliv.albalab.viewmodel.errorHttp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import retrofit2.HttpException

class RepositoriosTest : StringSpec({

    "CatalogoRepository devuelve los productos de la api" {
        val api = mockk<CatalogoApi>()
        val productos = listOf(Producto("Velas", "Hechas a mano", 4500, id = 1))
        coEvery { api.obtenerProductos() } returns productos

        CatalogoRepository(api).obtenerProductos() shouldBe productos
        coVerify(exactly = 1) { api.obtenerProductos() }
    }

    "InstitucionalRepository consulta programas y casos de éxito por separado" {
        val api = mockk<InstitucionalApi>()
        val programas = listOf(Programa("Formación", "Capacitación", id = 1))
        val casos = listOf(CasoExito("Integración", "Texto", "120 personas", id = 1))
        coEvery { api.obtenerProgramas() } returns programas
        coEvery { api.obtenerCasosExito() } returns casos

        val repo = InstitucionalRepository(api)
        repo.obtenerProgramas() shouldBe programas
        repo.obtenerCasosExito() shouldBe casos
    }

    "RegistrosRepository envía el registro y devuelve el que creó el backend" {
        val api = mockk<RegistrosApi>()
        val nuevo = RegistroInteres("Ana", "ana@example.com", "Donación", "Hola")
        coEvery { api.enviarRegistro(nuevo) } returns nuevo.copy(id = 7)

        RegistrosRepository(api).enviarRegistro(nuevo).id shouldBe 7
        coVerify(exactly = 1) { api.enviarRegistro(nuevo) }
    }

    "RegistrosRepository deja pasar el rechazo del backend para que el ViewModel lo muestre" {
        val api = mockk<RegistrosApi>()
        coEvery { api.enviarRegistro(any()) } throws errorHttp(400)

        shouldThrow<HttpException> {
            RegistrosRepository(api).enviarRegistro(RegistroInteres("Ana", "sin-arroba", "Donación", "Hola"))
        }.code() shouldBe 400
    }
})
