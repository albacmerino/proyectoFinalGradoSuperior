package org.dam2.appstreaming.ui.screen.busqueda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.remote.dto.tmdb.ResultadoBusqueda
import org.dam2.appstreaming.data.repository.TmdbRepository

/**
 * VIEWMODEL DE BÚSQUEDA
 *
 * Implementa una lógica de búsqueda optimizada para reducir la carga en el servidor y mejorar la UX.
 *
 */
@OptIn(FlowPreview::class)
class BusquedaViewModel : ViewModel() {

    private val repository = TmdbRepository()

    /**
     * Estado de la pantalla de búsqueda.
     */
    data class BusquedaState(
        val consulta: String = "",
        val resultados: List<ResultadoBusqueda> = emptyList(),
        val estaCargando: Boolean = false,
        val sinResultados: Boolean = false,   // true solo si buscamos y no encontramos nada
        val error: String? = null
    )

    private val _estado = MutableStateFlow(BusquedaState())
    val estado: StateFlow<BusquedaState> = _estado.asStateFlow()

    private val _consulta = MutableStateFlow("")

    init {
        // Observamos el texto y lanzamos la búsqueda tras 400ms sin cambios
        viewModelScope.launch {
            _consulta
                .debounce(400L)
                .distinctUntilChanged()
                .filter {
                    it.length >= 2
                }  // No buscamos con menos de 2 caracteres
                .collect { texto ->
                    buscar(texto)
                }
        }
    }

    /**
     * Actualiza la consulta de búsqueda
     */
    fun alEscribir(texto: String) {
        _estado.update { it.copy(consulta = texto) }
        _consulta.value = texto

        // Si borramos el texto, limpiamos los resultados inmediatamente
        if (texto.isBlank()) {
            _estado.update {
                it.copy(
                    resultados = emptyList(),
                    sinResultados = false,
                    error = null
                )
            }
        }
    }

    fun limpiarBusqueda() {
        _consulta.value = ""
        _estado.update {
            it.copy(
                consulta = "",
                resultados = emptyList(),
                sinResultados = false,
                error = null
            )
        }
    }

    /**
     * Realiza la llamada asíncrona al repositorio para obtener los resultados.
     */
    private fun buscar(consulta: String) {
        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true, error = null) }

            val resultados = repository.buscar(consulta)

            _estado.update {
                it.copy(
                    resultados = resultados,
                    estaCargando = false,
                    sinResultados = resultados.isEmpty()
                )
            }
        }
    }
}
