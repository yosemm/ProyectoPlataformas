package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.mashoras.R
import com.uvg.mashoras.ui.components.ProgressSection

@Composable
fun ActivitiesDashboard(
    currentHours: Int = 75,
    goalHours: Int = 100,
    onActivityJoin: (String) -> Unit = {}
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
                text = "Actividades disponibles",
                fontSize = 32.sp,
                fontWeight = FontWeight.W900,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            ActivityCard(
                title = "Tour por la universidad",
                description = "Conviértete en guía de tours y muestra lo mejor de nuestra universidad.",
                date = "12/10/25",
                time = "10:00 - 11:30 am",
                spots = "2/5",
                isEnrolled = false,
                illustration = "tour",
                onJoinClick = { onActivityJoin("tour") })
        }

        item {
            ActivityCard(
                title = "Publicación en blog",
                description = "Redacta un artículo sobre la inteligencia artificial y su influencia en los estudios universitarios.",
                date = "13/10/25",
                time = "5:00 - 7:00 pm",
                spots = "2/5",
                isEnrolled = false,
                illustration = "blog",
                onJoinClick = { onActivityJoin("blog") })
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    description: String,
    date: String,
    time: String,
    spots: String,
    isEnrolled: Boolean,
    illustration: String,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, top = 16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W900,
                    color = colorScheme.primary,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (!isEnrolled) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorScheme.error),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "No enrolad@",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 0.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (illustration) {
                        "tour" -> {
                            Image(
                                modifier = Modifier.requiredSize(140.dp),
                                painter = painterResource(id = R.drawable.tour),
                                contentDescription = "Ilustracion de tour",
                                contentScale = ContentScale.Fit
                            )
                        }

                        "blog" -> {
                            Image(
                                modifier = Modifier.requiredSize(140.dp),
                                painter = painterResource(id = R.drawable.blog),
                                contentDescription = "Ilustracion de blog",
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier.weight(1.7f),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier.padding(13.dp)
                ) {
                    Text(
                        text = description,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.W900,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fecha: $date",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W900
                    )

                    Text(
                        text = "Hora: $time",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            modifier = Modifier
                                .height(32.dp)
                                .weight(0.9f),
                            onClick = onJoinClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White, contentColor = colorScheme.primary
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Unirse", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Button(
                            modifier = Modifier
                                .height(32.dp)
                                .weight(0.9f),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, color = Color.White)
                        ) {
                            Text(
                                text = "Cupos: $spots",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}