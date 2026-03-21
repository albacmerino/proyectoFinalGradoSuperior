package org.dam2.appstreaming.ui.component

import com.google.gson.annotations.SerializedName

/**
 * Representa la información de una película desde la API de TMDB.
 */
data class FichaPelicula(
    @SerializedName("id") override val id: Int,
    @SerializedName("title") override val titulo: String,
    @SerializedName("overview") override val sinopsis: String,
    @SerializedName("poster_path") override val rutaPoster: String?,
    @SerializedName("backdrop_path") override val rutaFondo: String?,
    @SerializedName("release_date") override val fechaLanzamiento: String?,
    @SerializedName("vote_average") override val puntuacionMedia: Double,
    @SerializedName("genre_ids") override val idsGeneros: List<Int>?,
    @SerializedName("homepage") override val enlaceWeb: String? = null
) : ItemMultimedia
