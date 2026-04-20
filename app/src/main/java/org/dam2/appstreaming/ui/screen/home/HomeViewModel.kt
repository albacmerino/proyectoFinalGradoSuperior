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
    // Mapa para llevar el control de qué página cargar en cada sección
    private val paginasActuales = mutableMapOf<String, Int>()
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
        val cargando: Boolean = false,
        val cargandoMas: Boolean = false
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
            paginasActuales.clear() // Resetear páginas al cambiar de género o pestaña
            try {
                if (idGenero == null) {
                    _estado.update {
                        it.copy(
                            peliculasEstreno = tmdbRepo.obtenerPeliculasEnCine(1),
                            peliculasPopulares = tmdbRepo.obtenerPeliculasPopulares(1),
                            peliculasMejorValoradas = tmdbRepo.obtenerPeliculasMejorValoradas(1),
                            seriesEstreno = tmdbRepo.obtenerSeriesEnEmision(1),
                            seriesPopulares = tmdbRepo.obtenerSeriesPopulares(1),
                            seriesMejorValoradas = tmdbRepo.obtenerSeriesMejorValoradas(1)
                        )
                    }
                } else {
                    val esPeli = _estado.value.pestana == 0
                    if (esPeli) {
                        val filtradas = tmdbRepo.descubrirPeliculasPorGenero(idGenero, 1)
                        _estado.update { it.copy(peliculasEstreno = filtradas, peliculasPopulares = emptyList(), peliculasMejorValoradas = emptyList()) }
                    } else {
                        val filtradas = tmdbRepo.descubrirSeriesPorGenero(idGenero, 1)
                        _estado.update { it.copy(seriesEstreno = filtradas, seriesPopulares = emptyList(), seriesMejorValoradas = emptyList()) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _estado.update { it.copy(cargando = false) }
            }
        }
    }

    fun cargarMasContenido(seccion: String) {
        // Bloqueo de seguridad: No cargamos si ya está cargando, o si hay un género filtrado
        // (TMDB no pagina igual los filtros de género simples, mejor dejarlo para carga normal)
        if (_estado.value.cargando || _estado.value.cargandoMas || _estado.value.idGeneroSeleccionado != null) return

        val proximaPagina = (paginasActuales[seccion] ?: 1) + 1

        viewModelScope.launch {
            _estado.update { it.copy(cargandoMas = true) }
            try {
                when (seccion) {
                    "peliculasEstreno" -> {
                        val nuevas = tmdbRepo.obtenerPeliculasEnCine(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(peliculasEstreno = it.peliculasEstreno + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                    "peliculasPopulares" -> {
                        val nuevas = tmdbRepo.obtenerPeliculasPopulares(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(peliculasPopulares = it.peliculasPopulares + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                    "peliculasMejorValoradas" -> {
                        val nuevas = tmdbRepo.obtenerPeliculasMejorValoradas(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(peliculasMejorValoradas = it.peliculasMejorValoradas + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                    "seriesEstreno" -> {
                        val nuevas = tmdbRepo.obtenerSeriesEnEmision(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(seriesEstreno = it.seriesEstreno + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                    "seriesPopulares" -> {
                        val nuevas = tmdbRepo.obtenerSeriesPopulares(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(seriesPopulares = it.seriesPopulares + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                    "seriesMejorValoradas" -> {
                        val nuevas = tmdbRepo.obtenerSeriesMejorValoradas(proximaPagina)
                        if (nuevas.isNotEmpty()) {
                            _estado.update { it.copy(seriesMejorValoradas = it.seriesMejorValoradas + nuevas) }
                            paginasActuales[seccion] = proximaPagina
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error cargando página $proximaPagina de $seccion", e)
            } finally {
                _estado.update { it.copy(cargandoMas = false) }
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