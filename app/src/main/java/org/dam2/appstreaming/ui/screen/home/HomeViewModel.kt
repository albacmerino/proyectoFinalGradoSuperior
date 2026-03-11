package org.dam2.appstreaming.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.ui.component.FichaPelicula

class HomeViewModel : ViewModel() {

    private val repository = TmdbRepository()

    private val _movies = MutableStateFlow<List<FichaPelicula>>(emptyList())
    val movies: StateFlow<List<FichaPelicula>> = _movies

    init {
        cargarPeliculasReales()
    }

    private fun cargarPeliculasReales() {
        viewModelScope.launch {
            // Llamamos al repositorio para traer datos de internet
            val listaDesdeApi = repository.getPopularMovies()
            _movies.value = listaDesdeApi
        }
    }
}