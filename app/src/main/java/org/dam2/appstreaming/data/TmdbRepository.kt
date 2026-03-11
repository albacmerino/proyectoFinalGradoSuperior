package org.dam2.appstreaming.data

import org.dam2.appstreaming.data.network.TmdbApiService
import org.dam2.appstreaming.ui.component.FichaPelicula
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

    // Configuramos un cliente para añadir el Token de Acceso a todas las llamadas
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${TmdbConfig.ACCESS_TOKEN}")
            .addHeader("accept", "application/json")
            .build()
        chain.proceed(newRequest)
    }.build()

    private val api = Retrofit.Builder()
        .baseUrl(TmdbConfig.BASE_URL)
        .client(client) // Usamos el cliente con el Token
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApiService::class.java)

    suspend fun getPopularMovies(): List<FichaPelicula> {
        return try {
            // Ahora la llamada es más limpia porque el Token va en el 'client'
            val response = api.getPopularMovies()
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}