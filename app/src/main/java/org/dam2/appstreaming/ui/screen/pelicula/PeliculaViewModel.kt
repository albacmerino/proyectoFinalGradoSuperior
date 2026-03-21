package org.dam2.appstreaming.ui.screen.pelicula

<<<<<<< Updated upstream
class PeliculaViewModel {
=======
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.TmdbRepository
import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Provider
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero


/* ----- CEREBRO QUE CORDINA LAS PETICIONES ----- */
class PeliculaViewModel : ViewModel() {
    private val repository = TmdbRepository()

    private val _selectedPelicula = MutableStateFlow<FichaPelicula?>(null)
    val selectedPelicula: MutableStateFlow<FichaPelicula?> = _selectedPelicula

    private val _allGenres = MutableStateFlow<List<Genero>>(emptyList())
    val allGenres: StateFlow<List<Genero>> = _allGenres

    // StateFlow ->
    private val _trailerKey = MutableStateFlow<String?>(null)
    val trailerKey: StateFlow<String?> = _trailerKey

    private val _watchLink = MutableStateFlow<String?>(null)
    val watchLink: StateFlow<String?> = _watchLink

    private val _directPlatformLink = MutableStateFlow<String?>(null)
    val directPlatformLink: StateFlow<String?> = _directPlatformLink

    private val _mainProvider = MutableStateFlow<Provider?>(null)
    val mainProvider: StateFlow<Provider?> = _mainProvider

    private val _certification = MutableStateFlow<String?>(null)
    val certification: StateFlow<String?> = _certification

    private val _cast = MutableStateFlow<List<CastMember>>(emptyList())
    val cast: StateFlow<List<CastMember>> = _cast

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun setSelectedItem(pelicula: FichaPelicula) {
        _selectedPelicula.value = pelicula
        _trailerKey.value = null
        _watchLink.value = null
        _directPlatformLink.value = null
        _mainProvider.value = null
        _certification.value = null
        _cast.value = emptyList()
        _reviews.value = emptyList()
        loadDetails(pelicula.id)
    }

    fun setGenres(list: List<Genero>) {
        _allGenres.value = list
    }

    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            // 1. Cargar trailer
            _trailerKey.value = repository.getMovieTrailer(movieId)
            
            // 2. Cargar detalles extendidos (para el homepage/link directo)
            val details = repository.getMovieDetails(movieId)
            details?.let {
                _selectedPelicula.value = it
                _directPlatformLink.value = it.homepage
            }

            // 3. Cargar proveedores (para el logo y el link de respaldo)
            try {
                val watchInfo = repository.getMovieWatchProvidersData(movieId)
                _watchLink.value = watchInfo?.link
                _mainProvider.value = watchInfo?.flatrate?.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. Cargar certificación PEGI
            _certification.value = repository.getMovieCertification(movieId)

            // 5. Cargar reparto
            _cast.value = repository.getMovieCast(movieId)

            // 6. Cargar reseñas (Social)
            _reviews.value = repository.getMovieReviews(movieId)
        }
    }
>>>>>>> Stashed changes
}