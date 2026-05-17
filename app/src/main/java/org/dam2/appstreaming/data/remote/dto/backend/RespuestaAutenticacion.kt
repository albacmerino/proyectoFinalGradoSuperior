package org.dam2.appstreaming.data.remote.dto.backend

/**
 * RESPUESTA DE AUTENTICACIÓN
 * 
 * Clase que representa el objeto de respuesta tras un proceso exitoso de Login o Registro
 * en el servidor backend propio.
 *
 */
data class RespuestaAutenticacion(
    val token: String,          // Identificador único de sesión
    val nombreUsuario: String   // Alias del usuario para mostrar en la interfaz
)
