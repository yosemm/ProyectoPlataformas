package com.uvg.mashoras.utils

import com.uvg.mashoras.data.models.UserRole

object EmailValidator {
    
    /**
     * Regex para validar correo de estudiante UVG
     * Formato: 3 letras (apellido) + 5-7 dígitos (carnet) + @uvg.edu.gt
     * Ejemplos: par24761@uvg.edu.gt, ism123456@uvg.edu.gt
     */
    private val STUDENT_EMAIL_REGEX = Regex("^[a-zA-Z]{3}\\d{5,7}@uvg\\.edu\\.gt$")
    
    /**
     * Valida si el correo es institucional de UVG
     */
    fun isUvgEmail(email: String): Boolean {
        return email.endsWith("@uvg.edu.gt")
    }
    
    /**
     * Determina si el correo corresponde a un estudiante
     * basándose en el patrón: 3 letras + 5-7 dígitos
     */
    fun isStudentEmail(email: String): Boolean {
        return STUDENT_EMAIL_REGEX.matches(email)
    }
    
    /**
     * Determina el rol del usuario basándose en su correo
     */
    fun determineUserRole(email: String): UserRole {
        return if (isStudentEmail(email)) {
            UserRole.ESTUDIANTE
        } else {
            UserRole.MAESTRO
        }
    }
    
    /**
     * Extrae el número de carnet del correo de estudiante
     * Retorna null si no es un correo válido de estudiante
     */
    fun extractCarnet(email: String): String? {
        if (!isStudentEmail(email)) return null
        
        val match = STUDENT_EMAIL_REGEX.find(email) ?: return null
        val localPart = email.substringBefore("@")
        return localPart.substring(3) // Obtiene los dígitos después de las 3 letras
    }
}