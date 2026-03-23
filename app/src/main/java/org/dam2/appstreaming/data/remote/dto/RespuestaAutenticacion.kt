package org.dam2.appstreaming.data.remote.dto

/**
 * Respuesta que recibimos del servidor tras un login o registro exitoso.
 */
data class RespuestaAutenticacion(
    val token: String,
    val nombreUsuario: String
)
