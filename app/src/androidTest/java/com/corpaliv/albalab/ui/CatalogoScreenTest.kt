package com.corpaliv.albalab.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.corpaliv.albalab.data.model.Producto
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.ui.screens.CatalogoScreen
import com.corpaliv.albalab.ui.theme.CorpalivTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogoScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val velas = Producto("Velas artesanales", "Hechas a mano", 4500, id = 1)
    private val jabones = Producto("Jabones naturales", "Ingredientes naturales", 3200, id = 2)

    @Test
    fun muestraLosProductosConSuPrecio() {
        compose.setContent {
            CorpalivTheme { CatalogoScreen(UiState.Exito(listOf(velas, jabones)), onVerProducto = {}) }
        }

        compose.onNodeWithText("Velas artesanales").assertIsDisplayed()
        compose.onNodeWithText("Precio: \$4500").assertIsDisplayed()
        compose.onNodeWithText("Jabones naturales").assertIsDisplayed()
    }

    @Test
    fun muestraElErrorCuandoNoHayConexion() {
        compose.setContent {
            CorpalivTheme { CatalogoScreen(UiState.Error("Sin conexión con el servidor. Reintentando…"), onVerProducto = {}) }
        }

        compose.onNodeWithText("Sin conexión con el servidor. Reintentando…").assertIsDisplayed()
    }

    @Test
    fun alTocarUnProductoAbreSuDetalle() {
        var elegido: Producto? = null
        compose.setContent {
            CorpalivTheme { CatalogoScreen(UiState.Exito(listOf(velas, jabones)), onVerProducto = { elegido = it }) }
        }

        compose.onNodeWithText("Jabones naturales").performScrollTo().performClick()
        assertEquals(jabones, elegido)
    }
}
