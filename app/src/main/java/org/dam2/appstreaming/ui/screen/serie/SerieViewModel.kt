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
    private val repositorio = TmdbRepository()

    private val _serieSeleccionada = MutableStateFlow<FichaSerie?>(null)
    val serieSeleccionada: StateFlow<FichaSerie?> = _serieSeleccionada

    private val _todosLosGeneros = MutableStateFlow<List<Genero>>(emptyList())
    val todosLosGeneros: StateFlow<List<Genero>> = _todosLosGeneros

    private val _claveTrailer = MutableStateFlow<String?>(null)
    val claveTrailer: StateFlow<String?> = _claveTrailer

    private val _enlaceVer = MutableStateFlow<String?>(null)
    val enlaceVer: StateFlow<String?> = _enlaceVer

    private val _enlaceDirectoPlataforma = MutableStateFlow<String?>(null)
    val enlaceDirectoPlataforma: StateFlow<String?> = _enlaceDirectoPlataforma

    private val _proveedorPrincipal = MutableStateFlow<Provider?>(null)
    val proveedorPrincipal: StateFlow<Provider?> = _proveedorPrincipal

    private val _reparto = MutableStateFlow<List<CastMember>>(emptyList())
    val reparto: StateFlow<List<CastMember>> = _reparto

    private val _resenas = MutableStateFlow<List<Review>>(emptyList())
    val resenas: StateFlow<List<Review>> = _resenas

    private val _recomendaciones = MutableStateFlow<List<FichaSerie>>(emptyList())
    val recomendaciones: StateFlow<List<FichaSerie>> = _recomendaciones

    private val _palabrasClave = MutableStateFlow<List<Keyword>>(emptyList())
    val palabrasClave: StateFlow<List<Keyword>> = _palabrasClave

    init {
        cargarGeneros()
    }

    fun cargarGeneros() {
        viewModelScope.launch {
            try {
                _todosLosGeneros.value = repositorio.obtenerGenerosTv()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGeneros(generos: List<Genero>) {
        _todosLosGeneros.value = generos
    }

    fun establecerItemSeleccionado(item: FichaSerie) {
        _serieSeleccionada.value = item
        _claveTrailer.value = null
        _enlaceVer.value = null
        _enlaceDirectoPlataforma.value = null
        _proveedorPrincipal.value = null
        _reparto.value = emptyList()
        _resenas.value = emptyList()
        _recomendaciones.value = emptyList()
        _palabrasClave.value = emptyList()
        cargarDetalles(item.id)
    }

    private fun cargarDetalles(idSerie: Int) {
        viewModelScope.launch {
            try {
                _claveTrailer.value = repositorio.obtenerTrailerSerie(idSerie)

                val detalles = repositorio.obtenerDetallesSerie(idSerie)
                detalles?.let {
                    _serieSeleccionada.value = it
                    _enlaceDirectoPlataforma.value = it.homepage
                }

                val infoPlataformas = repositorio.obtenerPlataformasSerie(idSerie)
                _enlaceVer.value = infoPlataformas?.link
                _proveedorPrincipal.value = infoPlataformas?.flatrate?.firstOrNull()

                _reparto.value = repositorio.obtenerRepartoSerie(idSerie)
                _resenas.value = repositorio.obtenerResenasSerie(idSerie)
                _palabrasClave.value = repositorio.obtenerPalabrasClaveSerie(idSerie)
                _recomendaciones.value = repositorio.obtenerRecomendacionesSerie(idSerie)
            } catch (e: Exception) {
                // Si algo falla, la app ya no se cerrará. Puedes loguear el error aquí si quieres.
                e.printStackTrace()
            }
        }
    }
}
