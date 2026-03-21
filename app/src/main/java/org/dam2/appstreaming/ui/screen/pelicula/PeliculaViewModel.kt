package org.dam2.appstreaming.ui.screen.pelicula

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Provider
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.data.network.Keyword
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero

/**
 * ViewModel que gestiona el estado de la pantalla de detalles de una película.
 */
class PeliculaViewModel : ViewModel() {
    private val repository = TmdbRepository()

    /**
     * Estado unificado para la pantalla de detalle.
     */
    data class PeliculaState(
        val movie: FichaPelicula? = null,
        val allGenres: List<Genero> = emptyList(),
        val trailerKey: String? = null,
        val watchLink: String? = null,
        val directPlatformLink: String? = null,
        val mainProvider: Provider? = null,
        val certification: String? = null,
        val cast: List<CastMember> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val recommendations: List<FichaPelicula> = emptyList(),
        val keywords: List<Keyword> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(PeliculaState())
    val state: StateFlow<PeliculaState> = _state.asStateFlow()

    /**
     * Establece la película seleccionada e inicia la carga de detalles.
     */
    fun setSelectedItem(pelicula: FichaPelicula) {
        _state.update { 
            it.copy(
                movie = pelicula,
                isLoading = true,
                trailerKey = null,
                watchLink = null,
                directPlatformLink = null,
                mainProvider = null,
                certification = null,
                cast = emptyList(),
                reviews = emptyList(),
                recommendations = emptyList(),
                keywords = emptyList()
            ) 
        }
        loadDetails(pelicula.id)
    }

    /**
     * Establece la lista global de géneros para poder traducir los IDs.
     */
    fun setGenres(list: List<Genero>) {
        _state.update { it.copy(allGenres = list) }
    }

    /**
     * Carga toda la información adicional de la película desde el repositorio.
     */
    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                // Obtenemos el trailer de YouTube
                val trailer = repository.obtenerTrailerPelicula(movieId)
                
                // Obtenemos detalles extendidos (como la sinopsis completa o homepage)
                val details = repository.obtenerDetallesPelicula(movieId)
                
                // Obtenemos dónde ver la película (plataformas de streaming)
                val watchInfo = try { repository.obtenerPlataformasPelicula(movieId) } catch (e: Exception) { null }
                
                // Obtenemos otros datos: certificación, reparto, reseñas, etc.
                val cert = repository.obtenerCertificacionPelicula(movieId)
                val reparto = repository.obtenerRepartoPelicula(movieId)
                val resenas = repository.obtenerResenasPelicula(movieId)
                val recomend = repository.obtenerRecomendacionesPelicula(movieId)
                val palabras = repository.obtenerPalabrasClavePelicula(movieId)

                // Actualizamos el estado con toda la información cargada
                _state.update { currentState ->
                    currentState.copy(
                        movie = details ?: currentState.movie,
                        trailerKey = trailer,
                        directPlatformLink = details?.enlaceWeb,
                        watchLink = watchInfo?.link,
                        mainProvider = watchInfo?.flatrate?.firstOrNull(),
                        certification = cert,
                        cast = reparto,
                        reviews = resenas,
                        recommendations = recomend,
                        keywords = palabras,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }
}
