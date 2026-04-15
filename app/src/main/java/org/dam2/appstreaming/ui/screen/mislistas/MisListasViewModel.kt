package org.dam2.appstreaming.ui.screen.mislistas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import org.dam2.appstreaming.data.remote.dto.RespuestaLista
import org.dam2.appstreaming.data.repository.RepositorioBackend

class MisListasViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = RepositorioBackend(application)

    // El estado de la pantalla será un mapa: "Nombre de Lista" -> "Lista de Pelis/Series"
    val listas = repo.obtenerTodasLasListasAgrupadas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
}