package org.dam2.appstreaming.ui.screen.pelicula

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero

class PeliculaViewModel : ViewModel() {
    private val _selectedPelicula = MutableStateFlow<FichaPelicula?>(null)
    val selectedPelicula: StateFlow<FichaPelicula?> = _selectedPelicula
    private val _allGenres = MutableStateFlow<List<Genero>>(emptyList())
    val allGenres: StateFlow<List<Genero>> = _allGenres
    fun setSelectedItem(pelicula: FichaPelicula) {
        _selectedPelicula.value = pelicula
    }
    fun setGenres(list: List<Genero>) {
        _allGenres.value = list
    }
}