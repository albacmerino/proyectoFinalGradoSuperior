package org.dam2.appstreaming.data.network

import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import retrofit2.http.GET
import retrofit2.http.Query

// Clase para mapear la respuesta de TMDB (ellos devuelven un objeto con una lista 'results')
data class MovieResponse(
    val results: List<FichaPelicula>
)
data class SeriesResponse(
    val results: List<FichaSerie>
)
interface TmdbApiService {
    // PELÍCULAS
    @GET("movie/popular")
    suspend fun getPopularMovies(): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(): MovieResponse

    // SERIES
    @GET("tv/popular")
    suspend fun getPopularSeries(): SeriesResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(): SeriesResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirSeries(): SeriesResponse
}