package org.dam2.appstreaming.data.model

import com.google.gson.annotations.SerializedName

data class Genero(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class GeneroResponse(
    @SerializedName("genres") val genres: List<Genero>
)
