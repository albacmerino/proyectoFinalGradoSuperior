package org.dam2.appstreaming.ui.screen.pelicula

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.TmdbRepository
import org.dam2.appstreaming.data.remote.dto.CastMember
import org.dam2.appstreaming.data.remote.dto.Provider
import org.dam2.appstreaming.data.remote.dto.Review
import org.dam2.appstreaming.data.remote.dto.Keyword
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.Genero
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.dam2.appstreaming.data.remote.dto.SolicitudLista
import org.dam2.appstreaming.data.repository.RepositorioBackend

/**
 * ViewModel que gestiona el estado de la pantalla de detalles de una película.
 */
class PeliculaViewModel : ViewModel() {
    private val repository = TmdbRepository()
    private var repositorioBackend: RepositorioBackend? = null
    fun iniciarRepositorio(repo: RepositorioBackend) {
        this.repositorioBackend = repo
        observarListasDeRoom()
    }

    /**
     * Estado unificado para la pantalla de detalle.
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
     * Establece la película seleccionada e inicia la carga de detalles.
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

    /**
     * Establece la lista global de géneros para poder traducir los IDs.
     */
    fun setGenres(list: List<Genero>) {
        _state.update { it.copy(allGenres = list) }
    }

    /**
     * Carga toda la información adicional de la película desde el repositorio.
     */
    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                // Obtenemos el trailer de YouTube
                val trailer = repository.obtenerTrailerPelicula(movieId)

                // Obtenemos detalles extendidos (como la sinopsis completa o homepage)
                val details = repository.obtenerDetallesPelicula(movieId)

                // Obtenemos dónde ver la película (plataformas de streaming)
                val watchInfo = try {
                    repository.obtenerPlataformasPelicula(movieId)
                } catch (_: Exception) {
                    null
                }

                // Obtenemos otros datos: certificación, reparto, reseñas, etc.
                val cert = repository.obtenerCertificacionPelicula(movieId)
                val reparto = repository.obtenerRepartoPelicula(movieId)
                val resenas = repository.obtenerResenasPelicula(movieId)
                val recomend = repository.obtenerRecomendacionesPelicula(movieId)
                val palabras = repository.obtenerPalabrasClavePelicula(movieId)

                // Actualizamos el estado con toda la información cargada
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

    // En PeliculaDetailViewModel.kt
    fun toggleFavorito(pelicula: FichaPelicula) {
        viewModelScope.launch {
            try {
                // El nombre de usuario lo sacamos de Firebase
                // displayName puede ser nulo, así que usamos el email o un fallback
                val user = FirebaseAuth.getInstance().currentUser
                val nombreUser = when {
                    !user?.displayName.isNullOrBlank() -> user?.displayName
                    !user?.email.isNullOrBlank() -> user?.email?.substringBefore("@")
                    else -> user?.uid // Último recurso: el ID largo de Firebase
                } ?: "UsuarioAnonimo"

                val solicitud = SolicitudLista(
                    idMultimedia = pelicula.id,
                    titulo = pelicula.titulo,
                    rutaPoster = pelicula.rutaPoster,
                    esPelicula = true,
                    nombreUsuario = nombreUser,
                    tipoLista = "FAVORITO"
                )

                // 2. Llamamos al repositorio
                val exito = repositorioBackend?.agregarALista(solicitud) ?: false

                if (exito) {
                    Log.d(
                        "PeliculaViewModel",
                        "Guardado en PostgreSQL con éxito: ${pelicula.titulo}"
                    )
                } else {
                    Log.e("PeliculaViewModel", "Error al guardar en el servidor")
                }
            } catch (e: Exception) {
                Log.e("PeliculaViewModel", "Error en la petición de favoritos", e)
            }
        }
    }


    var mostrarSheet by mutableStateOf(false)


    private fun observarListasDeRoom() {
        viewModelScope.launch {
            // Escuchamos el Flow del repositorio (que viene de Room)
            repositorioBackend?.obtenerNombresDeListas()?.collect { listaDeNombres ->
                _state.update { it.copy(nombresListas = listaDeNombres) }
            }
        }
    }

    fun abrirSheet() {
        // Simplemente cambiamos el booleano, observarListasDeRoom() ya mantiene los nombres actualizados
        _state.update { it.copy(mostrarSheet = true) }
    }

    fun cerrarSheet() {
        _state.update { it.copy(mostrarSheet = false) }
    }
    /**
     * Guarda cualquier tipo de contenido (Pelicula o Serie) en una lista de Room.
     */
    fun guardarEnLista(id: Int, titulo: String, rutaPoster: String?, esPelicula: Boolean, nombreLista: String) {
        viewModelScope.launch {
            val solicitud = SolicitudLista(
                idMultimedia = id,
                titulo = titulo,
                rutaPoster = rutaPoster,
                esPelicula = esPelicula,
                nombreUsuario = "", // Gestionado por el repositorio
                tipoLista = nombreLista
            )

            val exito = repositorioBackend?.agregarALista(solicitud) ?: false

            if (exito) {
                _state.update { it.copy(mostrarSheet = false) }
                Log.d("MediaViewModel", "Contenido guardado en: $nombreLista")
            }else {
                Log.e("PeliculaViewModel", "Error al guardar en Room")
            }
        }
    }


}