package com.corpaliv.albalab.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corpaliv.albalab.navigation.Pantalla
import com.corpaliv.albalab.ui.components.BotonAccion
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TarjetaInfo

@Composable
fun InicioScreen(onNavegar: (Pantalla) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        EncabezadoPantalla("Bienvenido a Alba Lab")
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaInfo(
            titulo = "CORPALIV",
            descripcion = "Corporación de Padres y Amigos por el Limitado Visual. " +
                    "Impulsamos la inclusión social y laboral a través de la Red Sociolaboral Alba Lab."
        )
        Spacer(modifier = Modifier.height(20.dp))
        BotonAccion("Conoce nuestros programas") { onNavegar(Pantalla.Programas) }
        Spacer(modifier = Modifier.height(12.dp))
        BotonAccion("Ver catálogo de productos") { onNavegar(Pantalla.Catalogo) }
        Spacer(modifier = Modifier.height(12.dp))
        BotonAccion("Nuestro impacto") { onNavegar(Pantalla.Impacto) }
        Spacer(modifier = Modifier.height(12.dp))
        BotonAccion("Quiero colaborar") { onNavegar(Pantalla.Colaborar) }
    }
}
