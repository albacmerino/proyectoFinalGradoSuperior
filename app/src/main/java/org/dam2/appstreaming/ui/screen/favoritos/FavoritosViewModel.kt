package org.dam2.appstreaming.ui.screen.favoritos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.RespuestaLista

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = RepositorioBackend(application)

    private val _listaFavoritos = MutableStateFlow<List<RespuestaLista>>(emptyList())
    val listaFavoritos: StateFlow<List<RespuestaLista>> = _listaFavoritos

    private val _estaCargando = MutableStateFlow(false)

    fun cargarFavoritos() {
        viewModelScope.launch {
            _estaCargando.value = true
            // Llamamos al repositorio que ahora lee de ROOM
            val datos = repositorio.obtenerContenidoLista("FAVORITO")
            _listaFavoritos.value = datos
            _estaCargando.value = false
        }
    }

    fun eliminarDeLista(item: RespuestaLista) {
        viewModelScope.launch {

            val exito = repositorio.eliminarDeLista(item.tipoLista, item.idMultimedia)
            if (exito) {
                cargarFavoritos() // Recargamos la lista para que desaparezca de la pantalla
            }
        }
    }
}