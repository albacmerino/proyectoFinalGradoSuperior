import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.ui.component.common.GenreSelector
import org.dam2.appstreaming.ui.component.common.StreamTabs
import org.dam2.appstreaming.ui.component.common.StreamTopBar

@Composable
fun StreamHeader(
    titulo: String,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onMenuClick: () -> Unit,
    // Parámetros opcionales para el selector de géneros
    generos: List<Genero> = emptyList(),
    selectedGenreId: Int? = null,
    onGeneroClick: ((Int?) -> Unit)? = null
) {
    Column(modifier = Modifier.background(Color(0xFF000814))) {
        // 1. Barra Superior (Modular)
        StreamTopBar(
            title = titulo,
            showBackButton = false,
            onMenuClick = onMenuClick
        )

        // 2. Pestañas (Modular)
        StreamTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        // 3. Selector de Géneros (Solo si se pasan géneros)
        if (generos.isNotEmpty() && onGeneroClick != null) {
            GenreSelector(generos, selectedGenreId, onGeneroClick)
        }
    }
}