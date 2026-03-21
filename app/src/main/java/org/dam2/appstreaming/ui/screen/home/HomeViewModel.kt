package org.dam2.appstreaming.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.Genero

class HomeViewModel : ViewModel() {


    val selectedGenreId = MutableStateFlow<Int?>(null) // null significa "Todo"

    private val repository = TmdbRepository()

    // Control de la pestaña seleccionada (0: Películas, 1: Series)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    // --- ESTADOS DE PELÍCULAS ---
    // Mantenemos 'movies' como la lista principal (Novedades)
    private val _movies = MutableStateFlow<List<FichaPelicula>>(emptyList())
    val movies: StateFlow<List<FichaPelicula>> = _movies

    private val _popularMovies = MutableStateFlow<List<FichaPelicula>>(emptyList())
    val popularMovies: StateFlow<List<FichaPelicula>> = _popularMovies

    private val _topRatedMovies = MutableStateFlow<List<FichaPelicula>>(emptyList())
    val topRatedMovies: StateFlow<List<FichaPelicula>> = _topRatedMovies

    // --- ESTADOS DE SERIES ---
    // Mantenemos 'series' como la lista principal (Novedades)
    private val _series = MutableStateFlow<List<FichaSerie>>(emptyList())
    val series: StateFlow<List<FichaSerie>> = _series

    private val _popularSeries = MutableStateFlow<List<FichaSerie>>(emptyList())
    val popularSeries: StateFlow<List<FichaSerie>> = _popularSeries

    private val _topRatedSeries = MutableStateFlow<List<FichaSerie>>(emptyList())
    val topRatedSeries: StateFlow<List<FichaSerie>> = _topRatedSeries

    val movieGenres = MutableStateFlow<List<Genero>>(emptyList())
    val tvGenres = MutableStateFlow<List<Genero>>(emptyList())

    init {
        cargarTodo()
    }

    private fun cargarTodo() {
        viewModelScope.launch {
            try {
                // Carga de Películas (Cine)
                _movies.value = repository.obtenerPeliculasEnCine() // Novedades
                _popularMovies.value = repository.obtenerPeliculasPopulares()
                _topRatedMovies.value = repository.obtenerPeliculasMejorValoradas()

                // Carga de Series (TV)
                _series.value = repository.obtenerSeriesEnCine() // Novedades
                _popularSeries.value = repository.obtenerSeriesPopulares()
                _topRatedSeries.value = repository.obtenerSeriesMejorValoradas()

                //Cargar Generos
                movieGenres.value = repository.obtenerGenerosPelicula()
                tvGenres.value = repository.obtenerGenerosTv()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }


    fun onGeneroSelected(id: Int?) {
        selectedGenreId.value = id
        // Aquí podrías filtrar las listas llamando a la API con discover/movie?with_genres=id
    }
}