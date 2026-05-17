package org.dam2.appstreaming.data.remote.dto.backend

/**
 * RESPUESTA DE ELEMENTO DE LISTA
 * 
 * Representa la estructura de un ítem multimedia tal cual es devuelto por el servidor
 * o recuperado de la BBDD local.
 *
 */
data class RespuestaLista(
    val idMultimedia: Int,       // ID de referencia externa
    val titulo: String,          // Nombre del contenido
    val rutaPoster: String?,     // URL parcial de la imagen
    val esPelicula: Boolean,     // Flag de discriminación de tipo
    val tipoLista: String        // Categoría a la que pertenece (FAVORITO, etc.)
)
