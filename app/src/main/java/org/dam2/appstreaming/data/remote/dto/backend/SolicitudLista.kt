package org.dam2.appstreaming.data.remote.dto.backend

/**
 * SOLICITUD DE ADICIÓN A LISTA
 * 
 * Clase que encapsula los datos necesarios para registrar un nuevo elemento en las 
 * colecciones del usuario (Favoritos o listas personalizadas).
 *
 */
data class SolicitudLista(
    val idMultimedia: Int,       // ID único del contenido (Movie/TV)
    val titulo: String,          // Título para visualización rápida sin re-consulta
    val rutaPoster: String?,     // Referencia visual del contenido
    val esPelicula: Boolean,     // Discriminador de tipo de medio
    val nombreUsuario: String,   // Identificador del propietario de la lista
    val tipoLista: String        // Nombre de la categoría de destino
)
