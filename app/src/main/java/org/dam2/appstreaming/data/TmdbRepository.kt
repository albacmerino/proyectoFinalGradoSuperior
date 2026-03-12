package org.dam2.appstreaming.data

import androidx.activity.result.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.appstreaming.data.network.TmdbApiService
import org.dam2.appstreaming.ui.component.FichaPelicula
import okhttp3.OkHttpClient
import org.dam2.appstreaming.ui.component.FichaSerie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbConfig {

    const val API_KEY = "f5d89b86e2371eaced7a3d62db4d286c"

    const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNWQ4OWI4NmUyMzcxZWFjZWQ3YTNkNjJkYjRkMjg2YyIsIm5iZiI6MTc2OTY0MzE1NS4xNSwic3ViIjoiNjk3YTljOTMwODNhZWExMzBiYjE2OGFiIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.AJH7nc1wL_3zjMu_JLjgqV_ryGdxT_8d1MdrIb7TR5A"

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
}

class TmdbRepository {

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

    // FUNCIONES PURAS DE RED
    suspend fun getPopularMovies(): List<FichaPelicula> = try { api.getPopularMovies().results } catch (e: Exception) { emptyList() }
    suspend fun getNowPlayingMovies(): List<FichaPelicula> = try { api.getNowPlayingMovies().results } catch (e: Exception) { emptyList() }
    suspend fun getTopRatedMovies(): List<FichaPelicula> = try { api.getTopRatedMovies().results } catch (e: Exception) { emptyList() }

    suspend fun getPopularSeries(): List<FichaSerie> = try { api.getPopularSeries().results } catch (e: Exception) { emptyList() }
    suspend fun getTopRatedSeries(): List<FichaSerie> = try { api.getTopRatedSeries().results } catch (e: Exception) { emptyList() }
    suspend fun getOnTheAirSeries(): List<FichaSerie> = try { api.getOnTheAirSeries().results } catch (e: Exception) { emptyList() }
}