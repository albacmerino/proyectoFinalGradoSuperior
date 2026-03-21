package org.dam2.appstreaming.ui.screen.serie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.data.remote.dto.CastMember
import org.dam2.appstreaming.data.remote.dto.Provider
import org.dam2.appstreaming.data.remote.dto.Review
import org.dam2.appstreaming.data.remote.dto.Keyword
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero

/**
 * ViewModel que gestiona el estado de la pantalla de detalles de una serie.
 */
class SerieViewModel : ViewModel() {
    private val repository = TmdbRepository()

    /**
     * Estado unificado para la pantalla de detalle de serie.
     */
    data class SerieState(
        val serie: FichaSerie? = null,
        val allGenres: List<Genero> = emptyList(),
        val trailerKey: String? = null,
        val watchLink: String? = null,
        val directPlatformLink: String? = null,
        val mainProvider: Provider? = null,
        val cast: List<CastMember> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val recommendations: List<FichaSerie> = emptyList(),
        val keywords: List<Keyword> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(SerieState())
    val state: StateFlow<SerieState> = _state.asStateFlow()

    init {
        cargarGeneros()
    }

    private fun cargarGeneros() {
        viewModelScope.launch {
            try {
                val generos = repository.obtenerGenerosTv()
                _state.update { it.copy(allGenres = generos) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGeneros(generos: List<Genero>) {
        _state.update {
            it.copy(allGenres = generos)
        }
    }

    /**
     * Establece la serie seleccionada e inicia la carga de detalles.
     */
    fun establecerItemSeleccionado(item: FichaSerie) {
        _state.update { 
            it.copy(
                serie = item,
                isLoading = true,
                trailerKey = null,
                watchLink = null,
                directPlatformLink = null,
                mainProvider = null,
                cast = emptyList(),
                reviews = emptyList(),
                recommendations = emptyList(),
                keywords = emptyList()
            ) 
        }
        cargarDetalles(item.id)
    }

    /**
     * Carga toda la información adicional de la serie desde el repositorio.
     */
    private fun cargarDetalles(idSerie: Int) {
        viewModelScope.launch {
            try {
                val trailer = repository.obtenerTrailerSerie(idSerie)
                val detalles = repository.obtenerDetallesSerie(idSerie)
                val infoPlataformas = try {
                    repository.obtenerPlataformasSerie(idSerie)
                } catch (_: Exception) {
                    null
                }
                val reparto = repository.obtenerRepartoSerie(idSerie)
                val resenas = repository.obtenerResenasSerie(idSerie)
                val palabras = repository.obtenerPalabrasClaveSerie(idSerie)
                val recomend = repository.obtenerRecomendacionesSerie(idSerie)

                _state.update { currentState ->
                    currentState.copy(
                        serie = detalles ?: currentState.serie,
                        trailerKey = trailer,
                        directPlatformLink = detalles?.enlaceWeb,
                        watchLink = infoPlataformas?.link,
                        mainProvider = infoPlataformas?.flatrate?.firstOrNull(),
                        cast = reparto,
                        reviews = resenas,
                        keywords = palabras,
                        recommendations = recomend,
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
