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
import org.dam2.appstreaming.data.remote.dto.ResultadoBusqueda
import org.dam2.appstreaming.data.repository.TmdbRepository

/**
 * ViewModel de la pantalla de búsqueda.
 *
 * Usa `debounce` para no lanzar una petición a la API en cada tecla que
 * pulse el usuario — espera 400ms de inactividad antes de buscar.
 * Esto reduce el número de llamadas y evita resultados parpadeantes.
 */
@OptIn(FlowPreview::class)
class BusquedaViewModel : ViewModel() {

    private val repository = TmdbRepository()

    // -------------------------------------------------------------------------
    // Estado
    // -------------------------------------------------------------------------

    data class BusquedaState(
        val consulta: String = "",
        val resultados: List<ResultadoBusqueda> = emptyList(),
        val estaCargando: Boolean = false,
        val sinResultados: Boolean = false,   // true solo si buscamos y no encontramos nada
        val error: String? = null
    )

    private val _estado = MutableStateFlow(BusquedaState())
    val estado: StateFlow<BusquedaState> = _estado.asStateFlow()

    // Flow interno que escucha los cambios de texto del campo de búsqueda
    private val _consulta = MutableStateFlow("")

    init {
        // Observamos el texto y lanzamos la búsqueda tras 400ms sin cambios
        viewModelScope.launch {
            _consulta
                .debounce(400L)
                .distinctUntilChanged()
                .filter { it.length >= 2 }  // No buscamos con menos de 2 caracteres
                .collect { texto ->
                    buscar(texto)
                }
        }
    }

    // -------------------------------------------------------------------------
    // Acciones
    // -------------------------------------------------------------------------

    /**
     * Llamado cada vez que el usuario escribe en el campo de búsqueda.
     * Actualiza el texto visible y dispara el debounce.
     */
    fun alEscribir(texto: String) {
        _estado.update { it.copy(consulta = texto) }
        _consulta.value = texto

        // Si borramos todo el texto, limpiamos los resultados inmediatamente
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

    /** Limpia la búsqueda actual y vuelve al estado inicial. */
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

    // -------------------------------------------------------------------------
    // Lógica interna
    // -------------------------------------------------------------------------

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