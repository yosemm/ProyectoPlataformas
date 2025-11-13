package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.mashoras.ui.components.StudentProgressHeader

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    completedActivities: List<CompletedActivity> = getDefaultCompletedActivities()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(19.dp))
        }

        // Header de progreso (solo estudiante, se encarga StudentProgressHeader)
        item {
            StudentProgressHeader()
        }

        item {
            Text(
                text = "Historial de actividades",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(completedActivities) { activity ->
            CompletedActivityCard(activity = activity)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class CompletedActivity(
    val title: String,
    val completedDate: String,
    val hoursEarned: Int
)

@Composable
fun CompletedActivityCard(activity: CompletedActivity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Fecha: ${activity.completedDate}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(70.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+${activity.hoursEarned}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "horas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun getDefaultCompletedActivities(): List<CompletedActivity> {
    return listOf(
        CompletedActivity(
            title = "Taller de liderazgo",
            completedDate = "12/10/25",
            hoursEarned = 4
        ),
        CompletedActivity(
            title = "Voluntariado comunitario",
            completedDate = "13/10/25",
            hoursEarned = 2
        ),
        CompletedActivity(
            title = "Evento de networking",
            completedDate = "11/10/25",
            hoursEarned = 3
        ),
        CompletedActivity(
            title = "Workshop de desarrollo",
            completedDate = "10/10/25",
            hoursEarned = 4
        )
    )
}
