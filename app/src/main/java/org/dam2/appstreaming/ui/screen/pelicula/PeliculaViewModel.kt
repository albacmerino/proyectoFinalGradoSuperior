package org.dam2.appstreaming.ui.screen.pelicula

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Provider
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.data.network.Keyword
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero

class PeliculaViewModel : ViewModel() {
    private val repository = TmdbRepository()

    private val _selectedPelicula = MutableStateFlow<FichaPelicula?>(null)
    val selectedPelicula: StateFlow<FichaPelicula?> = _selectedPelicula

    private val _allGenres = MutableStateFlow<List<Genero>>(emptyList())
    val allGenres: StateFlow<List<Genero>> = _allGenres

    private val _trailerKey = MutableStateFlow<String?>(null)
    val trailerKey: StateFlow<String?> = _trailerKey

    private val _watchLink = MutableStateFlow<String?>(null)
    val watchLink: StateFlow<String?> = _watchLink

    private val _directPlatformLink = MutableStateFlow<String?>(null)
    val directPlatformLink: StateFlow<String?> = _directPlatformLink

    private val _mainProvider = MutableStateFlow<Provider?>(null)
    val mainProvider: StateFlow<Provider?> = _mainProvider

    private val _certificacion = MutableStateFlow<String?>(null)
    val certificacion: StateFlow<String?> = _certificacion

    private val _reparto = MutableStateFlow<List<CastMember>>(emptyList())
    val reparto: StateFlow<List<CastMember>> = _reparto

    private val _resenas = MutableStateFlow<List<Review>>(emptyList())
    val resenas: StateFlow<List<Review>> = _resenas

    private val _recomendaciones = MutableStateFlow<List<FichaPelicula>>(emptyList())
    val recomendaciones: StateFlow<List<FichaPelicula>> = _recomendaciones

    private val _palabrasClave = MutableStateFlow<List<Keyword>>(emptyList())
    val palabrasClave: StateFlow<List<Keyword>> = _palabrasClave

    fun setSelectedItem(pelicula: FichaPelicula) {
        _selectedPelicula.value = pelicula
        _trailerKey.value = null
        _watchLink.value = null
        _directPlatformLink.value = null
        _mainProvider.value = null
        _certificacion.value = null
        _reparto.value = emptyList()
        _resenas.value = emptyList()
        _recomendaciones.value = emptyList()
        _palabrasClave.value = emptyList()
        loadDetails(pelicula.id)
    }

    fun setGenres(list: List<Genero>) {
        _allGenres.value = list
    }

    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            _trailerKey.value = repository.getMovieTrailer(movieId)
            
            val details = repository.getMovieDetails(movieId)
            details?.let {
                _selectedPelicula.value = it
                _directPlatformLink.value = it.homepage
            }

            try {
                val watchInfo = repository.getMovieWatchProvidersData(movieId)
                _watchLink.value = watchInfo?.link
                _mainProvider.value = watchInfo?.flatrate?.firstOrNull()
            } catch (_: Exception) { }

            _certificacion.value = repository.getMovieCertification(movieId)
            _reparto.value = repository.getMovieCast(movieId)
            _resenas.value = repository.getMovieReviews(movieId)
            _recomendaciones.value = repository.getMovieRecommendations(movieId)
            _palabrasClave.value = repository.getMovieKeywords(movieId)
        }
    }
}