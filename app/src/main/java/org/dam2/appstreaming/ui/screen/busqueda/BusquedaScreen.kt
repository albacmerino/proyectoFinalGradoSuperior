package org.dam2.appstreaming.ui.screen.busqueda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.dam2.appstreaming.data.remote.dto.ResultadoBusqueda
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.ScoreRing

/**
 * Pantalla principal de búsqueda.
 *
 * Muestra un campo de texto siempre visible en la parte superior y
 * va mostrando resultados en tiempo real conforme el usuario escribe.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    estado: BusquedaViewModel.BusquedaState,
    onTextoChange: (String) -> Unit,
    onLimpiar: () -> Unit,
    onBackClick: () -> Unit,
    onPeliculaClick: (ResultadoBusqueda) -> Unit,
    onSerieClick: (ResultadoBusqueda) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Abrimos el teclado automáticamente al entrar en la pantalla
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000814))
    ) {
        // -----------------------------------------------------------------
        // Barra de búsqueda
        // -----------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón volver
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = SeaBlueLight
                )
            }

            // Campo de texto
            TextField(
                value = estado.consulta,
                onValueChange = onTextoChange,
                placeholder = {
                    Text(
                        "Buscar películas y series...",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (estado.consulta.isNotEmpty()) 2.dp else 1.dp,
                        brush = if (estado.consulta.isNotEmpty()) SeaGradient
                        else Brush.linearGradient(listOf(Color(0xFF1B263B), Color(0xFF1B263B))),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF001D3D),
                    unfocusedContainerColor = Color(0xFF001D3D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = SeaBlueLight,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SeaBlueLight
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.consulta.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = onLimpiar) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                )
            )
        }

        HorizontalDivider(color = Color(0xFF1B263B))

        // -----------------------------------------------------------------
        // Contenido central
        // -----------------------------------------------------------------
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // Estado inicial: no se ha escrito nada todavía
                estado.consulta.isBlank() -> {
                    PantallaInicial()
                }

                // Cargando resultados
                estado.estaCargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SeaBlueLight)
                    }
                }

                // Sin resultados
                estado.sinResultados -> {
                    PantallaSinResultados(consulta = estado.consulta)
                }

                // Lista de resultados
                else -> {
                    ListaResultados(
                        resultados = estado.resultados,
                        onItemClick = { resultado ->
                            focusManager.clearFocus()
                            if (resultado.esPelicula) onPeliculaClick(resultado)
                            else onSerieClick(resultado)
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Subcomponentes
// -----------------------------------------------------------------------------

/** Pantalla que se muestra cuando el campo de búsqueda está vacío. */
@Composable
private fun PantallaInicial() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔍", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¿Qué quieres ver hoy?",
            style = MaterialTheme.typography.headlineSmall.copy(brush = SeaGradient),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Escribe el nombre de una película o serie\npara encontrarla al instante.",
            color = Color.Gray,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** Pantalla que se muestra cuando la búsqueda no devuelve resultados. */
@Composable
private fun PantallaSinResultados(consulta: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "😕", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Sin resultados para",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = "\"$consulta\"",
            style = MaterialTheme.typography.titleLarge.copy(brush = SeaGradient),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Prueba con otro título o comprueba la ortografía.",
            color = Color.Gray,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** Lista de resultados de búsqueda. */
@Composable
private fun ListaResultados(
    resultados: List<ResultadoBusqueda>,
    onItemClick: (ResultadoBusqueda) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Cabecera con el número de resultados
        item {
            Text(
                text = "${resultados.size} resultados",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(resultados, key = { "${it.mediaType}_${it.id}" }) { resultado ->
            FilaResultado(resultado = resultado, onClick = { onItemClick(resultado) })
        }
    }
}

/**
 * Fila individual de la lista de resultados.
 * Muestra póster, título, año, tipo y puntuación.
 */
@Composable
private fun FilaResultado(
    resultado: ResultadoBusqueda,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Póster
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF001D3D))
        ) {
            if (resultado.rutaPoster != null) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w185${resultado.rutaPoster}",
                    contentDescription = resultado.tituloMostrar,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder cuando no hay póster
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (resultado.esPelicula) "🎬" else "📺",
                        fontSize = 24.sp
                    )
                }
            }

            // Anillo de puntuación sobre el póster
            if ((resultado.puntuacion ?: 0.0) > 0.0) {
                ScoreRing(
                    score = resultado.puntuacion ?: 0.0,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Información textual
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = resultado.tituloMostrar,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Año + tipo (PELÍCULA o SERIE)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                resultado.anio?.let { anio ->
                    Text(text = anio, color = Color.Gray, fontSize = 13.sp)
                }

                // Badge de tipo
                Surface(
                    color = SeaBlueLight.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        SeaBlueLight.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (resultado.esPelicula) "PELÍCULA" else "SERIE",
                        color = SeaBlueLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Sinopsis recortada
            if (!resultado.sinopsis.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = resultado.sinopsis,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color(0xFF1B263B)
    )
}