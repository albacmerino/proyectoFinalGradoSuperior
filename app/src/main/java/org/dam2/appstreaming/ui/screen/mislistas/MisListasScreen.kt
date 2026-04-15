import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dam2.appstreaming.data.remote.dto.RespuestaLista

@Composable
fun MisListasScreen(
    listasAgrupadas: Map<String, List<RespuestaLista>>,
    onItemClick: (RespuestaLista) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            // Reutilizamos tu TopBar modular
            org.dam2.appstreaming.ui.component.common.StreamTopBar(
                title = "Mis Listas",
                showBackButton = true,
                onMenuClick = onBackClick
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        if (listasAgrupadas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes listas creadas", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Iteramos sobre el mapa: cada clave es una sección (Novedades, Favoritos, etc)
                listasAgrupadas.forEach { (nombre, contenido) ->
                    item {
                        SeccionPersonalizada(nombre, contenido, onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionPersonalizada(
    titulo: String,
    items: List<RespuestaLista>,
    onClick: (RespuestaLista) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            color = org.dam2.appstreaming.ui.colors.SeaBlueLight,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                // Usamos la miniatura con el póster
                CardMiniatura(item, onClick = { onClick(item) })
            }
        }
    }
}
@Composable
fun CardMiniatura(
    item: RespuestaLista,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = androidx.compose.ui.Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFF032541)
        )
    ) {
        Column {
            coil.compose.AsyncImage(
                model = "https://image.tmdb.org/t/p/w342${item.rutaPoster}",
                contentDescription = item.titulo,
                modifier = androidx.compose.ui.Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            androidx.compose.material3.Text(
                text = item.titulo,
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = androidx.compose.ui.Modifier.padding(8.dp)
            )
        }
    }
}