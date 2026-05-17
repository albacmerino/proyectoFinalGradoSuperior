package org.dam2.appstreaming.ui.screen.mislistas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista

/**
 * VIEWMODEL DE "MIS LISTAS"
 * 
 * Gestiona el estado de las colecciones personalizadas del usuario. 
 * Esta clase demuestra el uso intensivo de la programación reactiva para mantener la UI sincronizada.
 * 
 * Conceptos TFG:
 * - Cold Flow to StateFlow: Convierte el flujo de la base de datos en un estado de UI observable.
 * - SharingStarted.WhileSubscribed: Optimización de recursos, el flujo solo está activo si hay suscriptores (la pantalla es visible).
 * - Agrupación de datos: Maneja la lógica de transformar una lista plana en un mapa agrupado por categorías.
 */
class MisListasViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = RepositorioBackend(application)

    // Observa todas las listas de forma reactiva desde Room
    val listas = repo.obtenerTodasLasListasAgrupadas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    /**
     * Lógica para eliminar contenido de una lista específica.
     * Al ser reactivo, no hace falta llamar a 'refresh', el Flow 'listas' detecta el cambio en Room automáticamente.
     */
    fun eliminarDeLista(item: RespuestaLista) {
        viewModelScope.launch {
            repo.eliminarDeLista(item.tipoLista, item.idMultimedia)
        }
    }
}
