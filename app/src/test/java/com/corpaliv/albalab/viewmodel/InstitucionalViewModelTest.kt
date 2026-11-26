package com.corpaliv.albalab.viewmodel

import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.repository.InstitucionalRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class InstitucionalViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    val programa = Programa("Formación para el empleo", "Capacitación laboral", id = 1)
    val caso = CasoExito("Integración laboral", "Texto", "120 personas formadas", id = 1)

    "carga programas y casos de éxito de forma independiente" {
        val repo = mockk<InstitucionalRepository>()
        coEvery { repo.obtenerProgramas() } returns listOf(programa)
        coEvery { repo.obtenerCasosExito() } returns listOf(caso)

        val vm = InstitucionalViewModel(repo)
        dispatcher.scheduler.runCurrent()

        vm.programas.value shouldBe UiState.Exito(listOf(programa))
        vm.casosExito.value shouldBe UiState.Exito(listOf(caso))
    }

    "si falla un endpoint, el otro igual se muestra" {
        val repo = mockk<InstitucionalRepository>()
        coEvery { repo.obtenerProgramas() } throws IOException("sin red")
        coEvery { repo.obtenerCasosExito() } returns listOf(caso)

        val vm = InstitucionalViewModel(repo)
        dispatcher.scheduler.runCurrent()

        vm.programas.value.shouldBeInstanceOf<UiState.Error>()
        vm.casosExito.value shouldBe UiState.Exito(listOf(caso))
    }
})
