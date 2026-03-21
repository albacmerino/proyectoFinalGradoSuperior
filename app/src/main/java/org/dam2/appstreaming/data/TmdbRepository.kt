package org.dam2.appstreaming.data

import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.data.network.TmdbApiService
import org.dam2.appstreaming.data.network.WatchCountryInfo
import org.dam2.appstreaming.data.network.Keyword
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.Genero
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbConfig {
    const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNWQ4OWI4NmUyMzcxZWFjZWQ3YTNkNjJkYjRkMjg2YyIsIm5iZiI6MTc2OTY0MzE1NS4xNSwic3ViIjoiNjk3YTljOTMwODNhZWExMzBiYjE2OGFiIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.AJH7nc1wL_3zjMu_JLjgqV_ryGdxT_8d1MdrIb7TR5A"
    const val BASE_URL = "https://api.themoviedb.org/3/"
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

    // --- PELÍCULAS ---
    suspend fun getPopularMovies(): List<FichaPelicula> = try {
        api.getPopularMovies().listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun getNowPlayingMovies(): List<FichaPelicula> = try {
        api.getNowPlayingMovies().listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun getTopRatedMovies(): List<FichaPelicula> = try {
        api.getTopRatedMovies().listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getMovieDetails(movieId: Int): FichaPelicula? = try {
        api.getMovieDetails(movieId)
    } catch (_: Exception) {
        null
    }

    suspend fun getMovieTrailer(movieId: Int): String? {
        return try {
            val response = api.getMovieVideos(movieId)
            val trailer = response.listaVideos.find {
                it.site == "YouTube" && it.type == "Trailer"
            } ?: response.listaVideos.find {
                    it.site == "YouTube"
            }
            trailer?.key
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieWatchProvidersData(movieId: Int): WatchCountryInfo? {
        return try {
            val response = api.getMovieWatchProviders(movieId)
            response.results["ES"] ?: response.results.values.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieCertification(movieId: Int): String? {
        return try {
            val response = api.getMovieReleaseDates(movieId)
            val spainResults = response.results.find {
                it.iso31661 == "ES"
            }
            val certification = spainResults?.releaseDates?.find {
                it.certification.isNotEmpty()
            }?.certification
                ?: response.results.flatMap {
                    it.releaseDates
                }.find {
                    it.certification.isNotEmpty()
                }?.certification
            certification
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieCast(movieId: Int): List<CastMember> = try {
        api.getMovieCredits(movieId).cast
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getMovieReviews(movieId: Int): List<Review> = try {
        api.getMovieReviews(movieId).results
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getMovieRecommendations(movieId: Int): List<FichaPelicula> = try {
        api.getMovieRecommendations(movieId).listaPeliculas
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getMovieKeywords(movieId: Int): List<Keyword> = try {
        api.getMovieKeywords(movieId).keywords ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    // --- SERIES ---
    suspend fun getPopularSeries(): List<FichaSerie> = try {
        api.getPopularSeries().listaSeries
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun getTopRatedSeries(): List<FichaSerie> = try {
        api.getTopRatedSeries().listaSeries
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun getOnTheAirSeries(): List<FichaSerie> = try {
        api.getOnTheAirSeries().listaSeries
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getSeriesTrailer(seriesId: Int): String? {
        return try {
            val response = api.getSeriesVideos(seriesId)
            val trailer = response.listaVideos.find {
                it.site == "YouTube" && it.type == "Trailer"
            }
                ?: response.listaVideos.find {
                    it.site == "YouTube"
                }
            trailer?.key
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getSeriesWatchProvidersData(seriesId: Int): WatchCountryInfo? {
        return try {
            val response = api.getSeriesWatchProviders(seriesId)
            response.results["ES"] ?: response.results.values.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getSeriesCast(seriesId: Int): List<CastMember> = try {
        api.getSeriesCredits(seriesId).cast
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getSeriesReviews(seriesId: Int): List<Review> = try {
        api.getSeriesReviews(seriesId).results
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getSeriesRecommendations(seriesId: Int): List<FichaSerie> = try {
        api.getSeriesRecommendations(seriesId).listaSeries
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getSeriesKeywords(seriesId: Int): List<Keyword> = try {
        api.getSeriesKeywords(seriesId).results ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun getSeriesDetails(serieId: Int): FichaSerie? = try {
        api.getSeriesDetails(serieId)
    } catch (_: Exception) {
        null
    }


    // --- GÉNEROS ---
    suspend fun getMovieGenres(): List<Genero> = try {
        api.getMovieGenres().genres
    } catch (_: Exception) {
        emptyList()
    }
    suspend fun getTvGenres(): List<Genero> = try {
        api.getTvGenres().genres
    } catch (_: Exception) {
        emptyList()
    }
}
