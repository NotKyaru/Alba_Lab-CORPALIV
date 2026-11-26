package com.corpaliv.albalab.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.ui.screens.ContactoScreen
import com.corpaliv.albalab.ui.theme.CorpalivTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactoScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private var enviado: RegistroInteres? = null
    private var avisarError: ((String) -> Unit)? = null

    private fun mostrarFormulario() {
        compose.setContent {
            CorpalivTheme {
                ContactoScreen(onVolver = {}, onEnviar = { registro, onError ->
                    enviado = registro
                    avisarError = onError
                })
            }
        }
    }

    private fun completar(correo: String) {
        compose.onNodeWithText("Nombre").performTextInput("Ana Prueba")
        compose.onNodeWithText("Correo").performTextInput(correo)
        compose.onNodeWithText("Donación").performScrollTo().performClick()
        compose.onNodeWithText("Mensaje").performScrollTo().performTextInput("Quiero donar")
        compose.onNodeWithText("Enviar solicitud").performScrollTo().performClick()
    }

    @Test
    fun rechazaUnCorreoSinArrobaSinEnviarAlBackend() {
        mostrarFormulario()
        completar("ana.example.com")

        compose.onNodeWithText("Error: El correo debe contener @.").assertIsDisplayed()
        assertNull(enviado)
    }

    @Test
    fun conDatosValidosEnviaElRegistro() {
        mostrarFormulario()
        completar("ana@example.com")

        assertEquals(RegistroInteres("Ana Prueba", "ana@example.com", "Donación", "Quiero donar"), enviado)
        compose.onNodeWithText("Enviando…").assertIsDisplayed() // botón bloqueado contra doble envío
    }

    @Test
    fun muestraElErrorQueDevuelveElBackend() {
        mostrarFormulario()
        completar("ana@example.com")

        compose.runOnIdle { avisarError!!("El servidor rechazó la solicitud (código 400).") }

        compose.onNodeWithText("Error: El servidor rechazó la solicitud (código 400).").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Enviar solicitud").assertExists() // el botón vuelve a estar disponible
    }
}
