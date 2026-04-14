package org.dam2.appstreaming.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.TmdbRepository
import org.dam2.appstreaming.data.repository.RepositorioBackend // Importamos tu repo de Room
import org.dam2.appstreaming.data.model.*
import org.dam2.appstreaming.data.remote.dto.SolicitudLista

/**
 * ViewModel que gestiona la logica de la pantalla de inicio.
 * Cambiamos a AndroidViewModel para tener acceso a 'application' para el RepositorioBackend.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Necesitamos los DOS repositorios
    private val tmdbRepo = TmdbRepository()
    private val backendRepo = RepositorioBackend(application)

    data class HomeState(
        val pestana: Int = 0,
        val peliculasEstreno: List<FichaPelicula> = emptyList(),
        val peliculasPopulares: List<FichaPelicula> = emptyList(),
        val peliculasMejorValoradas: List<FichaPelicula> = emptyList(),
        val seriesEstreno: List<FichaSerie> = emptyList(),
        val seriesPopulares: List<FichaSerie> = emptyList(),
        val seriesMejorValoradas: List<FichaSerie> = emptyList(),
        val generosPelicula: List<Genero> = emptyList(),
        val generosTv: List<Genero> = emptyList(),
        val idGeneroSeleccionado: Int? = null,
        val cargando: Boolean = false
    ) {
        val generosActuales get() = if (pestana == 0) generosPelicula else generosTv
    }

    // 2. Observamos los IDs de favoritos desde Room en tiempo real
    val idsFavoritos: StateFlow<List<Int>> = backendRepo.obtenerIdsFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _estado = MutableStateFlow(HomeState())
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val gPeliculas = tmdbRepo.obtenerGenerosPelicula()
                val gTv = tmdbRepo.obtenerGenerosTv()
                _estado.update {
                    it.copy(generosPelicula = gPeliculas, generosTv = gTv)
                }
                cargarDatos(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cargarDatos(idGenero: Int?) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            try {
                if (idGenero == null) {
                    // CARGA NORMAL: Cada categoría con su llamada correcta a TMDB
                    _estado.update {
                        it.copy(
                            peliculasEstreno = tmdbRepo.obtenerPeliculasEnCine(),
                            peliculasPopulares = tmdbRepo.obtenerPeliculasPopulares(),
                            peliculasMejorValoradas = tmdbRepo.obtenerPeliculasMejorValoradas(),
                            seriesEstreno = tmdbRepo.obtenerSeriesEnEmision(),
                            seriesPopulares = tmdbRepo.obtenerSeriesPopulares(),
                            seriesMejorValoradas = tmdbRepo.obtenerSeriesMejorValoradas()
                        )
                    }
                } else {
                    // FILTRADO POR GÉNERO:
                    if (_estado.value.pestana == 0) {
                        val filtradas = tmdbRepo.descubrirPeliculasPorGenero(idGenero)
                        _estado.update {
                            it.copy(
                                peliculasEstreno = filtradas,
                                peliculasPopulares = emptyList(), // Vaciamos para que no se repitan
                                peliculasMejorValoradas = emptyList()
                            )
                        }
                    } else {
                        val filtradas = tmdbRepo.descubrirSeriesPorGenero(idGenero)
                        _estado.update {
                            it.copy(
                                seriesEstreno = filtradas,
                                seriesPopulares = emptyList(),
                                seriesMejorValoradas = emptyList()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _estado.update { it.copy(cargando = false) }
            }
        }
    }

    fun alCambiarPestana(indice: Int) {
        _estado.update {
            it.copy(pestana = indice, idGeneroSeleccionado = null)
        }
        cargarDatos(null)
    }

    fun alSeleccionarGenero(id: Int?) {
        _estado.update { it.copy(idGeneroSeleccionado = id) }
        cargarDatos(id)
    }

    // 3. Función para añadir o quitar de favoritos (Room)
    fun toggleFavorito(pelicula: FichaPelicula) {
        viewModelScope.launch {
            val solicitud = SolicitudLista(
                idMultimedia = pelicula.id,
                titulo = pelicula.titulo,
                rutaPoster = pelicula.rutaPoster,
                esPelicula = true,
                nombreUsuario = "",
                tipoLista = "FAVORITO"
            )
            // Llamamos al repo de Backend/Room
            backendRepo.agregarALista(solicitud)
        }
    }

    // También para series
    fun toggleFavoritoSerie(serie: FichaSerie) {
        viewModelScope.launch {
            val solicitud = SolicitudLista(
                idMultimedia = serie.id,
                titulo = serie.titulo,
                rutaPoster = serie.rutaPoster,
                esPelicula = false,
                nombreUsuario = "",
                tipoLista = "FAVORITO"
            )
            backendRepo.agregarALista(solicitud)
        }
    }
}