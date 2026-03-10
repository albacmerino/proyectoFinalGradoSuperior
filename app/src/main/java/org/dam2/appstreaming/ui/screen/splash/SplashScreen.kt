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
import kotlinx.coroutines.delay
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.colors.SeaBlueLight

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

                        try {
                            findNavController ().navigate(R.id.action_splashFragment_to_loginFragment)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_animation_blue)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

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