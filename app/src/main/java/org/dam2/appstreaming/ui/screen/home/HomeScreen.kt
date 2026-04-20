package org.dam2.appstreaming.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.ui.component.MovieCard
import org.dam2.appstreaming.ui.component.SerieCard
import org.dam2.appstreaming.ui.component.common.GenreSelector
import org.dam2.appstreaming.ui.component.common.HomeDrawer
import org.dam2.appstreaming.ui.component.common.StreamTopBar
import org.dam2.appstreaming.ui.component.common.StreamTabs

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
    onLoadMore: (String) -> Unit,
    // Nuevos parámetros para la paginación del filtro de género
    resultadosFiltroGenero: List<Any>,
    onCargarMasFiltro: () -> Unit,
    onSearchClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer(
                onHomeClick = { scope.launch { drawerState.close() } },
                onPerfilClick = { scope.launch { drawerState.close() } },
                onFavoritosClick = { scope.launch { drawerState.close(); onNavigateToFavoritos() } },
                onLogoutClick = { scope.launch { drawerState.close(); onLogoutClick() } },
                onMisListasClick = { scope.launch { drawerState.close(); onNavigateToMisListas() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                StreamTopBar(
                    title = "SeaStream",
                    showBackButton = false,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = onSearchClick
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                StreamTabs(selectedTab, onTabSelected)
                GenreSelector(generos, selectedGenreId, onGeneroClick)

                MainContent(
                    selectedTab = selectedTab,
                    moviesNow = moviesNow,
                    moviesPop = moviesPop,
                    moviesTop = moviesTop,
                    seriesNow = seriesNow,
                    seriesPop = seriesPop,
                    seriesTop = seriesTop,
                    generos = generos,
                    selectedGenreId = selectedGenreId,
                    onMovieClick = onMovieClick,
                    onSerieClick = onSerieClick,
                    idsFavoritos = idsFavoritos,
                    onToggleFavorite = onToggleFavorite,
                    onLoadMore = onLoadMore,
                    resultadosFiltroGenero = resultadosFiltroGenero,
                    onCargarMasFiltro = onCargarMasFiltro
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
    onLoadMore: (String) -> Unit,
    resultadosFiltroGenero: List<Any>,
    onCargarMasFiltro: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (selectedGenreId != null) {
            // ---------------------------------------------------------------
            // MODO FILTRO: una sola sección con los resultados del género
            // ---------------------------------------------------------------
            val nombreGenero = generos.find { it.id == selectedGenreId }?.name ?: "Filtrados"

            item {
                if (selectedTab == 0) {
                    @Suppress("UNCHECKED_CAST")
                    HomeSectionPeliculas(
                        title = "Resultados: $nombreGenero",
                        items = resultadosFiltroGenero as List<FichaPelicula>,
                        allGenres = generos,
                        onClick = onMovieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        // Aquí sí conectamos la carga infinita del filtro
                        onLoadMore = onCargarMasFiltro
                    )
                } else {
                    @Suppress("UNCHECKED_CAST")
                    HomeSectionSeries(
                        title = "Resultados: $nombreGenero",
                        items = resultadosFiltroGenero as List<FichaSerie>,
                        allGenres = generos,
                        onClick = onSerieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = onCargarMasFiltro
                    )
                }
            }
        } else {
            // ---------------------------------------------------------------
            // MODO CATÁLOGO NORMAL: tres secciones por tab
            // ---------------------------------------------------------------
            if (selectedTab == 0) {
                item {
                    HomeSectionPeliculas(
                        title = "Novedades",
                        items = moviesNow,
                        allGenres = generos,
                        onClick = onMovieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("peliculasEstreno") }
                    )
                }
                item {
                    HomeSectionPeliculas(
                        title = "Populares",
                        items = moviesPop,
                        allGenres = generos,
                        onClick = onMovieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("peliculasPopulares") }
                    )
                }
                item {
                    HomeSectionPeliculas(
                        title = "Mejor valoradas",
                        items = moviesTop,
                        allGenres = generos,
                        onClick = onMovieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("peliculasMejorValoradas") }
                    )
                }
            } else {
                item {
                    HomeSectionSeries(
                        title = "Novedades TV",
                        items = seriesNow,
                        allGenres = generos,
                        onClick = onSerieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("seriesEstreno") }
                    )
                }
                item {
                    HomeSectionSeries(
                        title = "Populares en TV",
                        items = seriesPop,
                        allGenres = generos,
                        onClick = onSerieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("seriesPopulares") }
                    )
                }
                item {
                    HomeSectionSeries(
                        title = "Mejor valoradas",
                        items = seriesTop,
                        allGenres = generos,
                        onClick = onSerieClick,
                        idsFavoritos = idsFavoritos,
                        onToggleFavorite = onToggleFavorite,
                        onLoadMore = { onLoadMore("seriesMejorValoradas") }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Secciones tipadas
// -----------------------------------------------------------------------------

@Composable
private fun HomeSectionPeliculas(
    title: String,
    items: List<FichaPelicula>,
    allGenres: List<Genero>,
    onClick: (FichaPelicula) -> Unit,
    idsFavoritos: List<Int>,
    onToggleFavorite: (Any) -> Unit,
    onLoadMore: () -> Unit
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
            itemsIndexed(items) { index, pelicula ->
                // Pedimos más cuando quedan 2 elementos para el final
                if (index >= items.size - 2 && items.size >= 20) {
                    onLoadMore()
                }
                MovieCard(
                    movie = pelicula,
                    allGenres = allGenres,
                    isFavorite = idsFavoritos.contains(pelicula.id),
                    onToggleFavorite = { onToggleFavorite(pelicula) },
                    onClick = { onClick(pelicula) }
                )
            }
        }
    }
}

@Composable
private fun HomeSectionSeries(
    title: String,
    items: List<FichaSerie>,
    allGenres: List<Genero>,
    onClick: (FichaSerie) -> Unit,
    idsFavoritos: List<Int>,
    onToggleFavorite: (Any) -> Unit,
    onLoadMore: () -> Unit
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
            itemsIndexed(items) { index, serie ->
                if (index >= items.size - 2 && items.size >= 20) {
                    onLoadMore()
                }
                SerieCard(
                    serie = serie,
                    allGenres = allGenres,
                    isFavorite = idsFavoritos.contains(serie.id),
                    onToggleFavorite = { onToggleFavorite(serie) },
                    onClick = { onClick(serie) }
                )
            }
        }
    }
}