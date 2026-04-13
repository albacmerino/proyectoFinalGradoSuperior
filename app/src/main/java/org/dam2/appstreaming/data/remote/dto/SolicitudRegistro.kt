package org.dam2.appstreaming.data.remote.dto

/**
 * Datos que enviamos al servidor para registrar un nuevo usuario.
 */
data class SolicitudRegistro(
    val uid: String,           // El ID de Firebase
    val nombreUsuario: String,
    val email: String? = null
)
