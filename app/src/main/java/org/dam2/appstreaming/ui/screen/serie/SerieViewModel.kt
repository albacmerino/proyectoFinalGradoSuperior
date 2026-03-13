package org.dam2.appstreaming.ui.screen.serie

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie

class SerieViewModel : ViewModel() {
    // Estado para la película o serie seleccionada
    private val _selectedItem = MutableStateFlow<FichaSerie?>(null)
    val selectedItem: StateFlow<FichaSerie?> = _selectedItem

    fun setSelectedItem(item: FichaSerie) {
        _selectedItem.value = item
    }
}