package com.corpaliv.albalab.viewmodel

import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.CatalogoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

// El sondeo es un bucle infinito: se avanza el reloj virtual con advanceTimeBy + runCurrent.
// No usar advanceUntilIdle(), porque nunca termina.
@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun siguienteSondeo() {
        dispatcher.scheduler.advanceTimeBy(INTERVALO_SONDEO_MS)
        dispatcher.scheduler.runCurrent()
    }

    val velas = Producto("Velas artesanales", "Hechas a mano", 4500, id = 1)

    "empieza en Cargando y luego muestra los productos del backend" {
        val repo = mockk<CatalogoRepository>()
        coEvery { repo.obtenerProductos() } returns listOf(velas)

        val vm = CatalogoViewModel(repo)
        vm.productos.value shouldBe UiState.Cargando

        dispatcher.scheduler.runCurrent()
        vm.productos.value shouldBe UiState.Exito(listOf(velas))
    }

    "refleja un cambio del backend en el siguiente sondeo (tiempo real)" {
        val repo = mockk<CatalogoRepository>()
        coEvery { repo.obtenerProductos() } returnsMany listOf(listOf(velas), listOf(velas.copy(precio = 9990)))

        val vm = CatalogoViewModel(repo)
        dispatcher.scheduler.runCurrent()
        vm.productos.value shouldBe UiState.Exito(listOf(velas))

        siguienteSondeo()
        vm.productos.value shouldBe UiState.Exito(listOf(velas.copy(precio = 9990)))
        coVerify(exactly = 2) { repo.obtenerProductos() }
    }

    "sin conexión muestra un error y se recupera solo cuando vuelve el backend" {
        val repo = mockk<CatalogoRepository>()
        var llamadas = 0
        coEvery { repo.obtenerProductos() } answers {
            if (llamadas++ == 0) throw IOException("sin red") else listOf(velas)
        }

        val vm = CatalogoViewModel(repo)
        dispatcher.scheduler.runCurrent()
        vm.productos.value.shouldBeInstanceOf<UiState.Error>().mensaje shouldContain "Sin conexión"

        siguienteSondeo()
        vm.productos.value shouldBe UiState.Exito(listOf(velas))
    }

    "un error HTTP del servidor muestra el código" {
        val repo = mockk<CatalogoRepository>()
        coEvery { repo.obtenerProductos() } throws errorHttp(500)

        val vm = CatalogoViewModel(repo)
        dispatcher.scheduler.runCurrent()
        vm.productos.value.shouldBeInstanceOf<UiState.Error>().mensaje shouldContain "500"
    }
})
