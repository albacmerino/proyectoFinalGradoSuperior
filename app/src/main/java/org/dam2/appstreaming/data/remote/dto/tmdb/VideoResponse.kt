package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * RESPUESTA DE VÍDEOS
 * 
 * Clase que mapea los recursos multimedia (trailers, teasers) asociados a un contenido.
 *
 */
data class VideoResponse(
    @SerializedName("results") val listaVideos: List<Video>
)

data class Video(
    val key: String,    // ID del vídeo en la plataforma (ej. YouTube)
    val site: String,   // Plataforma
    val type: String    // Categoría del vídeo
)
