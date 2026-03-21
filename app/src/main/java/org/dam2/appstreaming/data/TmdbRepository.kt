package org.dam2.appstreaming.data

import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.data.network.TmdbApiService
import org.dam2.appstreaming.data.network.WatchCountryInfo
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.Genero
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbConfig {
    const val API_KEY = "f5d89b86e2371eaced7a3d62db4d286c"
    const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNWQ4OWI4NmUyMzcxZWFjZWQ3YTNkNjJkYjRkMjg2YyIsIm5iZiI6MTc2OTY0MzE1NS4xNSwic3ViIjoiNjk3YTljOTMwODNhZWExMzBiYjE2OGFiIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.AJH7nc1wL_3zjMu_JLjgqV_ryGdxT_8d1MdrIb7TR5A"
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
}

class TmdbRepository {

    // chain -> funcion lambda que recibe un objeto de tipo Interceptor.Chain y devuelve un objeto de tipo Response
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${TmdbConfig.ACCESS_TOKEN}")
            .addHeader("accept", "application/json")
            .build()
        chain.proceed(newRequest)
    }.build()

    private val api = Retrofit.Builder()
        .baseUrl(TmdbConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApiService::class.java)

    // PELÍCULAS
    suspend fun getPopularMovies(): List<FichaPelicula> = try { api.getPopularMovies().listaPeliculas } catch (e: Exception) { emptyList() }
    suspend fun getNowPlayingMovies(): List<FichaPelicula> = try { api.getNowPlayingMovies().listaPeliculas } catch (e: Exception) { emptyList() }
    suspend fun getTopRatedMovies(): List<FichaPelicula> = try { api.getTopRatedMovies().listaPeliculas } catch (e: Exception) { emptyList() }

    suspend fun getMovieDetails(movieId: Int): FichaPelicula? = try { api.getMovieDetails(movieId) } catch (e: Exception) { null }

    suspend fun getMovieTrailer(movieId: Int): String? {
        return try {
            val response = api.getMovieVideos(movieId)
            val trailer = response.listaVideos.find { it.site == "YouTube" && it.type == "Trailer" }
                ?: response.listaVideos.find { it.site == "YouTube" }
            trailer?.key
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieWatchLink(movieId: Int): String? {
        return try {
            val response = api.getMovieWatchProviders(movieId)
            response.results["ES"]?.link ?: response.results.values.firstOrNull()?.link
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieWatchProvidersData(movieId: Int): WatchCountryInfo? {
        return try {
            val response = api.getMovieWatchProviders(movieId)
            response.results["ES"] ?: response.results.values.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieCertification(movieId: Int): String? {
        return try {
            val response = api.getMovieReleaseDates(movieId)

            // 1. Intentamos buscar específicamente España (ES)
            val spainResults = response.results.find { it.iso_3166_1 == "ES" }

            // 2. Corregido: Usamos releaseDates (el nombre de la variable en tu data class)
            val certificationES = spainResults?.releaseDates?.find { it.certification.isNotBlank() }?.certification

            // 3. Corregido: Usamos releaseDates aquí también
            val certificationUS = response.results.find { it.iso_3166_1 == "US" }
                ?.releaseDates?.find { it.certification.isNotBlank() }?.certification

            // 4. Corregido: Usamos releaseDates en el flatMap
            val fallbackCertification = response.results
                .flatMap { it.releaseDates }
                .find { it.certification.isNotBlank() }?.certification

            // Prioridad: ES > US > Cualquiera > null
            certificationES ?: certificationUS ?: fallbackCertification

        } catch (e: Exception) {
            android.util.Log.e("TMDB_REPO", "Error al obtener certificación: ${e.message}")
            null
        }
    }
    /*suspend fun getMovieCertification(movieId: Int): String? {
        return try {
            val response = api.getMovieReleaseDates(movieId)
            // Buscamos la certificación en España (ES) o en su defecto la primera disponible
            val spainResults = response.results.find { it.iso_3166_1 == "ES" }
            val certification = spainResults?.release_dates?.find { it.certification.isNotEmpty() }?.certification
                ?: response.results.flatMap { it.release_dates }.find { it.certification.isNotEmpty() }?.certification
            certification
        } catch (e: Exception) {
            null
        }
    }*/

    suspend fun getMovieCast(movieId: Int): List<CastMember> {
        return try {
            api.getMovieCredits(movieId).cast
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMovieReviews(movieId: Int): List<Review> {
        return try {
            api.getMovieReviews(movieId).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    // SERIES
    suspend fun getPopularSeries(): List<FichaSerie> = try { api.getPopularSeries().listaSeries } catch (e: Exception) { emptyList() }
    suspend fun getTopRatedSeries(): List<FichaSerie> = try { api.getTopRatedSeries().listaSeries } catch (e: Exception) { emptyList() }
    suspend fun getOnTheAirSeries(): List<FichaSerie> = try { api.getOnTheAirSeries().listaSeries } catch (e: Exception) { emptyList() }

    suspend fun getSeriesTrailer(seriesId: Int): String? {
        return try {
            val response = api.getSeriesVideos(seriesId)
            val trailer = response.listaVideos.find { it.site == "YouTube" && it.type == "Trailer" }
                ?: response.listaVideos.find { it.site == "YouTube" }
            trailer?.key
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSeriesWatchLink(seriesId: Int): String? {
        return try {
            val response = api.getSeriesWatchProviders(seriesId)
            response.results["ES"]?.link ?: response.results.values.firstOrNull()?.link
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSeriesWatchProvidersData(seriesId: Int): WatchCountryInfo? {
        return try {
            val response = api.getSeriesWatchProviders(seriesId)
            response.results["ES"] ?: response.results.values.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSeriesCast(seriesId: Int): List<CastMember> {
        return try {
            api.getSeriesCredits(seriesId).cast
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSeriesReviews(seriesId: Int): List<Review> {
        return try {
            api.getSeriesReviews(seriesId).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    // GÉNEROS
    suspend fun getMovieGenres(): List<Genero> = try { api.getMovieGenres().genres } catch (e: Exception) { emptyList() }
    suspend fun getTvGenres(): List<Genero> = try { api.getTvGenres().genres } catch (e: Exception) { emptyList() }
}