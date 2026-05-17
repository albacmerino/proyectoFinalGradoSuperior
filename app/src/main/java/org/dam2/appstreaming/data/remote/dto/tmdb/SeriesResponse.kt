package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName
import org.dam2.appstreaming.data.model.FichaSerie

/**
 * RESPUESTA DE SERIES
 * 
 * Mapea el objeto raíz de la respuesta JSON del endpoint /tv de TMDB.
 *
 */
data class SeriesResponse(
    @SerializedName("results") val listaSeries: List<FichaSerie>
)
