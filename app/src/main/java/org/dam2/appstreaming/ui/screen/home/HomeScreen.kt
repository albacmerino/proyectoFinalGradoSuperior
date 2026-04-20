package org.dam2.appstreaming.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.*
import org.dam2.appstreaming.data.model.*
import org.dam2.appstreaming.ui.component.common.HomeDrawer
import org.dam2.appstreaming.ui.component.common.StreamTopBar
import org.dam2.appstreaming.ui.component.common.StreamTabs
import org.dam2.appstreaming.ui.component.common.GenreSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    moviesNow: List<FichaPelicula>,
    moviesPop: List<FichaPelicula>,
    moviesTop: List<FichaPelicula>,
    seriesNow: List<FichaSerie>,
    seriesPop: List<FichaSerie>,
    seriesTop: List<FichaSerie>,
    generos: List<Genero>,
    selectedGenreId: Int?,
    onGeneroClick: (Int?) -> Unit,
    onMovieClick: (FichaPelicula) -> Unit,
    onSerieClick: (FichaSerie) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    idsFavoritos: List<Int>,
    onToggleFavorite: (Any) -> Unit,
    onNavigateToMisListas: () -> Unit,
    onLoadMore: (String) -> Unit // Cambiado a String para saber qué sección cargar
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer(
                onHomeClick = { scope.launch { drawerState.close() } },
                onPerfilClick = { scope.launch { drawerState.close() } },
                onFavoritosClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToFavoritos()
                    }
                },
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        onLogoutClick()
                    }
                },
                onMisListasClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToMisListas()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                StreamTopBar(
                    title = "SeaStream",
                    showBackButton = false,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                StreamTabs(selectedTab, onTabSelected)
                GenreSelector(generos, selectedGenreId, onGeneroClick)

                MainContent(
                    selectedTab, moviesNow, moviesPop, moviesTop,
                    seriesNow, seriesPop, seriesTop, generos,
                    selectedGenreId,
                    onMovieClick, onSerieClick,
                    idsFavoritos, onToggleFavorite,
                    onLoadMore = onLoadMore // Pasamos la función hacia abajo
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    selectedTab: Int,
    moviesNow: List<FichaPelicula>,
    moviesPop: List<FichaPelicula>,
    moviesTop: List<FichaPelicula>,
    seriesNow: List<FichaSerie>,
    seriesPop: List<FichaSerie>,
    seriesTop: List<FichaSerie>,
    generos: List<Genero>,
    selectedGenreId: Int?,
    onMovieClick: (FichaPelicula) -> Unit,
    onSerieClick: (FichaSerie) -> Unit,
    idsFavoritos: List<Int>,
    onToggleFavorite: (Any) -> Unit,
    onLoadMore: (String) -> Unit // Recibimos la función
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 16.dp)) {

        if (selectedGenreId != null) {
            val nombreGenero = generos.find { it.id == selectedGenreId }?.name ?: "Filtrados"
            item {
                HomeSection(
                    title = "Resultados: $nombreGenero",
                    items = if (selectedTab == 0) moviesNow else seriesNow,
                    allGenres = generos,
                    onClick = {
                        if (it is FichaPelicula) onMovieClick(it)
                        else if (it is FichaSerie) onSerieClick(it)
                    },
                    isMovie = selectedTab == 0,
                    idsFavoritos = idsFavoritos,
                    onToggleFavorite = onToggleFavorite,
                    onLoadMore = {}
                )
            }
        } else {
            if (selectedTab == 0) {
                item { HomeSection("Novedades",
                    moviesNow,
                    generos,
                    onMovieClick,
                    true, idsFavoritos,
                    onToggleFavorite,
                    { onLoadMore("peliculasEstreno") }) }
                item { HomeSection("Populares",
                    moviesPop, generos,
                    onMovieClick,
                    true, idsFavoritos, onToggleFavorite,
                    { onLoadMore("peliculasPopulares") }) }
                item { HomeSection("Mejor valoradas",
                    moviesTop,
                    generos,
                    onMovieClick,
                    true, idsFavoritos,
                    onToggleFavorite,
                    { onLoadMore("peliculasMejorValoradas") }) }
            } else {
                item { HomeSection("Novedades TV",
                    seriesNow,
                    generos,
                    onSerieClick,
                    false,
                    idsFavoritos,
                    onToggleFavorite,
                    { onLoadMore("seriesEstreno") }) }
                item { HomeSection("Populares en TV",
                    seriesPop,
                    generos,
                    onSerieClick,
                    false,
                    idsFavoritos,
                    onToggleFavorite,
                    { onLoadMore("seriesPopulares") }) }
                item { HomeSection("Mejor valoradas",
                    seriesTop,
                    generos,
                    onSerieClick,
                    false,
                    idsFavoritos,
                    onToggleFavorite,
                    { onLoadMore("seriesMejorValoradas") }) }
            }
        }
    }
}

@Composable
private fun <T> HomeSection(
    title: String,
    items: List<T>,
    allGenres: List<Genero>,
    onClick: (T) -> Unit,
    isMovie: Boolean,
    idsFavoritos: List<Int>,
    onToggleFavorite: (Any) -> Unit,
    onLoadMore: () -> Unit // Parámetro añadido correctamente
) {
    if (items.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items) { index, item ->
                if (index >= items.size - 2 && items.size >= 20) {
                    onLoadMore()
                }


                val itemId = if (item is FichaPelicula) item.id else (item as FichaSerie).id
                val isFav = idsFavoritos.contains(itemId)

                if (isMovie) {
                    MovieCard(
                        movie = item as FichaPelicula,
                        allGenres = allGenres,
                        isFavorite = isFav,
                        onToggleFavorite = { onToggleFavorite(item) },
                        onClick = { onClick(item) }
                    )
                } else {
                    SerieCard(
                        serie = item as FichaSerie,
                        allGenres = allGenres,
                        isFavorite = isFav,
                        onToggleFavorite = { onToggleFavorite(item) },
                        onClick = { onClick(item) }
                    )
                }
            }
        }
    }
}