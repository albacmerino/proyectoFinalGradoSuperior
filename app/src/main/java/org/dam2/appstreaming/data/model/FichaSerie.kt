package org.dam2.appstreaming.data.model

import com.google.gson.annotations.SerializedName

/**
 * FICHA SERIE
 *
 * Representa la entidad Serie de TV en la capa de negocio.
 * Contiene atributos específicos que la diferencian de una película, como el país de origen.
 *
 */
data class FichaSerie(

    @SerializedName("id") override val id: Int,
    @SerializedName("name") override val titulo: String,
    @SerializedName("overview") override val sinopsis: String,
    @SerializedName("poster_path") override val rutaPoster: String?,
    @SerializedName("backdrop_path") override val rutaFondo: String?,
    @SerializedName("first_air_date") override val fechaLanzamiento: String?,
    @SerializedName("vote_average") override val puntuacionMedia: Double,
    @SerializedName("genre_ids") override val idsGeneros: List<Int>?,
    @SerializedName("origin_country") val paisOrigen: List<String>? = null,
    @SerializedName("original_name") val nombreOriginal: String? = null,
    @SerializedName("homepage") override val enlaceWeb: String? = null
) : ItemMultimedia
