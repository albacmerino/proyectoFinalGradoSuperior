package org.dam2.appstreaming.data.remote.dto

/**
 * Datos que enviamos al servidor para registrar un nuevo usuario.
 */
data class SolicitudRegistro(
    val nombreUsuario: String,
    val contrasena: String,
)
