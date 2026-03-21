package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName
import org.dam2.appstreaming.data.model.FichaPelicula

data class MovieResponse(
    @SerializedName("results") val listaPeliculas: List<FichaPelicula>
)
