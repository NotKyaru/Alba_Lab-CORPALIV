package com.corpaliv.albalab.viewmodel

import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.RegistrosRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrosViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    val existente = RegistroInteres("Empresa Ficticia", "e@example.com", "Voluntariado", "Hola", id = 1)
    val nuevo = RegistroInteres("Ana Prueba", "ana@example.com", "Donación", "Quiero donar")
    val creado = nuevo.copy(id = 2)

    // Envía [nuevo] y devuelve el mensaje de error, o null si se llamó a onExito.
    fun enviarYEsperar(vm: RegistrosViewModel): String? {
        var exito = false
        var error: String? = null
        vm.enviar(nuevo, onExito = { exito = true }, onError = { error = it })
        dispatcher.scheduler.runCurrent()
        return if (exito) null else error ?: "no se llamó a ningún callback"
    }

    "enviar guarda el registro y lo muestra en la lista sin esperar al sondeo" {
        val repo = mockk<RegistrosRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(existente)
        coEvery { repo.enviarRegistro(nuevo) } returns creado

        val vm = RegistrosViewModel(repo)
        dispatcher.scheduler.runCurrent()

        enviarYEsperar(vm).shouldBeNull()
        vm.registros.value shouldBe UiState.Exito(listOf(existente, creado))
        coVerify(exactly = 1) { repo.obtenerRegistros() } // no hizo una segunda petición
    }

    "sin conexión avisa con onError y no agrega nada" {
        val repo = mockk<RegistrosRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(existente)
        coEvery { repo.enviarRegistro(nuevo) } throws IOException("sin red")

        val vm = RegistrosViewModel(repo)
        dispatcher.scheduler.runCurrent()

        enviarYEsperar(vm)!! shouldContain "Sin conexión"
        vm.registros.value shouldBe UiState.Exito(listOf(existente))
    }

    "si el servidor rechaza el registro (400) avisa con el código" {
        val repo = mockk<RegistrosRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(existente)
        coEvery { repo.enviarRegistro(nuevo) } throws errorHttp(400)

        val vm = RegistrosViewModel(repo)
        dispatcher.scheduler.runCurrent()

        enviarYEsperar(vm)!! shouldContain "400"
    }
})
