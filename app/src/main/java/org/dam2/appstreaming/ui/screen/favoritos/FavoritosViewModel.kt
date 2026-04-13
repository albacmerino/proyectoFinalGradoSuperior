package org.dam2.appstreaming.ui.screen.favoritos

import android.app.Application
import androidx.activity.result.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.appstreaming.data.remote.dto.RespuestaLista
import org.dam2.appstreaming.data.repository.RepositorioBackend
import kotlinx.coroutines.launch

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = RepositorioBackend(application)

    private val _listaFavoritos = MutableStateFlow<List<RespuestaLista>>(emptyList())
    val listaFavoritos: StateFlow<List<RespuestaLista>> = _listaFavoritos

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarFavoritos(nombreUsuario: String) {
        viewModelScope.launch {
            _cargando.value = true
            // Llamamos al repositorio pidiendo específicamente la lista "FAVORITO"
            val lista = repositorio.obtenerContenidoLista(nombreUsuario, "FAVORITO")
            _listaFavoritos.value = lista
            _cargando.value = false
        }
    }
}