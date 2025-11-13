package com.uvg.mashoras.data.models

import com.google.firebase.Timestamp

data class Activity(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Timestamp? = null,
    val cupos: Int = 0,
    val carrera: String = "",
    val finalizado: Boolean = false,
    val horasARealizar: Int = 0,
    val estudiantesInscritos: List<String> = emptyList(),
    val creadoPor: String = ""
)
