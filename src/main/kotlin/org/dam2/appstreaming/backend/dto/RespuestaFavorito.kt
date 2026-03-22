package org.dam2.appstreaming.backend.dto

/**
 * Representa un favorito que el servidor devuelve a la app.
 */
data class RespuestaFavorito(
    val idMultimedia: Int,
    val titulo: String,
    val rutaPoster: String?,
    val esPelicula: Boolean
)
