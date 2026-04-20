package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint /search/multi de TMDB.
 * Contiene una lista de resultados mezclados (películas, series y personas).
 */
data class MultiSearchResponse(
    @SerializedName("results") val results: List<ResultadoBusqueda>,
    @SerializedName("total_results") val totalResultados: Int,
    @SerializedName("total_pages") val totalPaginas: Int
)

/**
 * Un resultado individual de la búsqueda multi.
 *
 * El campo [mediaType] indica qué tipo de contenido es:
 * - "movie"  → película
 * - "tv"     → serie
 * - "person" → persona (la ignoramos en la UI)
 *
 * TMDB mezcla los campos de película y serie en el mismo objeto,
 * así que algunos campos pueden ser nulos dependiendo del tipo.
 */
data class ResultadoBusqueda(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String,

    // Título: las películas usan "title", las series usan "name"
    @SerializedName("title") val titulo: String?,
    @SerializedName("name") val nombre: String?,

    @SerializedName("overview") val sinopsis: String?,
    @SerializedName("poster_path") val rutaPoster: String?,
    @SerializedName("backdrop_path") val rutaFondo: String?,
    @SerializedName("vote_average") val puntuacion: Double?,
    @SerializedName("genre_ids") val idsGeneros: List<Int>?,

    // Solo en películas
    @SerializedName("release_date") val fechaLanzamiento: String?,

    // Solo en series
    @SerializedName("first_air_date") val fechaEmision: String?
) {
    /** Nombre para mostrar: películas tienen "title", series tienen "name". */
    val tituloMostrar: String get() = titulo ?: nombre ?: "Sin título"

    /** true si es película, false si es serie. */
    val esPelicula: Boolean get() = mediaType == "movie"

    /** Fecha formateada para mostrar (solo el año). */
    val anio: String? get() = (fechaLanzamiento ?: fechaEmision)?.take(4)
}