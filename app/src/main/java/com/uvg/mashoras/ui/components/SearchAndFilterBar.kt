package com.uvg.mashoras.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ActivityFilter {
    ALL,
    AVAILABLE,
    ENROLLED,
    FULL
}

enum class ActivitySort {
    NEWEST,
    OLDEST,
    MORE_HOURS,
    LESS_HOURS,
    MORE_SPOTS,
    LESS_SPOTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: ActivityFilter,
    onFilterChange: (ActivityFilter) -> Unit,
    selectedSort: ActivitySort,
    onSortChange: (ActivitySort) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar actividades...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = Color.Gray
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        // Filtros y Ordenamiento
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón de Filtro
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { showFilterMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedFilter != ActivityFilter.ALL) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = when (selectedFilter) {
                            ActivityFilter.ALL -> "Filtrar"
                            ActivityFilter.AVAILABLE -> "Disponibles"
                            ActivityFilter.ENROLLED -> "Inscritas"
                            ActivityFilter.FULL -> "Llenas"
                        },
                        fontSize = 14.sp
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedFilter == ActivityFilter.ALL) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Todas")
                            }
                        },
                        onClick = {
                            onFilterChange(ActivityFilter.ALL)
                            showFilterMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedFilter == ActivityFilter.AVAILABLE) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Disponibles")
                            }
                        },
                        onClick = {
                            onFilterChange(ActivityFilter.AVAILABLE)
                            showFilterMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedFilter == ActivityFilter.ENROLLED) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Inscritas")
                            }
                        },
                        onClick = {
                            onFilterChange(ActivityFilter.ENROLLED)
                            showFilterMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedFilter == ActivityFilter.FULL) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Llenas")
                            }
                        },
                        onClick = {
                            onFilterChange(ActivityFilter.FULL)
                            showFilterMenu = false
                        }
                    )
                }
            }

            // Botón de Ordenamiento
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Ordenar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = when (selectedSort) {
                            ActivitySort.NEWEST -> "Nuevas"
                            ActivitySort.OLDEST -> "Antiguas"
                            ActivitySort.MORE_HOURS -> "+ horas"
                            ActivitySort.LESS_HOURS -> "- horas"
                            ActivitySort.MORE_SPOTS -> "+ cupos"
                            ActivitySort.LESS_SPOTS -> "- cupos"
                        },
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.NEWEST) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Más nuevas")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.NEWEST)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.OLDEST) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Más antiguas")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.OLDEST)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.MORE_HOURS) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Más horas")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.MORE_HOURS)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.LESS_HOURS) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Menos horas")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.LESS_HOURS)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.MORE_SPOTS) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Más cupos")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.MORE_SPOTS)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedSort == ActivitySort.LESS_SPOTS) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Menos cupos")
                            }
                        },
                        onClick = {
                            onSortChange(ActivitySort.LESS_SPOTS)
                            showSortMenu = false
                        }
                    )
                }
            }
        }
    }
}