package com.uvg.mashoras.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.Timestamp
import com.uvg.mashoras.ui.register.CareerPickerField
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        titulo: String,
        descripcion: String,
        fecha: Timestamp,
        cupos: Int,
        carrera: String,
        horasARealizar: Int
    ) -> Unit,
    isLoading: Boolean = false
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaText by remember { mutableStateOf("") }
    var cuposText by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf<String?>(null) }
    var horasText by remember { mutableStateOf("") }
    
    var showErrors by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nueva Actividad",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario scrolleable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = {
                            titulo = it
                            errorMessage = null
                        },
                        label = { Text("Título de la actividad") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showErrors && titulo.isBlank(),
                        supportingText = {
                            if (showErrors && titulo.isBlank()) {
                                Text("El título es requerido")
                            }
                        },
                        singleLine = true
                    )

                    // Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            errorMessage = null
                        },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showErrors && descripcion.isBlank(),
                        supportingText = {
                            if (showErrors && descripcion.isBlank()) {
                                Text("La descripción es requerida")
                            }
                        },
                        minLines = 3,
                        maxLines = 5
                    )

                    // Fecha (formato: dd/MM/yyyy)
                    OutlinedTextField(
                        value = fechaText,
                        onValueChange = {
                            fechaText = it
                            errorMessage = null
                        },
                        label = { Text("Fecha (dd/MM/yyyy)") },
                        placeholder = { Text("Ej: 25/12/2025") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showErrors && (fechaText.isBlank() || !isValidDate(fechaText)),
                        supportingText = {
                            if (showErrors && fechaText.isBlank()) {
                                Text("La fecha es requerida")
                            } else if (showErrors && !isValidDate(fechaText)) {
                                Text("Formato inválido. Use dd/MM/yyyy")
                            }
                        },
                        singleLine = true
                    )

                    // Cupos
                    OutlinedTextField(
                        value = cuposText,
                        onValueChange = {
                            cuposText = it.filter { char -> char.isDigit() }
                            errorMessage = null
                        },
                        label = { Text("Cupos disponibles") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showErrors && (cuposText.isBlank() || cuposText.toIntOrNull() == null || cuposText.toInt() <= 0),
                        supportingText = {
                            if (showErrors && (cuposText.isBlank() || cuposText.toIntOrNull() == null || cuposText.toInt() <= 0)) {
                                Text("Ingrese un número válido mayor a 0")
                            }
                        },
                        singleLine = true
                    )

                    // Carrera
                    CareerPickerField(
                        value = carrera,
                        onValueChange = {
                            carrera = it
                            errorMessage = null
                        },
                        label = "Carrera (o 'Todas')",
                        isError = showErrors && carrera.isNullOrBlank()
                    )

                    // Horas a realizar
                    OutlinedTextField(
                        value = horasText,
                        onValueChange = {
                            horasText = it.filter { char -> char.isDigit() }
                            errorMessage = null
                        },
                        label = { Text("Horas a realizar") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showErrors && (horasText.isBlank() || horasText.toIntOrNull() == null || horasText.toInt() <= 0),
                        supportingText = {
                            if (showErrors && (horasText.isBlank() || horasText.toIntOrNull() == null || horasText.toInt() <= 0)) {
                                Text("Ingrese un número válido mayor a 0")
                            }
                        },
                        singleLine = true
                    )

                    // Mensaje de error
                    if (errorMessage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            showErrors = true
                            
                            val cupos = cuposText.toIntOrNull()
                            val horas = horasText.toIntOrNull()
                            
                            if (titulo.isBlank() ||
                                descripcion.isBlank() ||
                                !isValidDate(fechaText) ||
                                cupos == null || cupos <= 0 ||
                                carrera.isNullOrBlank() ||
                                horas == null || horas <= 0
                            ) {
                                errorMessage = "Por favor, complete todos los campos correctamente"
                                return@Button
                            }

                            try {
                                val fecha = parseDate(fechaText)
                                val timestamp = Timestamp(fecha)
                                onConfirm(titulo, descripcion, timestamp, cupos, carrera!!, horas)
                            } catch (e: Exception) {
                                errorMessage = "Error al procesar la fecha: ${e.message}"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Crear Actividad")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Valida si una fecha tiene el formato dd/MM/yyyy
 */
private fun isValidDate(dateString: String): Boolean {
    if (dateString.isBlank()) return false
    
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.isLenient = false
        format.parse(dateString)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Parsea una fecha en formato dd/MM/yyyy a Date
 */
private fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.isLenient = false
    return format.parse(dateString) ?: throw IllegalArgumentException("Fecha inválida")
}