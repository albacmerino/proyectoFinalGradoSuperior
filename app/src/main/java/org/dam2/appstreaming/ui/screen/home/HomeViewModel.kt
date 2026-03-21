package org.dam2.appstreaming.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.ui.component.*

/**
 * ViewModel que gestiona la lógica de la pantalla de inicio.
 * Utiliza un patrón de estado único (HomeState) para simplificar la UI.
 */
class HomeViewModel : ViewModel() {
    private val repo = TmdbRepository()

    data class HomeState(
        val tab: Int = 0, // 0 para Cine, 1 para TV
        val moviesNow: List<FichaPelicula> = emptyList(),
        val moviesPop: List<FichaPelicula> = emptyList(),
        val moviesTop: List<FichaPelicula> = emptyList(),
        val seriesNow: List<FichaSerie> = emptyList(),
        val seriesPop: List<FichaSerie> = emptyList(),
        val seriesTop: List<FichaSerie> = emptyList(),
        val movieGenres: List<Genero> = emptyList(),
        val tvGenres: List<Genero> = emptyList(),
        val selectedGenreId: Int? = null,
        val isLoading: Boolean = false
    ) {
        val currentGenres get() = if (tab == 0) movieGenres else tvGenres
    }

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        // Carga inicial de géneros y datos generales
        viewModelScope.launch {
            try {
                val mGenres = repo.obtenerGenerosPelicula()
                val tGenres = repo.obtenerGenerosTv()
                _state.update { it.copy(movieGenres = mGenres, tvGenres = tGenres) }
                loadData(null)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    /**
     * Carga los datos dependiendo de si hay un género seleccionado o no.
     * Si hay un género, utiliza el servicio 'discover' para obtener muchos más resultados reales de ese género.
     */
    private fun loadData(genreId: Int?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (genreId == null) {
                    // Modo "Todo": Carga las listas generales
                    _state.update { it.copy(
                        moviesNow = repo.obtenerPeliculasEnCine(),
                        moviesPop = repo.obtenerPeliculasPopulares(),
                        moviesTop = repo.obtenerPeliculasMejorValoradas(),
                        seriesNow = repo.obtenerSeriesEnCine(),
                        seriesPop = repo.obtenerSeriesPopulares(),
                        seriesTop = repo.obtenerSeriesMejorValoradas()
                    )}
                } else {
                    // Modo "Género": Carga contenido específico de ese género usando discover
                    if (_state.value.tab == 0) {
                        val filteredMovies = repo.descubrirPeliculasPorGenero(genreId)
                        _state.update { it.copy(
                            moviesNow = filteredMovies,
                            moviesPop = filteredMovies,
                            moviesTop = filteredMovies
                        )}
                    } else {
                        val filteredSeries = repo.descubrirSeriesPorGenero(genreId)
                        _state.update { it.copy(
                            seriesNow = filteredSeries,
                            seriesPop = filteredSeries,
                            seriesTop = filteredSeries
                        )}
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTab(i: Int) {
        _state.update { it.copy(tab = i, selectedGenreId = null) }
        loadData(null)
    }

    fun onGenre(id: Int?) {
        _state.update { it.copy(selectedGenreId = id) }
        loadData(id)
    }
}
