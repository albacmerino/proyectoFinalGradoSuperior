package org.dam2.appstreaming.ui.screen.serie

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
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.Genero

class SerieViewModel : ViewModel() {
    private val repository = TmdbRepository()

    private val _selectedSerie = MutableStateFlow<FichaSerie?>(null)
    val selectedSerie: StateFlow<FichaSerie?> = _selectedSerie

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

    private val _reparto = MutableStateFlow<List<CastMember>>(emptyList())
    val reparto: StateFlow<List<CastMember>> = _reparto

    private val _resenas = MutableStateFlow<List<Review>>(emptyList())
    val resenas: StateFlow<List<Review>> = _resenas

    private val _recomendaciones = MutableStateFlow<List<FichaSerie>>(emptyList())
    val recomendaciones: StateFlow<List<FichaSerie>> = _recomendaciones

    private val _palabrasClave = MutableStateFlow<List<Keyword>>(emptyList())
    val palabrasClave: StateFlow<List<Keyword>> = _palabrasClave

    fun setSelectedItem(item: FichaSerie) {
        _selectedSerie.value = item
        _trailerKey.value = null
        _watchLink.value = null
        _directPlatformLink.value = null
        _mainProvider.value = null
        _reparto.value = emptyList()
        _resenas.value = emptyList()
        _recomendaciones.value = emptyList()
        _palabrasClave.value = emptyList()
        loadDetails(item.id)
    }

    fun setGenres(list: List<Genero>) {
        _allGenres.value = list
    }

    private fun loadDetails(seriesId: Int) {
        viewModelScope.launch {
            try {
                _trailerKey.value = repository.getSeriesTrailer(seriesId)

                val details = repository.getSeriesDetails(seriesId)
                details?.let {
                    _selectedSerie.value = it
                    _directPlatformLink.value = it.homepage
                }

                val watchInfo = repository.getSeriesWatchProvidersData(seriesId)
                _watchLink.value = watchInfo?.link
                _mainProvider.value = watchInfo?.flatrate?.firstOrNull()

                _reparto.value = repository.getSeriesCast(seriesId)
                _resenas.value = repository.getSeriesReviews(seriesId)
                _recomendaciones.value = repository.getSeriesRecommendations(seriesId)
                _palabrasClave.value = repository.getSeriesKeywords(seriesId)
            } catch (e: Exception) {
                // Si algo falla, la app ya no se cerrará. Puedes loguear el error aquí si quieres.
                e.printStackTrace()
            }
        }
    }
}