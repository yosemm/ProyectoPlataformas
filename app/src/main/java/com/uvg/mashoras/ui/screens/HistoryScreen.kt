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
import com.uvg.mashoras.ui.components.ProgressSection

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    currentHours: Int = 75,
    goalHours: Int = 100,
    completedActivities: List<CompletedActivity> = getDefaultCompletedActivities()
) {
    val progress = (currentHours.toFloat() / goalHours.toFloat()).coerceIn(0f, 1f)
    val progressPercentage = (progress * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }

        item {
            ProgressSection(
                progress = progress,
                progressPercentage = progressPercentage,
                currentHours = currentHours,
                goalHours = goalHours
            )
        }

        item {
            Text(
                text = "Historial",
                fontSize = 32.sp,
                fontWeight = FontWeight.W900,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        items(completedActivities.size) { index ->
            CompletedActivityCard(
                activity = completedActivities[index]
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CompletedActivityCard(
    activity: CompletedActivity
) {
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
                    color = colorScheme.primary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Completada",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W900,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 0.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier.weight(0.9f),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(26.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Finalizada: ${activity.completedDate}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W900
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Horas conseguidas: ${activity.hoursEarned}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W900
                    )
                }
            }
        }
    }
}

data class CompletedActivity(
    val title: String, 
    val completedDate: String, 
    val hoursEarned: Int
)

private fun getDefaultCompletedActivities(): List<CompletedActivity> {
    return listOf(
        CompletedActivity(
            title = "Tour por la universidad", 
            completedDate = "12/10/25", 
            hoursEarned = 1
        ), 
        CompletedActivity(
            title = "Publicación en blog", 
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