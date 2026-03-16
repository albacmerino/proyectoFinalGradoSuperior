package org.dam2.appstreaming.data.network


import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.GeneroResponse
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

    // --- GÉNEROS ---

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse
}