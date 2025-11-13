package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uvg.mashoras.ui.register.CareerPickerField

@Composable
fun RegisterScreen(
    onRegister: suspend (email: String, password: String, career: String) -> Result<Unit>,
    onSuccessNavigate: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var career by remember { mutableStateOf<String?>(null) }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val canSubmit = email.isNotBlank() &&
            password.length >= 6 &&
            password == confirm &&
            !career.isNullOrBlank()

    LaunchedEffect(loading) {
        if (loading) {
            val result = onRegister(email, password, career!!)
            loading = false
            result.fold(
                onSuccess = {
                    onSuccessNavigate()   // aquí ya navega al AvailableActivitiesScreen
                },
                onFailure = {
                    error = it.message ?: "Error desconocido"
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Correo institucional") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = confirm.isNotEmpty() && confirm != password
        )

        CareerPickerField(
            value = career,
            onValueChange = { career = it },
            supportingText = "Pulsa para elegir. Lista desplazable.",
            isError = career.isNullOrBlank()
        )

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                error = null
                loading = true
            },
            enabled = canSubmit && !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Procesando" else "Registrarme")
        }
    }
}
