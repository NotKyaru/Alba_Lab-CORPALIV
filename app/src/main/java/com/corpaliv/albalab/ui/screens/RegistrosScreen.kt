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
import com.corpaliv.albalab.data.RegistroInteres
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TarjetaInfo
import com.corpaliv.albalab.ui.components.TextoVolver

@Composable
fun RegistrosScreen(
    registros: List<RegistroInteres>,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextoVolver(onVolver)
        Spacer(modifier = Modifier.height(8.dp))
        EncabezadoPantalla("Registros de interés")
        Spacer(modifier = Modifier.height(12.dp))
        if (registros.isEmpty()) {
            Text(
                text = "Aún no hay registros de interés.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            registros.forEach { registro ->
                TarjetaInfo(
                    titulo = registro.nombre,
                    descripcion = "${registro.modalidad}\n${registro.correo}\n${registro.mensaje}"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
