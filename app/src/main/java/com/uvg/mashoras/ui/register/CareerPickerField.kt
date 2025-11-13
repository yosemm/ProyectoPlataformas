package com.uvg.mashoras.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerPickerField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String = "Carrera",
    supportingText: String? = null,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }   // toggle
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.9f)
                // opcional: puedes dejarlo o quitarlo
                .clickable { expanded = true },
            value = value.orEmpty(),
            onValueChange = { /* readOnly */ },
            readOnly = true,
            label = { Text(label) },
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(8.dp),
            supportingText = {
                if (supportingText != null) {
                    Text(supportingText)
                }
            }
        )

        // IMPORTANTE: sin LazyColumn aquí
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Careers.allowed.forEach { career ->
                DropdownMenuItem(
                    text = { Text(career) },
                    onClick = {
                        onValueChange(career)
                        expanded = false
                    }
                )
            }
        }
    }
}
