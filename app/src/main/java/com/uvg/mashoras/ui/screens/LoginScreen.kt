package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uvg.mashoras.MasHorasApp
import com.uvg.mashoras.navigation.AppScreens
import com.uvg.mashoras.presentation.login.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as MasHorasApp
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(app.container.authRepository))
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            navController.navigate(AppScreens.AvailableActivitiesScreen.route) {
                popUpTo(AppScreens.WelcomeScreen.route) { inclusive = true }
            }
        } else if (state is LoginState.Error) {
            val raw = (state as LoginState.Error).message
            val translated = translateErrorToSpanish(raw)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(translated)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.9f)) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp))
                Spacer(Modifier.width(12.dp))
                Text("Inicio de sesión", fontSize = 24.sp, fontWeight = FontWeight.W900, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = email, onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = { Text("Correo", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(0.9f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                isError = emailError != null
            )
            if (emailError != null) {
                Spacer(Modifier.height(6.dp))
                Text(emailError ?: "", color = Color.Red, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(0.9f))
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password, onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true,
                isError = passwordError != null
            )
            if (passwordError != null) {
                Spacer(Modifier.height(6.dp))
                Text(passwordError ?: "", color = Color.Red, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(0.9f))
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    var hasError = false
                    if (email.isBlank()) {
                        emailError = "Ingrese su correo electrónico"
                        hasError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Correo electrónico inválido"
                        hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = "Ingrese su contraseña"
                        hasError = true
                    } else if (password.length < 6) {
                        passwordError = "La contraseña debe tener al menos 6 caracteres"
                        hasError = true
                    }

                    if (!hasError) {
                        viewModel.login(email.trim(), password)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Corrige los errores antes de continuar")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                shape = RoundedCornerShape(24.dp),
                enabled = state != LoginState.Loading
            ) {
                if (state == LoginState.Loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                else Text("Iniciar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Regresar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))
            if (state is LoginState.Error) {
                val translated = translateErrorToSpanish((state as LoginState.Error).message)
                Text(translated, color = Color.Red, fontSize = 14.sp)
            }
        }
    }
}

private fun translateErrorToSpanish(raw: String): String {
    if (raw.isBlank()) return "Error al iniciar sesión"

    val afterDash = raw.substringAfterLast(" - ", raw)
    val afterColon = afterDash.substringAfterLast(":", afterDash)
    val cleaned = afterColon.trim()
    val lower = cleaned.lowercase()

    return when {
        // "The supplied auth credential is incorrect, malformed or has expired."
        "supplied auth credential" in lower
                || "auth credential is incorrect" in lower
                || ("credential" in lower && ("incorrect" in lower || "malformed" in lower || "expired" in lower || "has expired" in lower)) ->
            "Contraseña incorrecta."

        // Firebase / Auth common messages
        "badly formatted" in lower || "bad format" in lower -> "Correo electrónico malformado"
        "invalid" in lower && "credential" in lower -> "Correo o contraseña incorrectos"
        "wrong-password" in lower || "wrong password" in lower || ("contraseña" in lower && ("incorrect" in lower || "inválida" in lower)) -> "Contraseña incorrecta"
        ("user" in lower && ("not found" in lower || "no existe" in lower)) || "user-disabled" in lower -> "Usuario no encontrado o inhabilitado"
        "network" in lower || "unable to resolve" in lower || "failed to connect" in lower -> "Error de red. Verifique su conexión e intente nuevamente"
        "timeout" in lower -> "Tiempo de espera agotado. Intente de nuevo"
        "unauthorized" in lower || "403" in lower -> "Acceso no autorizado"
        "401" in lower -> "No autorizado. Verifique sus credenciales"
        "invalid email" in lower || "email address is badly formatted" in lower || "auth/invalid-email" in lower -> "Correo electrónico inválido"
        "password" in lower && "required" in lower -> "Se requiere contraseña"
        "auth/wrong-password" in lower || "wrong-password" in lower -> "Contraseña incorrecta"
        "auth/user-not-found" in lower || "user-not-found" in lower -> "Usuario no encontrado"
        else -> {
            if (cleaned.isNotBlank()) "Error al iniciar sesión: $cleaned" else "Error al iniciar sesión"
        }
    }
}