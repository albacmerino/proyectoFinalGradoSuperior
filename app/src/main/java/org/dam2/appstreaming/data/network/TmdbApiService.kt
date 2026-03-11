package org.dam2.appstreaming.data.network

import org.dam2.appstreaming.ui.component.FichaPelicula
import retrofit2.http.GET
import retrofit2.http.Query

// Clase para mapear la respuesta de TMDB (ellos devuelven un objeto con una lista 'results')
data class MovieResponse(
    val results: List<FichaPelicula>
)

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): MovieResponse
}