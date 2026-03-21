package org.dam2.appstreaming.ui.component

<<<<<<< Updated upstream
class FichaSerie {
}
=======
import com.google.gson.annotations.SerializedName

data class FichaSerie(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("origin_country") val originCountry: List<String>? = null,
    @SerializedName("original_name") val originalName: String? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = emptyList(),
    //enlace oficial de la serie (netflix, amazon, etc)
    @SerializedName("homepage") val homepage: String? = null
)
>>>>>>> Stashed changes
