package org.dam2.appstreaming.backend.dto

/**
 * Datos recibidos para Login o Registro.
 */
data class SolicitudAutenticacion(
    val uid: String,
    val nombreUsuario: String,
    val contrasena: String
)
