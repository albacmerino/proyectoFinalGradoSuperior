package org.dam2.appstreaming.ui.screen.favoritos

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.remote.dto.RespuestaLista
import org.dam2.appstreaming.ui.component.common.StreamTabs
import org.dam2.appstreaming.ui.component.common.StreamTopBar
import org.dam2.appstreaming.ui.component.common.HomeDrawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    listaContenido: List<RespuestaLista>,
    onItemClick: (RespuestaLista) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMisListas: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Filtramos la lista según la pestaña (0: Películas, 1: Series)
    val listaFiltrada = remember(listaContenido, selectedTab) {
        listaContenido.filter { if (selectedTab == 0) it.esPelicula else !it.esPelicula }
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
                onPerfilClick = { scope.launch { drawerState.close() } },
                onFavoritosClick = { scope.launch { drawerState.close() } },
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
            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("No tienes nada guardado aún", color = Color.Gray)
                }
            } else {
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
                        CardFavorito(item, onItemClick)
                    }
                }
            }
        }
    }
}


@Composable
private fun CardFavorito(
    item: RespuestaLista,
    onClick: (RespuestaLista) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(2f / 3f) // Proporción estándar de póster
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(item) }
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w342${item.rutaPoster}",
            contentDescription = item.titulo,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}