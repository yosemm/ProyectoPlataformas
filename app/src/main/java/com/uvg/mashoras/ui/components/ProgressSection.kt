package com.uvg.mashoras.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.mashoras.ui.theme.AppTypography

/**
 * Componente compartido que muestra el progreso circular y las horas actuales vs. meta
 */
@Composable
fun ProgressSection(
    progress: Float,
    progressPercentage: Int,
    currentHours: Int,
    goalHours: Int
) {
    val errorColor = colorScheme.error
    val primaryColor = colorScheme.primary
    
    Row(
        modifier = Modifier.fillMaxWidth(0.85f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Círculo de progreso
        Box(
            contentAlignment = Alignment.Center, 
            modifier = Modifier.size(120.dp)
        ) {
            Canvas(
                modifier = Modifier.size(120.dp)
            ) {
                val strokeWidth = 12.dp.toPx()
                val radius = (minOf(size.width, size.height) - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                val innerSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                // Círculo de fondo (rojo)
                drawCircle(
                    color = errorColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Arco de progreso (verde)
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Square),
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = innerSize
                )

                // Pequeño arco rojo al final si no está completo
                if (progress < 1f) {
                    drawArc(
                        color = errorColor,
                        startAngle = -90f + (360f * progress),
                        sweepAngle = 20f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Square),
                        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                        size = innerSize
                    )
                }
            }

            // Porcentaje en el centro
            Text(
                text = "$progressPercentage%",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                style = AppTypography.titleLarge,
                color = primaryColor
            )
        }

        // Tarjeta de información
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Tu meta: $goalHours horas",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W900
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu avance: $currentHours horas",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W900
                )
            }
        }
    }
}