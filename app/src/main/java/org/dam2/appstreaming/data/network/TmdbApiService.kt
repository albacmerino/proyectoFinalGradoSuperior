package org.dam2.appstreaming.data.network

import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.GeneroResponse
import org.dam2.appstreaming.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    // Descubrir películas por género
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
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

    // Descubrir series por género
    @GET("discover/tv")
    suspend fun discoverSeries(
        @Query("with_genres") genreId: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("language") lang: String = "es-ES"
    ): SeriesResponse

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
