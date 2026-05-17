package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * RESPUESTA DE FECHAS DE LANZAMIENTO Y CERTIFICACIÓN
 * 
 * Clase que mapea el endpoint /release_dates de TMDB. Se utiliza principalmente
 * para obtener la clasificación por edades (certificación) del contenido.
 *
 */
data class ReleaseDatesResponse(
    val results: List<RegionReleaseDate>
)

/**
 * Información de lanzamiento agrupada por región geográfica.
 */
data class RegionReleaseDate(
    @SerializedName("iso_3166_1") val iso31661: String,
    @SerializedName("release_dates") val releaseDates: List<CertificationItem>
)

/**
 * Detalle individual de un lanzamiento, incluyendo la clasificación por edades.
 */
data class CertificationItem(
    val certification: String // Ejemplo: "TP", "12", "16", "18"
)
