package org.dam2.appstreaming.ui.screen.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.mapper.toFichaPelicula
import org.dam2.appstreaming.data.mapper.toFichaSerie
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

/**
 * BÚSQUEDA
 * 
 * Actúa como el host de Android para la funcionalidad de búsqueda global. 
 * Se encarga de coordinar la transición entre los resultados de búsqueda y las pantallas de detalle.
 *
 */
class BusquedaFragment : Fragment() {

    private val viewModel: BusquedaViewModel by viewModels()
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val estado by viewModel.estado.collectAsState()

                MaterialTheme {
                    BusquedaScreen(
                        estado = estado,
                        onTextoChange = {
                            viewModel.alEscribir(it)
                                        },
                        onLimpiar = {
                            viewModel.limpiarBusqueda()
                                    },
                        onBackClick = {
                            findNavController().popBackStack()
                                      },
                        onPeliculaClick = { resultado ->
                            // Transformación de DTO de búsqueda a Modelo de Película
                            peliculaViewModel.setSelectedItem(resultado.toFichaPelicula())
                            findNavController().navigate(
                                R.id.action_busquedaFragment_to_peliculaDetailFragment
                            )
                        },
                        onSerieClick = { resultado ->
                            // Transformación de DTO de búsqueda a Modelo de Serie
                            serieViewModel.establecerItemSeleccionado(resultado.toFichaSerie())
                            findNavController().navigate(
                                R.id.action_busquedaFragment_to_serieDetailFragment
                            )
                        }
                    )
                }
            }
        }
    }
}
