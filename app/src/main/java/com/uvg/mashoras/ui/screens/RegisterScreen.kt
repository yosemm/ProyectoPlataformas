package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.mashoras.ui.register.CareerPickerField
import com.uvg.mashoras.utils.EmailValidator
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun RegisterScreen(
    onRegister: suspend (email: String, password: String, nombre: String, apellido: String, career: String) -> Result<Unit>,
    onSuccessNavigate: () -> Unit,
    onBack: () -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var career by remember { mutableStateOf<String?>(null) }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Para mostrar errores visuales solo después de intentar registrar
    var showErrors by remember { mutableStateOf(false) }

    // Flags para saber si el usuario ya interactuó con cada campo
    var nombreTouched by remember { mutableStateOf(false) }
    var apellidoTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }
    var careerTouched by remember { mutableStateOf(false) }

    // Visibilidad de contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val isUvgEmail = EmailValidator.isUvgEmail(email)

    val canSubmit = nombre.isNotBlank() &&
            apellido.isNotBlank() &&
            email.isNotBlank() &&
            isUvgEmail &&
            password.length >= 6 &&
            password == confirm &&
            !career.isNullOrBlank()

    // Validaciones por campo para mostrar razones cuando el botón está deshabilitado
    val nombreValid = nombre.isNotBlank()
    val apellidoValid = apellido.isNotBlank()
    val emailValid = email.isNotBlank() && isUvgEmail
    val passwordValid = password.length >= 6

    // Scope para lanzar corrutinas desde el botón
    val scope = rememberCoroutineScope()
    
    // ScrollState para hacer la pantalla scrolleable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // Título
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Crear cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.W900,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(22.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it.trim()
                error = null
                nombreTouched = true
            },
            label = { Text("Nombre", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = ((showErrors || nombreTouched) && !nombreValid),
            supportingText = {
                if ((showErrors || nombreTouched) && !nombreValid) {
                    Text("El nombre es requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // Campo Apellido
        OutlinedTextField(
            value = apellido,
            onValueChange = {
                apellido = it.trim()
                error = null
                apellidoTouched = true
            },
            label = { Text("Apellido", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = ((showErrors || apellidoTouched) && !apellidoValid),
            supportingText = {
                if ((showErrors || apellidoTouched) && !apellidoValid) {
                    Text("El apellido es requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // Correo institucional
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it.trim()
                error = null
                emailTouched = true
            },
            label = { Text("Correo institucional", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = ((showErrors || emailTouched) && !emailValid),
            supportingText = {
                when {
                    email.isNotBlank() && isUvgEmail -> {
                        val rol = if (EmailValidator.isStudentEmail(email)) "Estudiante" else "Maestro"
                        Text(text = "Registrándose como: $rol", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                    (showErrors || emailTouched) && email.isBlank() -> Text("El correo institucional es requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    (showErrors || emailTouched) && email.isNotBlank() && !isUvgEmail -> Text("Debe ser un correo institucional @uvg.edu.gt", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    else -> Spacer(Modifier.height(0.dp))
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
                passwordTouched = true
            },
            label = { Text("Contraseña", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = ((showErrors || passwordTouched) && !passwordValid),
            supportingText = {
                if ((showErrors || passwordTouched) && password.length < 6 && password.isNotEmpty()) {
                    Text(
                        text = "Mínimo 6 caracteres",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // Confirmar contraseña
        OutlinedTextField(
            value = confirm,
            onValueChange = {
                confirm = it
                error = null
                confirmTouched = true
            },
            label = { Text("Confirmar contraseña", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            isError = ((showErrors || confirmTouched) && confirm.isNotEmpty() && confirm != password)
        )

        if ((showErrors || confirmTouched) && confirm.isNotBlank() && confirm != password) {
            Text(
                text = "Las contraseñas no coinciden",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Picker de carrera
        CareerPickerField(
            value = career,
            onValueChange = {
                career = it
                error = null
                careerTouched = true
            },
            modifier = Modifier.fillMaxWidth(0.9f),
            isError = ((showErrors || careerTouched) && career.isNullOrBlank())
        )

        if ((showErrors || careerTouched) && career.isNullOrBlank()) {
            Text(
                text = "Seleccione una carrera",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Mensaje de error (registro)
        if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            Spacer(Modifier.height(8.dp))
        }

        // Botón "Registrarme"
        Button(
            onClick = {
                error = null
                showErrors = true

                if (canSubmit && !loading) {
                    loading = true
                    scope.launch {
                        val result = onRegister(email, password, nombre, apellido, career!!)
                        loading = false

                        println("DEBUG register isSuccess=${result.isSuccess}, exception=${result.exceptionOrNull()}")

                        result.fold(
                            onSuccess = {
                                println("DEBUG register onSuccess, navegando...")
                                onSuccessNavigate()
                            },
                            onFailure = {
                                it.printStackTrace()
                                error = translateErrorToSpanish(it)
                            }
                        )
                    }
                }
            },
            enabled = canSubmit && !loading,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text("Registrarme", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón "Regresar"
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                "Regresar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

private fun translateErrorToSpanish(throwable: Throwable): String {
    val raw = throwable.localizedMessage ?: throwable.message ?: ""
    if (raw.isBlank()) return "Error al registrar"

    val afterDash = raw.substringAfterLast(" - ", raw)
    val afterColon = afterDash.substringAfterLast(":", afterDash)
    val cleaned = afterColon.trim()
    val lower = cleaned.lowercase()

    return when {
        // Mensajes posibles de Firebase / Auth
        "email already in use" in lower || "already in use" in lower || "el correo ya" in lower -> "Este correo electrónico ya está en uso."
        "weak-password" in lower || "weak password" in lower || "weakpassword" in lower || "débil" in lower -> "La contraseña es muy débil."
        "invalid email" in lower || "invalid-email" in lower || "invalid email" in cleaned.lowercase() || "address is badly formatted" in lower || "badly formatted" in lower -> "El correo electrónico es inválido."
        "missing-email" in lower || "missing email" in lower || "email is required" in lower -> "El correo electrónico es obligatorio."
        "user-not-found" in lower || "user not found" in lower || "no user" in lower -> "No se encontró ningún usuario con este correo electrónico."
        "wrong-password" in lower || "wrong password" in lower || "incorrect password" in lower -> "La contraseña es incorrecta."
        "network" in lower || "failed to connect" in lower || "unable to resolve" in lower -> "Error de red. Verifique su conexión e intente nuevamente."
        "timeout" in lower || "timed out" in lower -> "Tiempo de espera agotado. Intente de nuevo."
        "unauthorized" in lower || "403" in lower -> "Acceso no autorizado."
        "401" in lower -> "No autorizado. Verifique sus credenciales."
        "auth/invalid-email" in lower -> "El correo electrónico es inválido."
        "auth/email-already-in-use" in lower || "auth/email-already-in-use" in lower -> "Este correo electrónico ya está en uso."
        "auth/wrong-password" in lower -> "La contraseña es incorrecta."
        "auth/user-not-found" in lower -> "No se encontró ningún usuario con este correo electrónico."
        else -> {
            // Fallback
            "Error al registrar $cleaned"
        }
    }
}
