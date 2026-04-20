package org.dam2.appstreaming.data.repository

import org.dam2.appstreaming.data.remote.dto.*
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.data.remote.api.TmdbApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbConfig {
    const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNWQ4OWI4NmUyMzcxZWFjZWQ3YTNkNjJkYjRkMjg2YyIsIm5iZiI6MTc2OTY0MzE1NS4xNSwic3ViIjoiNjk3YTljOTMwODNhZWExMzBiYjE2OGFiIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.AJH7nc1wL_3zjMu_JLjgqV_ryGdxT_8d1MdrIb7TR5A"
    const val BASE_URL = "https://api.themoviedb.org/3/"
}

class TmdbRepository {

    private val cliente = OkHttpClient.Builder().addInterceptor { chain ->
        val nuevaRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${TmdbConfig.ACCESS_TOKEN}")
            .addHeader("accept", "application/json")
            .build()
        chain.proceed(nuevaRequest)
    }.build()

    private val api = Retrofit.Builder()
        .baseUrl(TmdbConfig.BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApiService::class.java)

    // --- PELICULAS ---
    suspend fun obtenerPeliculasPopulares(page: Int = 1): List<FichaPelicula> = try {
        api.getPopularMovies(page = page).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun obtenerPeliculasEnCine(page: Int = 1): List<FichaPelicula> = try {
        api.getNowPlayingMovies(page = page).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun obtenerPeliculasMejorValoradas(page: Int = 1): List<FichaPelicula> = try {
        api.getTopRatedMovies(page = page).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerDetallesPelicula(idPelicula: Int): FichaPelicula? = try {
        api.getMovieDetails(idPelicula)
    } catch (_: Exception) {
        null
    }

    suspend fun obtenerTrailerPelicula(idPelicula: Int): String? {
        return try {
            val respuesta = api.getMovieVideos(idPelicula)
            val trailer = respuesta.listaVideos.find {
                it.site == "YouTube" && it.type == "Trailer"
            } ?: respuesta.listaVideos.find {
                    it.site == "YouTube"
            }
            trailer?.key
        } catch (_: Exception) {
            null
        }
    }

    suspend fun obtenerPlataformasPelicula(idPelicula: Int): WatchCountryInfo? {
        return try {
            val respuesta = api.getMovieWatchProviders(idPelicula)
            respuesta.results["ES"] ?: respuesta.results.values.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun obtenerCertificacionPelicula(idPelicula: Int): String? {
        return try {
            val respuesta = api.getMovieReleaseDates(idPelicula)
            val resultadosEspania = respuesta.results.find {
                it.iso31661 == "ES"
            }
            val certificacion = resultadosEspania?.releaseDates?.find {
                it.certification.isNotEmpty()
            }?.certification
                ?: respuesta.results.flatMap {
                    it.releaseDates
                }.find {
                    it.certification.isNotEmpty()
                }?.certification
            certificacion
        } catch (_: Exception) {
            null
        }
    }

    suspend fun obtenerRepartoPelicula(idPelicula: Int): List<CastMember> = try {
        api.getMovieCredits(idPelicula).cast
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerResenasPelicula(idPelicula: Int): List<Review> = try {
        api.getMovieReviews(idPelicula).results
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerRecomendacionesPelicula(idPelicula: Int, page: Int = 1): List<FichaPelicula> = try {
        api.getMovieRecommendations(idPelicula, page = page).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerPalabrasClavePelicula(idPelicula: Int): List<Keyword> = try {
        api.getMovieKeywords(idPelicula).keywords ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun descubrirPeliculasPorGenero(idGenero: Int, page: Int = 1): List<FichaPelicula> = try {
        api.discoverMovies(idGenero, page = page).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }

    // --- SERIES ---
    suspend fun obtenerSeriesPopulares(page: Int = 1): List<FichaSerie> = try {
        api.getPopularSeries(page = page).listaSeries
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerSeriesEnEmision(page: Int = 1): List<FichaSerie> = try {
        api.getOnTheAirSeries(page = page).listaSeries
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun obtenerSeriesMejorValoradas(page: Int = 1): List<FichaSerie> = try {
        api.getTopRatedSeries(page = page).listaSeries
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerDetallesSerie(idSerie: Int, page: Int = 1): FichaSerie? = try {
        api.getSeriesDetails(idSerie, page = page)
    } catch (_: Exception) {
        null
    }

    suspend fun obtenerTrailerSerie(idSerie: Int): String? {
        return try {
            val respuesta = api.getSeriesVideos(idSerie)
            val trailer = respuesta.listaVideos.find {
                it.site == "YouTube" && it.type == "Trailer"
            }
                ?: respuesta.listaVideos.find {
                    it.site == "YouTube"
                }
            trailer?.key
        } catch (_: Exception) {
            null
        }
    }

    suspend fun obtenerPlataformasSerie(idSerie: Int): WatchCountryInfo? {
        return try {
            val respuesta = api.getSeriesWatchProviders(idSerie)
            respuesta.results["ES"] ?: respuesta.results.values.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun obtenerRepartoSerie(idSerie: Int): List<CastMember> = try {
        api.getSeriesCredits(idSerie).cast
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerResenasSerie(idSerie: Int): List<Review> = try {
        api.getSeriesReviews(idSerie).results
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerRecomendacionesSerie(idSerie: Int, page: Int = 1): List<FichaSerie> = try {
        api.getSeriesRecommendations(idSerie, page = page).listaSeries
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun obtenerPalabrasClaveSerie(idSerie: Int): List<Keyword> = try {
        api.getSeriesKeywords(idSerie).results ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun descubrirSeriesPorGenero(idGenero: Int, page: Int = 1): List<FichaSerie> = try {
        api.discoverSeries(idGenero, page = page).listaSeries
    } catch (_: Exception) {
        emptyList()
    }


    // --- GENEROS ---
    suspend fun obtenerGenerosPelicula(): List<Genero> = try {
        api.getMovieGenres().genres
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun obtenerGenerosTv(): List<Genero> = try {
        api.getTvGenres().genres
    } catch (_: Exception) {
        emptyList()
    }

    // Dentro de TmdbRepository.kt

    /**
     * Realiza una búsqueda global (películas y series) usando el endpoint Multi-Search.
     * Filtra los resultados para ignorar personas (sólo queremos "movie" y "tv").
     */
    suspend fun buscar(consulta: String): List<ResultadoBusqueda> {
        return try {
            val respuesta = api.buscarMulti(consulta)
            // Filtramos para que sólo devuelva películas o series, ignorando actores/personas
            respuesta.results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
        } catch (e: Exception) {
            android.util.Log.e("TmdbRepository", "Error al buscar: ${e.message}")
            emptyList()
        }
    }
}
