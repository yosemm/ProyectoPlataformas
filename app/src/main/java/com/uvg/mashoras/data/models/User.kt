package com.uvg.mashoras.data.models

data class User(
    val uid: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val meta: Int = 0, // Meta de horas a completar
    val avance: Int = 0, // Horas completadas
    val rol: UserRole = UserRole.ESTUDIANTE,
    val carrera: String = "",
    val actividadesRealizadas: List<String> = emptyList() // Lista de IDs de actividades
)

enum class UserRole {
    ESTUDIANTE,
    MAESTRO
}