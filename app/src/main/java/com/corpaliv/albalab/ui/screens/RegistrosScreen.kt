package com.corpaliv.albalab.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.ui.components.ContenidoSegunEstado
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TarjetaInfo
import com.corpaliv.albalab.ui.components.TextoVolver

@Composable
fun RegistrosScreen(
    registros: UiState<List<RegistroInteres>>,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextoVolver(onVolver)
        EncabezadoPantalla("Registros de interés")
        Spacer(modifier = Modifier.height(8.dp))

        ContenidoSegunEstado(registros) { lista ->
            Column {
                Text(
                    text = "Total de registros: ${lista.size}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (lista.isEmpty()) {
                    Text(
                        text = "Aún no hay registros de interés.\n" +
                                "Completa el formulario de contacto para ver las solicitudes aquí.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    lista.forEach { registro ->
                        TarjetaInfo(
                            titulo = registro.nombre,
                            descripcion = "${registro.modalidad} · ${registro.estado}\n" +
                                    "${registro.correo}\n${registro.mensaje}"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
