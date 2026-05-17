package org.dam2.appstreaming.ui.screen.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.dam2.appstreaming.ui.component.common.StreamTabs
import org.dam2.appstreaming.ui.component.common.StreamTopBar
import org.dam2.appstreaming.ui.component.common.HomeDrawer
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista

/**
 * FAVORITOS
 * 
 * Interfaz encargada de mostrar la colección de películas y series guardadas por el usuario.
 * Implementa un diseño de cuadrícula (Grid) para optimizar el espacio en pantalla.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    listaContenido: List<RespuestaLista>,
    onItemClick: (RespuestaLista) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMisListas: () -> Unit,
    onDeleteClick: (RespuestaLista) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Se recalcula automáticamente si cambia la lista o la pestaña seleccionada
    val listaFiltrada = remember(listaContenido, selectedTab) {
        listaContenido.filter {
            if (selectedTab == 0) it.esPelicula else !it.esPelicula
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer(
                onHomeClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToHome()
                    }
                },
                onFavoritosClick = {
                    scope.launch { drawerState.close() }
                                   },
                onMisListasClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToMisListas()
                    }
                },
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        onLogoutClick()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    StreamTopBar(
                        title = "Mis Favoritos",
                        showBackButton = false,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                    StreamTabs(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            // Gestión de estados vacíos
            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes nada guardado aún", color = Color.Gray)
                }
            } else {
                // Cuadrícula de 3 columnas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaFiltrada) { item ->
                        CardFavorito(item, onItemClick, onDelete = { onDeleteClick(item) })
                    }
                }
            }
        }
    }
}

/**
 * TARJETA DE FAVORITO
 * Versión simplificada de la Card optimizada para visualización en cuadrícula.
 */
@Composable
private fun CardFavorito(
    item: RespuestaLista,
    onClick: (RespuestaLista) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick(item)
            }
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w342${item.rutaPoster}",
            contentDescription = item.titulo,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Botón de borrado rápido
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
