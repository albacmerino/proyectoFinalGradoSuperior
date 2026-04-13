package org.dam2.appstreaming.backend.dto

/**
 * DTO para recibir los datos de sincronización desde Android.
 * Debe coincidir con el JSON que envía el móvil.
 */
data class SolicitudRegistro(
    val uid: String,
    val nombreUsuario: String,
    val email: String? = null
)