package org.dam2.appstreaming.ui.component


import com.google.gson.annotations.SerializedName

data class FichaPelicula(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("overview") val overview: String,
    //enlace oficial de la pelicula (netflix, amazon, etc)
    @SerializedName("homepage") val homepage: String? = null
)
