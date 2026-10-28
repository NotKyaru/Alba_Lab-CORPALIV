package com.corpaliv.albalab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// encabezado de pantalla reutilizable
@Composable
fun EncabezadoPantalla(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

// tarjeta de información usando Box + clip + background
@Composable
fun TarjetaInfo(
    titulo: String,
    descripcion: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// menu desplegable de opciones, similar al componente NuevoMenu del ejemplo
@Composable
fun MenuOpciones(
    isExpanded: Boolean,
    onItemClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val opciones = listOf("Inicio", "Contacto", "Registros")
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss) {
        opciones.forEach { opcion ->
            DropdownMenuItem(
                text = { Text(text = opcion) },
                onClick = { onItemClick(opcion) }
            )
        }
    }
}

// boton de acción construido con Box + clip + background + clickable
@Composable
fun BotonAccion(
    texto: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

// texto "volver" clickeable, para regresar a la pantalla anterior
@Composable
fun TextoVolver(onVolver: () -> Unit) {
    Text(
        text = "← Volver",
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onVolver() }
            .padding(8.dp)
    )
}