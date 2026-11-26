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
import com.corpaliv.albalab.data.model.Programa
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.ui.components.ContenidoSegunEstado
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TarjetaInfo

@Composable
fun ProgramasScreen(programas: UiState<List<Programa>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        EncabezadoPantalla("Programas")
        Spacer(modifier = Modifier.height(12.dp))
        ContenidoSegunEstado(programas) { lista ->
            lista.forEach { programa ->
                TarjetaInfo(
                    titulo = programa.nombre,
                    descripcion = programa.descripcion
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
