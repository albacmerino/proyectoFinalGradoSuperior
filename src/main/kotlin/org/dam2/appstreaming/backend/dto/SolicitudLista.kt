package org.dam2.appstreaming.backend.dto

/**
 * Datos que enviamos al servidor para añadir un favorito.
 */
data class SolicitudLista(
    val idMultimedia: Int,
    val titulo: String,
    val rutaPoster: String?,
    val esPelicula: Boolean,
    val nombreUsuario: String, // Lo usamos para identificar al usuario sin JWT real por ahora
    val tipoLista: String
)
