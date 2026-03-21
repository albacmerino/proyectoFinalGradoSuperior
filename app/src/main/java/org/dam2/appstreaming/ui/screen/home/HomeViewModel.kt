package org.dam2.appstreaming.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.TmdbRepository
import org.dam2.appstreaming.data.model.*

/**
 * ViewModel que gestiona la logica de la pantalla de inicio.
 * Utiliza un patron de estado unico (HomeState) para simplificar la UI.
 */
class HomeViewModel : ViewModel() {
    private val repo = TmdbRepository()

    data class HomeState(
        val pestana: Int = 0, // 0 para Cine, 1 para TV
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

    private val _estado = MutableStateFlow(HomeState())
    val estado = _estado.asStateFlow()

    init {
        // Carga inicial de generos y datos generales
        viewModelScope.launch {
            try {
                val gPeliculas = repo.obtenerGenerosPelicula()
                val gTv = repo.obtenerGenerosTv()
                _estado.update {
                    it.copy(generosPelicula = gPeliculas, generosTv = gTv) }
                cargarDatos(null)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    /**
     * Carga los datos dependiendo de si hay un genero seleccionado o no.
     */
    private fun cargarDatos(idGenero: Int?) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            try {
                if (idGenero == null) {
                    // Modo "Todo": Carga las listas generales
                    _estado.update { it.copy(
                        peliculasEstreno = repo.obtenerPeliculasEnCine(),
                        peliculasPopulares = repo.obtenerPeliculasPopulares(),
                        peliculasMejorValoradas = repo.obtenerPeliculasMejorValoradas(),
                        seriesEstreno = repo.obtenerSeriesEnEmision(),
                        seriesPopulares = repo.obtenerSeriesPopulares(),
                        seriesMejorValoradas = repo.obtenerSeriesMejorValoradas()
                    )}
                } else {
                    // Modo "Genero": Carga contenido especifico usando discover
                    if (_estado.value.pestana == 0) {
                        val peliculasFiltradas = repo.descubrirPeliculasPorGenero(idGenero)
                        _estado.update { it.copy(
                            peliculasEstreno = peliculasFiltradas,
                            peliculasPopulares = peliculasFiltradas,
                            peliculasMejorValoradas = peliculasFiltradas
                        )}
                    } else {
                        val seriesFiltradas = repo.descubrirSeriesPorGenero(idGenero)
                        _estado.update { it.copy(
                            seriesEstreno = seriesFiltradas,
                            seriesPopulares = seriesFiltradas,
                            seriesMejorValoradas = seriesFiltradas
                        )}
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
        _estado.update { it.copy(pestana = indice, idGeneroSeleccionado = null) }
        cargarDatos(null)
    }

    fun alSeleccionarGenero(id: Int?) {
        _estado.update { it.copy(idGeneroSeleccionado = id) }
        cargarDatos(id)
    }
}
