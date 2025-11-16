package com.uvg.mashoras.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit,
    label: String = "Fecha",
    isError: Boolean = false,
    supportingText: String? = null,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = selectedDate?.let { dateFormatter.format(it) } ?: ""

    OutlinedTextField(
        value = dateText,
        onValueChange = { /* Read-only */ },
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        readOnly = true,
        isError = isError,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha"
                )
            }
        },
        supportingText = {
            if (supportingText != null) {
                Text(supportingText)
            } else if (isError && selectedDate == null) {
                Text("La fecha es requerida")
            }
        },
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.time ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}