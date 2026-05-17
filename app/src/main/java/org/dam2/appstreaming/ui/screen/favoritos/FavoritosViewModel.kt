package org.dam2.appstreaming.ui.screen.favoritos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista

/**
 * FAVORITOS
 * 
 * Clase encargada de gestionar la lógica de negocio para la pantalla de marcadores/favoritos.
 * Interactúa directamente con el RepositorioBackend para consultar y modificar los datos persistidos en Room.
 *
 */
class FavoritosViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = RepositorioBackend(application)

    // Contiene la lista de elementos favoritos
    private val _listaFavoritos = MutableStateFlow<List<RespuestaLista>>(emptyList())
    val listaFavoritos: StateFlow<List<RespuestaLista>> = _listaFavoritos

    private val _estaCargando = MutableStateFlow(false)

    /**
     * Recupera la lista de favoritos del usuario desde la BBDD local.
     */
    fun cargarFavoritos() {
        viewModelScope.launch {
            _estaCargando.value = true
            // Consulta al repositorio (acceso a Room)
            val datos = repositorio.obtenerContenidoLista("FAVORITO")
            _listaFavoritos.value = datos
            _estaCargando.value = false
        }
    }

    /**
     * Elimina un elemento de la lista de favoritos.
     * Tras el borrado, refresca la lista para sincronizar la UI.
     */
    fun eliminarDeLista(item: RespuestaLista) {
        viewModelScope.launch {
            val exito = repositorio.eliminarDeLista(item.tipoLista, item.idMultimedia)
            if (exito) {
                // Sincronización inmediata de la interfaz
                cargarFavoritos()
            }
        }
    }
}
