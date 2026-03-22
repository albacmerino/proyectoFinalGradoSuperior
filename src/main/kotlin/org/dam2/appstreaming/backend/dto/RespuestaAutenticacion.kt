package org.dam2.appstreaming.backend.dto

/**
 * Respuesta enviada tras un login o registro exitoso.
 */
data class RespuestaAutenticacion(
    val token: String,
    val nombreUsuario: String
)
