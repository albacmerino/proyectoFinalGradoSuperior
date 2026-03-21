package org.dam2.appstreaming.data.network

import com.google.gson.annotations.SerializedName
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.GeneroResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Clase para mapear la respuesta de TMDB (ellos devuelven un objeto con una lista 'results')
data class MovieResponse(
    @SerializedName("results") val listaPeliculas: List<FichaPelicula>
)
data class SeriesResponse(
    @SerializedName("results") val listaSeries: List<FichaSerie>
)

data class VideoResponse(
    @SerializedName("results") val listaVideos: List<Video>
)

data class Video(
    val key: String,
    val site: String,
    val type: String
)

data class WatchProvidersResponse(
    val results: Map<String, WatchCountryInfo>
)

data class WatchCountryInfo(
    val link: String,
    val flatrate: List<Provider>? = null
)

data class Provider(
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String
)

data class ReleaseDatesResponse(
    val results: List<RegionReleaseDate>
)

data class RegionReleaseDate(
    @SerializedName("iso_3166_1") val iso31661: String,
    @SerializedName("release_dates") val releaseDates: List<CertificationItem>
)

data class CertificationItem(
    val certification: String
)

data class CreditsResponse(
    val cast: List<CastMember>
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class ReviewResponse(
    @SerializedName("results") val results: List<Review>
)

data class Review(
    val author: String,
    val content: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails
)

data class AuthorDetails(
    val name: String,
    val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    val rating: Double?
)

data class KeywordResponse(
    val id: Int,
    @SerializedName("keywords") val keywords: List<Keyword>? = null,
    @SerializedName("results") val results: List<Keyword>? = null
)

data class Keyword(
    val id: Int,
    val name: String
)

interface TmdbApiService {

    // --- PELÍCULAS ---

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") lang: String = "es-ES"
    ): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") lang: String = "es-ES"
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") lang: String = "es-ES"
    ): MovieResponse

    // Para obtener los detalles de una película
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): FichaPelicula

    // Para obtener los videos de una película
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): VideoResponse

    // Para consultar en que plataformas de streaming esta disponible una película
    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int
    ): WatchProvidersResponse

    @GET("movie/{movie_id}/release_dates")
    suspend fun getMovieReleaseDates(
        @Path("movie_id") movieId: Int
    ): ReleaseDatesResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): CreditsResponse

    //consultar reseñas
    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): ReviewResponse

    //recomendaciones
    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): MovieResponse

    //palabras clave
    @GET("movie/{movie_id}/keywords")
    suspend fun getMovieKeywords(
        @Path("movie_id") movieId: Int
    ): KeywordResponse

    // --- SERIES ---

    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirSeries(
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES"
    ): FichaSerie

    @GET("tv/{series_id}/videos")
    suspend fun getSeriesVideos(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES"
    ): VideoResponse

    @GET("tv/{series_id}/watch/providers")
    suspend fun getSeriesWatchProviders(
        @Path("series_id") seriesId: Int
    ): WatchProvidersResponse

    @GET("tv/{series_id}/credits")
    suspend fun getSeriesCredits(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES"
    ): CreditsResponse

    @GET("tv/{series_id}/reviews")
    suspend fun getSeriesReviews(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES"
    ): ReviewResponse

    @GET("tv/{series_id}/recommendations")
    suspend fun getSeriesRecommendations(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

    @GET("tv/{series_id}/keywords")
    suspend fun getSeriesKeywords(
        @Path("series_id") seriesId: Int
    ): KeywordResponse

    // --- GENEROS ---

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse
}