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
import com.corpaliv.albalab.data.model.CasoExito
import com.corpaliv.albalab.data.model.UiState
import com.corpaliv.albalab.ui.components.ContenidoSegunEstado
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TarjetaInfo

@Composable
fun ImpactoScreen(casosExito: UiState<List<CasoExito>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        EncabezadoPantalla("Impacto y casos de éxito")
        Spacer(modifier = Modifier.height(12.dp))
        ContenidoSegunEstado(casosExito) { lista ->
            lista.forEach { caso ->
                TarjetaInfo(
                    titulo = caso.titulo,
                    descripcion = "${caso.descripcion}\nIndicador: ${caso.indicador}"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
