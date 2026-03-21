package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName
import org.dam2.appstreaming.data.model.FichaSerie

data class SeriesResponse(
    @SerializedName("results") val listaSeries: List<FichaSerie>
)
