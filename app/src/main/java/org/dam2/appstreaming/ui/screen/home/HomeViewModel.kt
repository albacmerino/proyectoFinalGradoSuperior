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
    // Repositorio para obtener los datos de la API de TMDB
    private val repo = TmdbRepository()

    /**
     * Representa todo el estado de la pantalla en un solo objeto.
     */
    data class HomeState(
        val tab: Int = 0, // 0 para Cine, 1 para TV
        val moviesNow: List<FichaPelicula> = emptyList(), // Estrenos de cine
        val moviesPop: List<FichaPelicula> = emptyList(), // Películas populares
        val moviesTop: List<FichaPelicula> = emptyList(), // Películas mejor valoradas
        val seriesNow: List<FichaSerie> = emptyList(),   // Estrenos de TV
        val seriesPop: List<FichaSerie> = emptyList(),   // Series populares
        val seriesTop: List<FichaSerie> = emptyList(),   // Series mejor valoradas
        val movieGenres: List<Genero> = emptyList(),     // Lista de géneros para cine
        val tvGenres: List<Genero> = emptyList(),        // Lista de géneros para TV
        val selectedGenreId: Int? = null                 // ID del género seleccionado (null = todos)
    ) {
        // Propiedad calculada que devuelve los géneros correspondientes a la pestaña activa
        val currentGenres get() = if (tab == 0) movieGenres else tvGenres
    }

    // Flujo de estado privado (mutable) y público (solo lectura)
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        // Al inicializar el ViewModel, cargamos todos los datos necesarios en una corrutina
        viewModelScope.launch {
            try {
                // Actualizamos el estado con la información recibida del repositorio
                _state.update { it.copy(
                    moviesNow = repo.obtenerPeliculasEnCine(),
                    moviesPop = repo.obtenerPeliculasPopulares(),
                    moviesTop = repo.obtenerPeliculasMejorValoradas(),
                    seriesNow = repo.obtenerSeriesEnCine(),
                    seriesPop = repo.obtenerSeriesPopulares(),
                    seriesTop = repo.obtenerSeriesMejorValoradas(),
                    movieGenres = repo.obtenerGenerosPelicula(),
                    tvGenres = repo.obtenerGenerosTv()
                )}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Cambia la pestaña actual entre Películas (0) y Series (1).
     */
    fun onTab(i: Int) = _state.update { it.copy(tab = i) }

    /**
     * Actualiza el filtro de género seleccionado.
     */
    fun onGenre(id: Int?) = _state.update { it.copy(selectedGenreId = id) }
}
