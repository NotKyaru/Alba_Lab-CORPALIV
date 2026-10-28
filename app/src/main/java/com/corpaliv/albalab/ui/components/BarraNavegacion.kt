package com.corpaliv.albalab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.corpaliv.albalab.navigation.Pantalla

// Item de la barra de navegación: pantalla + etiqueta + icono.
private data class ItemNavegacion(
    val pantalla: Pantalla,
    val etiqueta: String,
    val icono: ImageVector
)

private val itemsNavegacion = listOf(
    ItemNavegacion(Pantalla.Inicio, "Inicio", Icons.Filled.Home),
    ItemNavegacion(Pantalla.Programas, "Programas", Icons.Filled.Info),
    ItemNavegacion(Pantalla.Catalogo, "Productos", Icons.Filled.ShoppingCart),
    ItemNavegacion(Pantalla.Impacto, "Impacto", Icons.Filled.Favorite),
    ItemNavegacion(Pantalla.Colaborar, "Colaborar", Icons.Filled.Person)
)

// Barra de navegación inferior usando NavigationBar + NavigationBarItem.
@Composable
fun BarraNavegacion(
    pantallaActual: Pantalla,
    onNavegar: (Pantalla) -> Unit
) {
    NavigationBar {
        itemsNavegacion.forEach { item ->
            NavigationBarItem(
                selected = pantallaActual == item.pantalla,
                onClick = { onNavegar(item.pantalla) },
                icon = {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = item.etiqueta
                    )
                },
                label = {
                    Text(item.etiqueta)
                }
            )
        }
    }
}
