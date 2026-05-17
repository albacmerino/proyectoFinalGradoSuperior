package org.dam2.appstreaming.data.model

import com.google.gson.annotations.SerializedName

/**
 * GÉNERO
 *
 * Representa una categoría cinematográfica (acción, comedia, etc.).
 * Se utiliza tanto para el filtrado de contenido como para la visualización de etiquetas en el detalle.
 */
data class Genero(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

/**
 * RESPUESTA DE GÉNEROS
 * Contenedor para la respuesta de la API que devuelve una lista de géneros.
 */
data class GeneroResponse(
    @SerializedName("genres") val genres: List<Genero>
)
