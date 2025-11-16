package com.uvg.mashoras.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.uvg.mashoras.data.models.Activity
import com.uvg.mashoras.data.models.UserRole
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke

@Composable
fun ActivityDetailDialog(
    activity: Activity,
    userRole: UserRole,
    currentUserUid: String?,
    isEnrolled: Boolean,
    onDismiss: () -> Unit,
    onEnroll: () -> Unit,
    onUnenroll: () -> Unit,
    onMarkCompleted: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    val isFull = activity.estudiantesInscritos.size >= activity.cupos
    val isCreator = currentUserUid == activity.creadoPor

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
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
                        text = activity.titulo,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Gray
                        )
                    }
                }

                // Estado
                if (activity.finalizado) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Actividad Finalizada",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else if (isEnrolled) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Inscrito",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido scrolleable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Descripción
                    Text(
                        text = "Descripción",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = activity.descripcion,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )

                    Divider()

                    // Detalles
                    Text(
                        text = "Detalles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    DetailRow("Fecha", formatDate(activity.fecha?.toDate()))
                    DetailRow("Horas a realizar", "${activity.horasARealizar}h")
                    DetailRow("Carrera", activity.carrera)
                    DetailRow("Cupos", "${activity.estudiantesInscritos.size}/${activity.cupos}")
                    
                    if (isFull && !isEnrolled && userRole == UserRole.ESTUDIANTE) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ Actividad llena - No hay cupos disponibles",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // 👇 NUEVO: Lista de estudiantes inscritos (solo para maestros creadores)
                    if (userRole == UserRole.MAESTRO && isCreator && activity.estudiantesInscritos.isNotEmpty()) {
                        Divider()
                        
                        EnrolledStudentsList(
                            studentIds = activity.estudiantesInscritos,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                if (!activity.finalizado) {
                    when (userRole) {
                        UserRole.ESTUDIANTE -> {
                            // Botones para estudiantes
                            if (isEnrolled) {
                                Button(
                                    onClick = {
                                        onUnenroll()
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Desinscribirse")
                                }
                            } else if (!isFull) {
                                Button(
                                    onClick = {
                                        onEnroll()
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Inscribirse")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cerrar")
                                }
                            }
                        }
                        
                        UserRole.MAESTRO -> {
                            if (isCreator) {
                                // Botones para maestro creador
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onMarkCompleted()
                                            onDismiss()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Marcar como Finalizada")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            onEdit()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Editar Actividad")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            onDelete()
                                            onDismiss()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cerrar")
                                }
                            }
                        }
                    }
                } else {
                    // Actividad finalizada - solo botón cerrar
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return "Sin fecha"
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}