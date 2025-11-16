package com.uvg.mashoras.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.utils.EmailValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EnrolledStudentsList(
    studentIds: List<String>,
    modifier: Modifier = Modifier
) {
    var students by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    
    LaunchedEffect(studentIds) {
        if (studentIds.isEmpty()) {
            isLoading = false
            return@LaunchedEffect
        }
        
        scope.launch {
            try {
                isLoading = true
                error = null
                
                val loadedStudents = studentIds.mapNotNull { uid ->
                    try {
                        val doc = firestore.collection("users")
                            .document(uid)
                            .get()
                            .await()
                        
                        if (doc.exists()) {
                            User(
                                uid = doc.getString("uid") ?: uid,
                                nombre = doc.getString("nombre") ?: "",
                                apellido = doc.getString("apellido") ?: "",
                                correo = doc.getString("correo") ?: "",
                                meta = (doc.getLong("meta") ?: 0L).toInt(),
                                avance = (doc.getLong("avance") ?: 0L).toInt(),
                                rol = when (doc.getString("rol")) {
                                    UserRole.MAESTRO.name -> UserRole.MAESTRO
                                    else -> UserRole.ESTUDIANTE
                                },
                                carrera = doc.getString("carrera") ?: "",
                                actividadesRealizadas = (doc.get("actividadesRealizadas") as? List<String>).orEmpty()
                            )
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
                
                students = loadedStudents
                isLoading = false
            } catch (e: Exception) {
                error = e.message ?: "Error al cargar estudiantes"
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estudiantes inscritos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${studentIds.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
            
            error != null -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            students.isEmpty() -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "No hay estudiantes inscritos aún",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            else -> {
                // Lista de estudiantes
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    students.forEach { student ->
                        StudentCard(student = student)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentCard(student: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del estudiante
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre completo
                val nombreCompleto = if (student.apellido.isBlank()) {
                    student.nombre
                } else {
                    "${student.nombre} ${student.apellido}"
                }
                
                Text(
                    text = nombreCompleto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Carrera
                Text(
                    text = student.carrera,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Carnet (si existe)
                val carnet = EmailValidator.extractCarnet(student.correo)
                if (!carnet.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = carnet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Progreso (si tiene meta configurada)
            if (student.meta > 0) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${student.avance}h",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/ ${student.meta}h",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}