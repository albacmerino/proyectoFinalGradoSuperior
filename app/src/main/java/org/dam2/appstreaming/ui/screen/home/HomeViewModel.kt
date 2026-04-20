package org.dam2.appstreaming.ui.screen.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.data.remote.dto.SolicitudLista
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.repository.TmdbRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tmdb = TmdbRepository()
    private val backend = RepositorioBackend(application)

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
        val generosActuales: List<Genero> = emptyList(),
        val idGeneroSeleccionado: Int? = null,
        // Resultados del filtro por género (lista separada para no mezclar con el catálogo normal)
        val resultadosFiltroGenero: List<Any> = emptyList(),
        // Páginas para catálogo normal
        val paginaEstreno: Int = 1,
        val paginaPopulares: Int = 1,
        val paginaMejorValoradas: Int = 1,
        val paginaSeriesEstreno: Int = 1,
        val paginaSeriesPopulares: Int = 1,
        val paginaSeriesMejorValoradas: Int = 1,
        // Página para el filtro de género (independiente del catálogo normal)
        val paginaFiltroGenero: Int = 1,
        val estaCargandoMasFiltro: Boolean = false
    )

    private val _estado = MutableStateFlow(HomeState())
    val estado: StateFlow<HomeState> = _estado.asStateFlow()

    val idsFavoritos: StateFlow<List<Int>> = backend.obtenerIdsFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        cargarTodo()
    }

    private fun cargarTodo() {
        viewModelScope.launch {
            val generosPeli = tmdb.obtenerGenerosPelicula()
            val generosTv = tmdb.obtenerGenerosTv()
            _estado.update {
                it.copy(
                    generosPelicula = generosPeli,
                    generosTv = generosTv,
                    generosActuales = generosPeli
                )
            }
        }
        viewModelScope.launch {
            val estreno = tmdb.obtenerPeliculasEnCine()
            val populares = tmdb.obtenerPeliculasPopulares()
            val topRated = tmdb.obtenerPeliculasMejorValoradas()
            _estado.update {
                it.copy(
                    peliculasEstreno = estreno,
                    peliculasPopulares = populares,
                    peliculasMejorValoradas = topRated
                )
            }
        }
        viewModelScope.launch {
            val estreno = tmdb.obtenerSeriesEnEmision()
            val populares = tmdb.obtenerSeriesPopulares()
            val topRated = tmdb.obtenerSeriesMejorValoradas()
            _estado.update {
                it.copy(
                    seriesEstreno = estreno,
                    seriesPopulares = populares,
                    seriesMejorValoradas = topRated
                )
            }
        }
    }

    fun alCambiarPestana(nuevaPestana: Int) {
        _estado.update { estado ->
            val generos = if (nuevaPestana == 0) estado.generosPelicula else estado.generosTv
            estado.copy(
                pestana = nuevaPestana,
                generosActuales = generos,
                idGeneroSeleccionado = null,
                resultadosFiltroGenero = emptyList(),
                paginaFiltroGenero = 1
            )
        }
    }

    /**
     * Selecciona un género y carga la primera página de resultados.
     * Si se pulsa el mismo género activo, se limpia el filtro.
     */
    fun alSeleccionarGenero(idGenero: Int?) {
        val esMismoGenero = idGenero == _estado.value.idGeneroSeleccionado

        if (idGenero == null || esMismoGenero) {
            _estado.update {
                it.copy(
                    idGeneroSeleccionado = null,
                    resultadosFiltroGenero = emptyList(),
                    paginaFiltroGenero = 1
                )
            }
            return
        }

        // Nuevo género: reseteamos la lista y cargamos desde la página 1
        _estado.update {
            it.copy(
                idGeneroSeleccionado = idGenero,
                resultadosFiltroGenero = emptyList(),
                paginaFiltroGenero = 1
            )
        }

        cargarPaginaFiltro(idGenero, pagina = 1, acumular = false)
    }

    /**
     * Carga más resultados del género activo (paginación infinita del filtro).
     * Llamado desde la UI cuando el usuario llega al final de la lista filtrada.
     */
    fun cargarMasFiltroGenero() {
        val estado = _estado.value
        val idGenero = estado.idGeneroSeleccionado ?: return

        // Evitamos lanzar varias peticiones simultáneas
        if (estado.estaCargandoMasFiltro) return

        val siguientePagina = estado.paginaFiltroGenero + 1
        cargarPaginaFiltro(idGenero, pagina = siguientePagina, acumular = true)
    }

    /**
     * Petición real a TMDB para obtener una página del filtro.
     *
     * @param acumular si es true, añade los resultados a los existentes (paginación);
     *                 si es false, los reemplaza (primera carga o cambio de género).
     */
    private fun cargarPaginaFiltro(idGenero: Int, pagina: Int, acumular: Boolean) {
        viewModelScope.launch {
            _estado.update { it.copy(estaCargandoMasFiltro = true) }

            if (_estado.value.pestana == 0) {
                val nuevas = tmdb.descubrirPeliculasPorGenero(idGenero, pagina)
                _estado.update { estado ->
                    val listaActualizada = if (acumular) {
                        @Suppress("UNCHECKED_CAST")
                        (estado.resultadosFiltroGenero as List<FichaPelicula>) + nuevas
                    } else {
                        nuevas
                    }
                    estado.copy(
                        resultadosFiltroGenero = listaActualizada,
                        paginaFiltroGenero = pagina,
                        estaCargandoMasFiltro = false
                    )
                }
            } else {
                val nuevas = tmdb.descubrirSeriesPorGenero(idGenero, pagina)
                _estado.update { estado ->
                    val listaActualizada = if (acumular) {
                        @Suppress("UNCHECKED_CAST")
                        (estado.resultadosFiltroGenero as List<FichaSerie>) + nuevas
                    } else {
                        nuevas
                    }
                    estado.copy(
                        resultadosFiltroGenero = listaActualizada,
                        paginaFiltroGenero = pagina,
                        estaCargandoMasFiltro = false
                    )
                }
            }
        }
    }

    // Paginación del catálogo normal (sin cambios respecto a la versión anterior)
    fun cargarMasContenido(seccion: String) {
        viewModelScope.launch {
            val estado = _estado.value
            when (seccion) {
                "peliculasEstreno" -> {
                    val p = estado.paginaEstreno + 1
                    val nuevas = tmdb.obtenerPeliculasEnCine(p)
                    _estado.update { it.copy(peliculasEstreno = it.peliculasEstreno + nuevas, paginaEstreno = p) }
                }
                "peliculasPopulares" -> {
                    val p = estado.paginaPopulares + 1
                    val nuevas = tmdb.obtenerPeliculasPopulares(p)
                    _estado.update { it.copy(peliculasPopulares = it.peliculasPopulares + nuevas, paginaPopulares = p) }
                }
                "peliculasMejorValoradas" -> {
                    val p = estado.paginaMejorValoradas + 1
                    val nuevas = tmdb.obtenerPeliculasMejorValoradas(p)
                    _estado.update { it.copy(peliculasMejorValoradas = it.peliculasMejorValoradas + nuevas, paginaMejorValoradas = p) }
                }
                "seriesEstreno" -> {
                    val p = estado.paginaSeriesEstreno + 1
                    val nuevas = tmdb.obtenerSeriesEnEmision(p)
                    _estado.update { it.copy(seriesEstreno = it.seriesEstreno + nuevas, paginaSeriesEstreno = p) }
                }
                "seriesPopulares" -> {
                    val p = estado.paginaSeriesPopulares + 1
                    val nuevas = tmdb.obtenerSeriesPopulares(p)
                    _estado.update { it.copy(seriesPopulares = it.seriesPopulares + nuevas, paginaSeriesPopulares = p) }
                }
                "seriesMejorValoradas" -> {
                    val p = estado.paginaSeriesMejorValoradas + 1
                    val nuevas = tmdb.obtenerSeriesMejorValoradas(p)
                    _estado.update { it.copy(seriesMejorValoradas = it.seriesMejorValoradas + nuevas, paginaSeriesMejorValoradas = p) }
                }
            }
        }
    }

    // Favoritos
    fun toggleFavorito(pelicula: FichaPelicula) {
        viewModelScope.launch {
            val solicitud = buildSolicitudFavorito(pelicula.id, pelicula.titulo, pelicula.rutaPoster, true)
            val exito = backend.agregarALista(solicitud)
            if (!exito) Log.e("HomeViewModel", "Error al actualizar favorito: ${pelicula.titulo}")
        }
    }

    fun toggleFavoritoSerie(serie: FichaSerie) {
        viewModelScope.launch {
            val solicitud = buildSolicitudFavorito(serie.id, serie.titulo, serie.rutaPoster, false)
            val exito = backend.agregarALista(solicitud)
            if (!exito) Log.e("HomeViewModel", "Error al actualizar favorito serie: ${serie.titulo}")
        }
    }

    private fun buildSolicitudFavorito(id: Int, titulo: String, poster: String?, esPelicula: Boolean): SolicitudLista {
        val user = FirebaseAuth.getInstance().currentUser
        val nombreUsuario = user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")
            ?: user?.uid
            ?: "anonimo"
        return SolicitudLista(
            idMultimedia = id,
            titulo = titulo,
            rutaPoster = poster,
            esPelicula = esPelicula,
            nombreUsuario = nombreUsuario,
            tipoLista = "FAVORITO"
        )
    }
}