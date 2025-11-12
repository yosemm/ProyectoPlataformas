package com.uvg.mashoras.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

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
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = true },
            value = value.orEmpty(),
            onValueChange = { /* readOnly */ },
            readOnly = true,
            label = { Text(label) },
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            supportingText = { if (supportingText != null) Text(supportingText) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LazyColumn {
                items(Careers.allowed) { career ->
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
}
