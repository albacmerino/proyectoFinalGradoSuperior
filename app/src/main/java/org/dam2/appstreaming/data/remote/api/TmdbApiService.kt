package org.dam2.appstreaming.data.remote.api

import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.GeneroResponse
import org.dam2.appstreaming.data.remote.dto.tmdb.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * INTERFAZ TMDB API SERVICE
 * 
 * Esta interfaz define el contrato de comunicación con la API externa de The Movie Database (TMDB).
 * Utiliza la librería Retrofit para convertir las definiciones de los métodos en llamadas HTTP.
 *
 */
interface TmdbApiService {

    // SECCIÓN DE PELÍCULAS
    /**
     * Obtiene una lista de películas populares.
     * @param lang Idioma de los resultados (por defecto español de España).
     * @param page Número de página para la paginación de la API.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): MovieResponse

    /**
     * Obtiene los detalles completos de una película específica.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): FichaPelicula

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): VideoResponse

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

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES"
    ): ReviewResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): MovieResponse

    @GET("movie/{movie_id}/keywords")
    suspend fun getMovieKeywords(
        @Path("movie_id") movieId: Int
    ): KeywordResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("language") lang: String = "es-ES"
    ): MovieResponse

    // SECCIÓN DE SERIES
    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): SeriesResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): SeriesResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirSeries(
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): SeriesResponse

    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
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
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int
    ): SeriesResponse

    @GET("tv/{series_id}/keywords")
    suspend fun getSeriesKeywords(
        @Path("series_id") seriesId: Int
    ): KeywordResponse

    @GET("discover/tv")
    suspend fun discoverSeries(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

    // SECCIÓN DE GÉNEROS Y BÚSQUEDA
    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("language") lang: String = "es-ES"
    ): GeneroResponse

    @GET("search/multi")
    suspend fun buscarMulti(
        @Query("query") consulta: String,
        @Query("language") lang: String = "es-ES",
        @Query("page") page: Int = 1
    ): MultiSearchResponse
}
