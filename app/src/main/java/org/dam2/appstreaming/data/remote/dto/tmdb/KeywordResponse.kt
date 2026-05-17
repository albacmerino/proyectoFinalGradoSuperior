package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * RESPUESTA DE PALABRAS CLAVE
 * 
 * Clase que mapea las etiquetas o términos de búsqueda asociados a una película o serie.
 *
 */
data class KeywordResponse(
    val id: Int,
    @SerializedName("keywords") val keywords: List<Keyword>? = null,
    @SerializedName("results") val results: List<Keyword>? = null
)

/**
 * Representa una palabra clave individual.
 */
data class Keyword(
    val id: Int,
    val name: String
)
