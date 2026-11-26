package com.corpaliv.albalab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.corpaliv.albalab.data.model.RegistroInteres
import com.corpaliv.albalab.data.listaModalidades
import com.corpaliv.albalab.ui.components.BotonAccion
import com.corpaliv.albalab.ui.components.EncabezadoPantalla
import com.corpaliv.albalab.ui.components.TextoVolver

@Composable
fun ContactoScreen(
    onVolver: () -> Unit,
    // El segundo parámetro se llama si el backend no pudo guardar el registro.
    onEnviar: (registro: RegistroInteres, onError: (String) -> Unit) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) } // evita enviar el mismo registro dos veces


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextoVolver(onVolver)
        Spacer(modifier = Modifier.height(8.dp))
        EncabezadoPantalla("Solicitar contacto")
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Modalidad de colaboracion",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        listaModalidades.forEach { opcion ->
            val seleccionada = modalidad == opcion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (seleccionada) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .clickable { modalidad = opcion }
                    .padding(16.dp)
            ) {
                Text(
                    text = opcion,
                    color = if (seleccionada) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Mensaje") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (errorMensaje.isNotEmpty()) {
            Text(
                text = errorMensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        BotonAccion(if (enviando) "Enviando…" else "Enviar solicitud") {
            if (enviando) return@BotonAccion
            errorMensaje = ""
            validarFormulario(nombre, correo, modalidad, mensaje)
                .onSuccess { registro ->
                    enviando = true
                    onEnviar(registro) { error ->
                        enviando = false
                        errorMensaje = "Error: $error"
                    }
                }
                .onFailure { error -> errorMensaje = "Error: ${error.message}" }
        }

    }
}

private fun validarFormulario(
    nombre: String,
    correo: String,
    modalidad: String,
    mensaje: String
): Result<RegistroInteres> {
    // Mismas reglas que valida ms-registros, para avisar antes de enviar.
    if (nombre.isBlank()) {
        return Result.failure(IllegalArgumentException("El nombre es obligatorio."))
    }
    if (correo.isBlank()) {
        return Result.failure(IllegalArgumentException("El correo es obligatorio."))
    }
    if (!correo.contains("@")) {
        return Result.failure(IllegalArgumentException("El correo debe contener @."))
    }
    if (modalidad.isEmpty()) {
        return Result.failure(IllegalArgumentException("Selecciona una modalidad de colaboración."))
    }
    if (mensaje.isBlank()) {
        return Result.failure(IllegalArgumentException("El mensaje es obligatorio."))
    }
    return Result.success(RegistroInteres(nombre, correo, modalidad, mensaje))
}
