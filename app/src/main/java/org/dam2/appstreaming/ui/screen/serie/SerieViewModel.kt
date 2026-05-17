package org.dam2.appstreaming.ui.screen.serie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.TmdbRepository
import org.dam2.appstreaming.data.remote.dto.tmdb.CastMember
import org.dam2.appstreaming.data.remote.dto.tmdb.Provider
import org.dam2.appstreaming.data.remote.dto.tmdb.Review
import org.dam2.appstreaming.data.remote.dto.tmdb.Keyword
import org.dam2.appstreaming.data.remote.dto.backend.SolicitudLista
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.data.repository.RepositorioBackend

/**
 * VIEWMODEL DE DETALLE DE SERIE
 *
 * Gestiona el estado y la lógica de negocio para la pantalla de información detallada de una serie.
 * Al igual que películas, coordina múltiples fuentes de datos de TMDB.
 *
 */
class SerieViewModel : ViewModel() {
    private val repository = TmdbRepository()
    private var repositorioBackend: RepositorioBackend? = null

    /**
     * Inyección manual de dependencias para el repositorio de persistencia local.
     */
    fun iniciarRepositorio(repo: RepositorioBackend) {
        this.repositorioBackend = repo
        observarListasDeRoom()
    }

    /**
     * UI State que encapsula toda la información necesaria para renderizar la pantalla de series.
     */
    data class SerieState(
        val serie: FichaSerie? = null,
        val allGenres: List<Genero> = emptyList(),
        val trailerKey: String? = null,
        val watchLink: String? = null,
        val directPlatformLink: String? = null,
        val mainProvider: Provider? = null,
        val cast: List<CastMember> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val recommendations: List<FichaSerie> = emptyList(),
        val keywords: List<Keyword> = emptyList(),
        val isLoading: Boolean = false,
        val mostrarSheet: Boolean = false,
        val nombresListas: List<String> = listOf("FAVORITO")
    )

    private val _state = MutableStateFlow(SerieState())
    val state: StateFlow<SerieState> = _state.asStateFlow()

    init {
        cargarGeneros()
    }

    /**
     * Obtiene la lista de géneros de TV para la traducción de IDs en la UI.
     */
    private fun cargarGeneros() {
        viewModelScope.launch {
            try {
                val generos = repository.obtenerGenerosTv()
                _state.update { it.copy(allGenres = generos) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGeneros(generos: List<Genero>) {
        _state.update {
            it.copy(allGenres = generos)
        }
    }

    /**
     * Inicializa el estado con la serie seleccionada y dispara la carga de detalles.
     */
    fun establecerItemSeleccionado(item: FichaSerie) {
        _state.update { 
            it.copy(
                serie = item,
                isLoading = true,
                trailerKey = null,
                watchLink = null,
                directPlatformLink = null,
                mainProvider = null,
                cast = emptyList(),
                reviews = emptyList(),
                recommendations = emptyList(),
                keywords = emptyList()
            ) 
        }
        cargarDetalles(item.id)
    }

    /**
     * Realiza las llamadas a la API para completar la ficha de la serie (reparto, trailer, etc.).
     */
    private fun cargarDetalles(idSerie: Int) {
        viewModelScope.launch {
            try {
                val trailer = repository.obtenerTrailerSerie(idSerie)
                val detalles = repository.obtenerDetallesSerie(idSerie)
                val infoPlataformas = try {
                    repository.obtenerPlataformasSerie(idSerie)
                } catch (_: Exception) {
                    null
                }
                val reparto = repository.obtenerRepartoSerie(idSerie)
                val resenas = repository.obtenerResenasSerie(idSerie)
                val palabras = repository.obtenerPalabrasClaveSerie(idSerie)
                val recomend = repository.obtenerRecomendacionesSerie(idSerie)

                _state.update { currentState ->
                    currentState.copy(
                        serie = detalles ?: currentState.serie,
                        trailerKey = trailer,
                        directPlatformLink = detalles?.enlaceWeb,
                        watchLink = infoPlataformas?.link,
                        mainProvider = infoPlataformas?.flatrate?.firstOrNull(),
                        cast = reparto,
                        reviews = resenas,
                        keywords = palabras,
                        recommendations = recomend,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    /**
     * Sincronización con los nombres de las listas del usuario en Room.
     */
    private fun observarListasDeRoom() {
        viewModelScope.launch {
            repositorioBackend?.obtenerNombresDeListas()?.collect { listaDeNombres ->
                _state.update { it.copy(nombresListas = listaDeNombres) }
            }
        }
    }

    fun abrirSheet() {
        _state.update { it.copy(mostrarSheet = true) }
    }

    fun cerrarSheet() {
        _state.update { it.copy(mostrarSheet = false) }
    }

    /**
     * Persiste la serie en una lista personalizada del usuario.
     */
    fun guardarEnLista(id: Int, titulo: String, rutaPoster: String?, esPelicula: Boolean, nombreLista: String) {
        viewModelScope.launch {
            val solicitud = SolicitudLista(
                idMultimedia = id,
                titulo = titulo,
                rutaPoster = rutaPoster,
                esPelicula = esPelicula,
                nombreUsuario = "",
                tipoLista = nombreLista
            )
            val exito = repositorioBackend?.agregarALista(solicitud) ?: false
            if (exito) {
                _state.update { it.copy(mostrarSheet = false) }
            }
        }
    }
}
