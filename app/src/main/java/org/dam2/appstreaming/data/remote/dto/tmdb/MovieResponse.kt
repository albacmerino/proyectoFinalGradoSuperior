package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName
import org.dam2.appstreaming.data.model.FichaPelicula

/**
 * RESPUESTA DE PELÍCULAS
 * 
 * Clase que mapea el objeto raíz de la respuesta JSON del endpoint /movie de TMDB.
 *
 */
data class MovieResponse(
    @SerializedName("results") val listaPeliculas: List<FichaPelicula>
)
