package org.dam2.appstreaming.ui.screen.pelicula

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
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.data.repository.RepositorioBackend

/**
 * VIEWMODEL DE DETALLE DE PELÍCULA
 * 
 * Gestiona el estado de la pantalla de detalles de una película.
 * Centraliza la lógica de obtención de datos extendidos (reparto, trailers, recomendaciones)
 * y la interacción con las listas personales del usuario.
 *
 */
class PeliculaViewModel : ViewModel() {
    private val repository = TmdbRepository()
    private var repositorioBackend: RepositorioBackend? = null
    
    /**
     * Inyecta la instancia del repositorio de backend para la gestión de listas.
     */
    fun iniciarRepositorio(repo: RepositorioBackend) {
        this.repositorioBackend = repo
        observarListasDeRoom()
    }

    /**
     * UI State de la pantalla de detalles.
     * Representa de forma atómica lo que la vista necesita renderizar.
     */
    data class PeliculaState(
        val movie: FichaPelicula? = null,
        val allGenres: List<Genero> = emptyList(),
        val trailerKey: String? = null,
        val watchLink: String? = null,
        val directPlatformLink: String? = null,
        val mainProvider: Provider? = null,
        val certification: String? = null,
        val cast: List<CastMember> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val recommendations: List<FichaPelicula> = emptyList(),
        val keywords: List<Keyword> = emptyList(),
        val isLoading: Boolean = false,
        val mostrarSheet: Boolean = false,
        val nombresListas: List<String> = listOf("FAVORITO")
    )

    private val _state = MutableStateFlow(PeliculaState())
    val state: StateFlow<PeliculaState> = _state.asStateFlow()

    /**
     * Establece la película seleccionada e inicia la carga de metadatos.
     */
    fun setSelectedItem(pelicula: FichaPelicula) {
        _state.update {
            it.copy(
                movie = pelicula,
                isLoading = true,
                trailerKey = null,
                watchLink = null,
                directPlatformLink = null,
                mainProvider = null,
                certification = null,
                cast = emptyList(),
                reviews = emptyList(),
                recommendations = emptyList(),
                keywords = emptyList()
            )
        }
        loadDetails(pelicula.id)
    }

    fun setGenres(list: List<Genero>) {
        _state.update { it.copy(allGenres = list) }
    }

    /**
     * Carga información adicional mediante múltiples llamadas.
     * Implementa un manejo de errores básico para evitar que la app se detenga si una petición falla.
     */
    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                // Peticiones paralelas al repositorio de TMDB
                val trailer = repository.obtenerTrailerPelicula(movieId)
                val details = repository.obtenerDetallesPelicula(movieId)
                val watchInfo = try {
                    repository.obtenerPlataformasPelicula(movieId)
                } catch (_: Exception) {
                    null
                }
                val cert = repository.obtenerCertificacionPelicula(movieId)
                val reparto = repository.obtenerRepartoPelicula(movieId)
                val resenas = repository.obtenerResenasPelicula(movieId)
                val recomend = repository.obtenerRecomendacionesPelicula(movieId)
                val palabras = repository.obtenerPalabrasClavePelicula(movieId)

                _state.update { currentState ->
                    currentState.copy(
                        movie = details ?: currentState.movie,
                        trailerKey = trailer,
                        directPlatformLink = details?.enlaceWeb,
                        watchLink = watchInfo?.link,
                        mainProvider = watchInfo?.flatrate?.firstOrNull(),
                        certification = cert,
                        cast = reparto,
                        reviews = resenas,
                        recommendations = recomend,
                        keywords = palabras,
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
     * Observa los nombres de las listas disponibles en Room.
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
     * Guarda el contenido actual en una de las listas del usuario.
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
