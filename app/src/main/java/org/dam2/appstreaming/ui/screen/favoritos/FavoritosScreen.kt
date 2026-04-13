package org.dam2.appstreaming.ui.screen.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// Asegúrate de importar tu DTO aquí. Si no lo tienes en la carpeta 'data', créalo.

import org.dam2.appstreaming.data.remote.dto.RespuestaLista
import org.dam2.appstreaming.ui.component.common.StreamTabs
import org.dam2.appstreaming.ui.component.common.StreamTopBar

@Composable
fun FavoritosScreen(
    listaContenido: List<RespuestaLista>,
    selectedTab: Int, // 0: Pelis, 1: Series
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000814))
    ) {
        // Barra superior con botón de atrás
        StreamTopBar(
            title = "Mis Favoritos",
            onBackClick = onBackClick
        )

        // Pestañas (PELÍCULAS / SERIES)
        StreamTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        // Cuadrícula de 3 columnas
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Filtramos la lista según la pestaña seleccionada
            val listaFiltrada = listaContenido.filter {
                if (selectedTab == 0) it.esPelicula else !it.esPelicula
            }

            items(listaFiltrada) { item ->
                ItemCardFavorito(
                    item = item,
                    onClick = { onItemClick(item.idMultimedia) }
                )
            }
        }
    }
}

@Composable
fun ItemCardFavorito(item: RespuestaLista, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w342${item.rutaPoster}",
            contentDescription = item.titulo,
            modifier = Modifier
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.titulo,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}