package com.uvg.mashoras.data.models

import java.util.Date

data class Activity(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Date? = null,
    val cupos: Int = 0,
    val carrera: String = "", // Puede ser "Todas" o una carrera específica
    val finalizado: Boolean = false,
    val horasArealizar: Int = 0,
    val estudiantesInscritos: List<String> = emptyList() // Lista de UIDs de usuarios
)