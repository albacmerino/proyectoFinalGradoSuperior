package org.dam2.appstreaming.ui.screen.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.local.prefs.PreferenciasUsuario
import org.dam2.appstreaming.ui.colors.SeaBlueLight

/**
 * PANTALLA DE CARGA
 * 
 * Es la pantalla de bienvenida de la aplicación. Su función principal es gestionar
 * el enrutamiento inicial del usuario basándose en su estado de sesión.
 *
 */
class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SplashScreenContent(onTimeout = {
                        val auth = FirebaseAuth.getInstance()
                        val prefs = PreferenciasUsuario(requireContext())
                        val currentUser = auth.currentUser

                        // Se aplica el principio de persistencia de sesión configurada por el usuario.
                        if (currentUser != null && prefs.obtenerMantenerSesion()) {
                            // Usuario autenticado y con preferencia de sesión activa -> Pantalla Principal
                            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                        } else {
                            // En caso contrario, se fuerza el login por seguridad.
                            if (currentUser != null) auth.signOut()
                            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                        }
                    })
                }
            }
        }
    }
}

/**
 * COMPONENTE VISUAL DEL SPLASH
 * Centraliza la UI de la pantalla de bienvenida.
 */
@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    // Animación Lottie (JSON)
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_animation_blue)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Lanzamos un efecto secundario de temporización para la transición de pantalla
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cargando contenido...",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.LightGray
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp" // Fuerza un tamaño de móvil real
)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreenContent(onTimeout = {})
    }
}
