package org.dam2.appstreaming.data.remote.dto

/**
 * Datos que enviamos al servidor para iniciar sesion.
 */
data class SolicitudLogin(
    val nombreUsuario: String,
    val contrasena: String
)
