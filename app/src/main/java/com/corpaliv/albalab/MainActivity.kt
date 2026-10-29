package com.corpaliv.albalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.corpaliv.albalab.data.RegistroInteres
import com.corpaliv.albalab.navigation.Pantalla
import com.corpaliv.albalab.ui.components.BarraNavegacion
import com.corpaliv.albalab.ui.components.MenuOpciones
import com.corpaliv.albalab.ui.screens.CatalogoScreen
import com.corpaliv.albalab.ui.screens.ColaborarScreen
import com.corpaliv.albalab.ui.screens.ContactoScreen
import com.corpaliv.albalab.ui.screens.DetalleProductoScreen
import com.corpaliv.albalab.ui.screens.ImpactoScreen
import com.corpaliv.albalab.ui.screens.InicioScreen
import com.corpaliv.albalab.ui.screens.ProgramasScreen
import com.corpaliv.albalab.ui.screens.RegistrosScreen
import com.corpaliv.albalab.ui.theme.CorpalivTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CorpalivTheme {
                CorpalivApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorpalivApp() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Inicio) }
    val registros = remember { mutableStateListOf<RegistroInteres>() }
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Text(text = pantallaActual.titulo)
                },
                actions = {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Opciones de menú"
                        )
                        MenuOpciones(
                            isExpanded = menuAbierto,
                            onItemClick = { opcion ->
                                pantallaActual = when (opcion) {
                                    "Contacto" -> Pantalla.Contacto
                                    "Registros" -> Pantalla.Registros
                                    else -> Pantalla.Inicio
                                }
                                menuAbierto = false
                            },
                            onDismiss = { menuAbierto = false }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BarraNavegacion(
                pantallaActual = pantallaActual,
                onNavegar = { pantallaActual = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val pantalla = pantallaActual
            when (pantalla) {
                Pantalla.Inicio -> InicioScreen(onNavegar = { pantallaActual = it })

                Pantalla.Programas -> ProgramasScreen()

                Pantalla.Catalogo -> CatalogoScreen(onVerProducto = { producto ->
                    pantallaActual = Pantalla.DetalleProducto(producto)
                })

                Pantalla.Impacto -> ImpactoScreen()

                Pantalla.Colaborar -> ColaborarScreen(
                    onContacto = { pantallaActual = Pantalla.Contacto }
                )

                Pantalla.Contacto -> ContactoScreen(
                    onVolver = { pantallaActual = Pantalla.Colaborar },
                    onEnviar = { registro ->
                        registros.add(registro)
                        pantallaActual = Pantalla.Registros
                    }
                )

                Pantalla.Registros -> RegistrosScreen(
                    registros = registros,
                    onVolver = { pantallaActual = Pantalla.Inicio }
                )

                is Pantalla.DetalleProducto -> DetalleProductoScreen(
                    producto = pantalla.producto,
                    onVolver = { pantallaActual = Pantalla.Catalogo }
                )
            }
        }
    }
}
