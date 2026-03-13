package org.dam2.appstreaming.ui.screen.pelicula

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.appstreaming.ui.component.FichaPelicula

class PeliculaViewModel : ViewModel() {
    private val _selectedPelicula = MutableStateFlow<FichaPelicula?>(null)
    val selectedPelicula: StateFlow<FichaPelicula?> = _selectedPelicula

    fun setSelectedItem(pelicula: FichaPelicula) {
        _selectedPelicula.value = pelicula
    }
}