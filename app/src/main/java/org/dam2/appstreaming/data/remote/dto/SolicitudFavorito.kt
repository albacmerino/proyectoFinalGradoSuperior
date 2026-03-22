package org.dam2.appstreaming.data.remote.dto

/**
 * Datos que enviamos al servidor para añadir un favorito.
 */
data class SolicitudFavorito(
    val idMultimedia: Int,
    val titulo: String,
    val rutaPoster: String?,
    val esPelicula: Boolean,
    val nombreUsuario: String
)
