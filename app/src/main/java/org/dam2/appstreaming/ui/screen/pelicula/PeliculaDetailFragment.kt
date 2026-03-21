package org.dam2.appstreaming.ui.screen.pelicula

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import org.dam2.appstreaming.data.network.CastMember
import org.dam2.appstreaming.data.network.Provider
import org.dam2.appstreaming.data.network.Review
import org.dam2.appstreaming.data.network.Keyword
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.SeaRatingBar
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero
import androidx.core.net.toUri

class PeliculaDetailFragment : Fragment() {

    private val viewModel: PeliculaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ContenidoDetallePelicula(
                        viewModel = viewModel,
                        alVolver = { findNavController().popBackStack() },
                        abrirUrl = { url ->
                            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                        },
                        onPeliculaClick = { pelicula ->
                            viewModel.setSelectedItem(pelicula)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoDetallePelicula(
    viewModel: PeliculaViewModel,
    alVolver: () -> Unit,
    abrirUrl: (String) -> Unit,
    onPeliculaClick: (FichaPelicula) -> Unit
) {
    val peliculaSeleccionada by viewModel.selectedPelicula.collectAsState()
    val todosLosGeneros by viewModel.allGenres.collectAsState()
    val claveTrailer by viewModel.trailerKey.collectAsState()
    val enlacePlataformas by viewModel.watchLink.collectAsState()
    val enlaceDirecto by viewModel.directPlatformLink.collectAsState()
    val proveedorPrincipal by viewModel.mainProvider.collectAsState()
    val certificacion by viewModel.certificacion.collectAsState()
    val reparto by viewModel.reparto.collectAsState()
    val resenas by viewModel.resenas.collectAsState()
    val recomendaciones by viewModel.recomendaciones.collectAsState()
    val palabrasClave by viewModel.palabrasClave.collectAsState()

    peliculaSeleccionada?.let { datosPelicula ->
        PantallaDetallePelicula(
            item = datosPelicula,
            allGenres = todosLosGeneros,
            watchLink = enlacePlataformas,
            directLink = enlaceDirecto,
            mainProvider = proveedorPrincipal,
            certification = certificacion,
            cast = reparto,
            resenas = resenas,
            recomendaciones = recomendaciones,
            palabrasClave = palabrasClave,
            onBackClick = alVolver,
            onPlayTrailerClick = {
                claveTrailer?.let { clave -> abrirUrl("https://www.youtube.com/watch?v=$clave") }
            },
            onWatchNowClick = {
                val urlFinal = if (!enlaceDirecto.isNullOrBlank()) enlaceDirecto else enlacePlataformas
                urlFinal?.let { url -> abrirUrl(url) }
            },
            onSeeAllReviewsClick = {
                abrirUrl("https://www.themoviedb.org/movie/${datosPelicula.id}/reviews?language=es-ES")
            },
            onRecommendationClick = onPeliculaClick
        )
    } ?: PantallaCargando()
}

@Composable
fun PantallaCargando() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000814)), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SeaBlueLight)
    }
}

@Composable
fun PantallaDetallePelicula(
    item: FichaPelicula,
    allGenres: List<Genero>,
    watchLink: String?,
    directLink: String?,
    mainProvider: Provider?,
    certification: String?,
    cast: List<CastMember>,
    resenas: List<Review>,
    recomendaciones: List<FichaPelicula>,
    palabrasClave: List<Keyword>,
    onBackClick: () -> Unit,
    onPlayTrailerClick: () -> Unit,
    onWatchNowClick: () -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onRecommendationClick: (FichaPelicula) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000814))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            CabeceraImagen(item.backdropPath, onPlayTrailerClick)
            BannerStreaming(watchLink, directLink, mainProvider, onWatchNowClick)

            Column(modifier = Modifier.padding(20.dp)) {
                TituloSeccion(item.title)
                MetadatosPelicula(item.releaseDate, item.genreIds, allGenres, certification)
                
                SeccionOpinion()

                Spacer(modifier = Modifier.height(24.dp))
                PuntuacionYAcciones(item.voteAverage)
                Spacer(modifier = Modifier.height(32.dp))
                SinopsisSeccion(item.overview)
                
                RepartoSeccion(cast)
                SeccionSocial(resenas, onSeeAllReviewsClick)
                PalabrasClaveSeccion(palabrasClave)
                RecomendacionesSeccion(recomendaciones, onRecommendationClick)
            }
        }
        BotonIrAtras(onBackClick)
    }
}

@Composable
fun SeccionOpinion() {
    var mostrarSelector by remember { mutableStateOf(false) }
    var notaSeleccionada by remember { mutableIntStateOf(0) }
    var vibraSeleccionada by remember { mutableStateOf("") }
    val listaVibras = listOf("😍", "😂", "🤔", "😱", "😴")

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { mostrarSelector = !mostrarSelector }
        ) {
            Text(text = vibraSeleccionada.ifEmpty { "😍🤔" }, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Dinos tu opinión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(28.dp).background(Color(0xFF01B4E4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$notaSeleccionada", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (mostrarSelector) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                color = Color(0xFF032541),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¿Cuál es tu vibra?", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listaVibras.forEach { vibra ->
                            Text(
                                text = vibra,
                                fontSize = 26.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (vibraSeleccionada == vibra) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { vibraSeleccionada = vibra }
                                    .padding(4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Selecciona tu puntuación:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (i in 1..10) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (notaSeleccionada == i) Color(0xFF01B4E4) else Color.White.copy(alpha = 0.1f))
                                    .clickable { notaSeleccionada = i },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$i", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Button(
                        onClick = { mostrarSelector = false },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01B4E4))
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionSocial(resenas: List<Review>, onSeeAllReviewsClick: () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Social",
                style = MaterialTheme.typography.titleLarge,
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(24.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Reseñas ${resenas.size}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(60.dp).height(3.dp).background(SeaBlueLight))
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))

        if (resenas.isNotEmpty()) {
            val resena = resenas.first()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF032541).copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val avatarUrl = resena.authorDetails.avatarPath?.let {
                            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w185$it"
                        }
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Una reseña de ${resena.author}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (resena.authorDetails.rating != null) {
                                    Surface(color = Color.Black, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "★ ${resena.authorDetails.rating.toInt()}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = "Escrito el ${resena.createdAt.take(10)}",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resena.content,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Leer todas las reseñas",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSeeAllReviewsClick() }
            )

        } else {
            Text("No hay reseñas todavía.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

@Composable
fun RepartoSeccion(reparto: List<CastMember>) {
    if (reparto.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            Text("Reparto principal", style = MaterialTheme.typography.titleLarge, color = SeaBlueLight, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(contentPadding = PaddingValues(end = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reparto.take(15)) { actor ->
                    CardActor(actor)
                }
            }
        }
    }
}

@Composable
fun CardActor(actor: CastMember) {
    Card(modifier = Modifier.width(120.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF032541))) {
        Column {
            AsyncImage(model = "https://image.tmdb.org/t/p/w185${actor.profilePath}", contentDescription = null, modifier = Modifier.height(150.dp).fillMaxWidth(), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(actor.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(actor.character, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PalabrasClaveSeccion(palabrasClave: List<Keyword>) {
    if (palabrasClave.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Text(
                text = "Palabras clave",
                style = MaterialTheme.typography.titleLarge,
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                palabrasClave.forEach { keyword ->
                    KeywordChip(keyword.name)
                }
            }
        }
    }
}

@Composable
fun KeywordChip(name: String) {
    Surface(
        color = SeaBlueLight.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SeaBlueLight.copy(alpha = 0.5f))
    ) {
        Text(
            text = name,
            color = SeaBlueLight,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RecomendacionesSeccion(recomendaciones: List<FichaPelicula>, onPeliculaClick: (FichaPelicula) -> Unit) {
    if (recomendaciones.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.titleLarge,
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recomendaciones) { pelicula ->
                    CardRecomendacion(pelicula, onPeliculaClick)
                }
            }
        }
    }
}

@Composable
fun CardRecomendacion(pelicula: FichaPelicula, onPeliculaClick: (FichaPelicula) -> Unit) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .clickable { onPeliculaClick(pelicula) }
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${pelicula.backdropPath}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pelicula.title,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${(pelicula.voteAverage * 10).toInt()}%",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CabeceraImagen(ruta: String?, alPulsarPlay: () -> Unit) {
    Box(modifier = Modifier.height(350.dp).fillMaxWidth()) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w1280$ruta",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF000814).copy(alpha = 0.5f)), startY = 600f)
        ))
        Surface(
            modifier = Modifier.align(Alignment.Center).clickable { alPulsarPlay() },
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.8f))
        ) {
            Icon(Icons.Default.PlayArrow, null, Modifier.padding(16.dp).size(48.dp), Color.White)
        }
    }
}

@Composable
fun BannerStreaming(watchLink: String?, directLink: String?, proveedor: Provider?, alPulsar: () -> Unit) {
    if (watchLink != null || directLink != null) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF032541))
                .clickable { alPulsar() }.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            proveedor?.let {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/original${it.logoPath}",
                    contentDescription = null,
                    modifier = Modifier.size(35.dp).clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text("Ahora en retransmisión", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text("Ver ahora", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun TituloSeccion(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.displaySmall.copy(brush = SeaGradient, fontWeight = FontWeight.ExtraBold),
        lineHeight = 40.sp
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun MetadatosPelicula(fecha: String?, idsGeneros: List<Int>?, listaGeneros: List<Genero>, certification: String?) {
    val nombres = idsGeneros?.mapNotNull { id -> listaGeneros.find { it.id == id }?.name }
        ?.joinToString(" • ") ?: "General"

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!certification.isNullOrEmpty()) {
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
            ) {
                Text(text = certification, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(fecha?.take(4) ?: "N/A", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(" • ", color = SeaBlueLight, modifier = Modifier.padding(horizontal = 6.dp))
        Text(nombres, color = SeaBlueLight.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1, fontWeight = FontWeight.Medium)
        Text(" • ", color = SeaBlueLight, modifier = Modifier.padding(horizontal = 6.dp))
        Surface(color = SeaBlueLight.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp), border = androidx.compose.foundation.BorderStroke(1.dp, SeaBlueLight.copy(alpha = 0.5f))) {
            Text(
                text = "PELÍCULA",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = SeaBlueLight,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PuntuacionYAcciones(nota: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SeaRatingBar(rating = (nota / 2).toInt())
        Text("  ${String.format("%.1f", nota)}",
            color = SeaBlueLight,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
    }
    Spacer(modifier = Modifier.height(32.dp))
    Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp).border(2.dp, SeaGradient, RoundedCornerShape(16.dp)), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), shape = RoundedCornerShape(16.dp)) {
        Text("AÑADIR A MI LISTA", color = Color.White, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun SinopsisSeccion(texto: String) {
    Text("Sinopsis", style = MaterialTheme.typography.titleLarge, color = SeaBlueLight, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
    Text(texto.ifEmpty { "No hay descripción disponible." }, color = Color.White.copy(alpha = 0.8f), lineHeight = 26.sp)
}

@Composable
fun BotonIrAtras(alPulsar: () -> Unit) {
    IconButton(onClick = alPulsar, modifier = Modifier.padding(top = 48.dp, start = 16.dp).size(45.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape)) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
    }
}