package org.dam2.appstreaming.ui.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.local.prefs.PreferenciasUsuario
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.colors.SeaBlueDark
import org.dam2.appstreaming.ui.colors.SeaBlueLight

class LoginFragment : Fragment() {

    // Vinculamos el ViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val estado by viewModel.estadoLogin.collectAsState()
                val context = LocalContext.current

                // 1. ESTA ES LA ÚNICA VÍA DE ENTRADA AL HOME
                LaunchedEffect(estado) {
                    when (estado) {
                        is AuthViewModel.ResultadoAuth.Exito -> {
                            // Si Firebase dice que los datos son correctos, navegamos
                            findNavController().navigate(
                                R.id.action_loginFragment_to_homeFragment,
                                null,
                                navOptions {
                                    popUpTo(R.id.loginFragment) { inclusive = true }
                                }
                            )
                             viewModel.resetearEstado()
                        }
                        is AuthViewModel.ResultadoAuth.Error -> {
                            // Si los datos están mal, Firebase lanza error y mostramos Toast
                            Toast.makeText(context, (estado as AuthViewModel.ResultadoAuth.Error).mensaje, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }

                MaterialTheme {
                    LoginScreen(
                        onLoginClick = { usuario, password ->
                            if (usuario.isNotBlank() && password.isNotBlank()) {
                                val emailFinal = if (usuario.contains("@")) usuario else "$usuario@seastream.com"
                                viewModel.iniciarSesion(emailFinal, password)
                            } else {
                                Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },

                        onForgotPasswordClick = { u ->
                            // Creamos un bundle por si queremos pasar el usuario que ya escribió
                            val bundle = Bundle().apply {
                                putString("email_previa", if (u.contains("@")) u else "$u@seastream.com")
                            }

                            // Navegamos a la nueva pantalla (Asegúrate de tener este ID en tu nav_graph.xml)
                            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment, bundle)
                        },
                        onCreateAccountClick = {
                            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit,
                onForgotPasswordClick: (String) -> Unit, // <--- CAMBIA ESTO (añade String)
                onCreateAccountClick: () -> Unit
) {

    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mantenerSesion by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) } // Estado para la visibilidad

    // Fondo oscuro para que el neón brille
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF000814)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "SeaStream",
                style = MaterialTheme.typography.displayMedium.copy(
                    brush = SeaGradient,
                    alpha = 1f
                ),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Sigue la corriente del mejor streaming",
                style = MaterialTheme.typography.bodyMedium,
                color = SeaBlueLight.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 48.dp),
                textAlign = TextAlign.Center
            )

            // CAMPO USUARIO (Actualizado)
            TextField(
                value = usuario,
                onValueChange = { usuario = it },
                placeholder = { Text("Usuario", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(
                        width = if (usuario.isNotEmpty()) 2.dp else 1.dp,
                        brush = if (usuario.isNotEmpty()) SeaGradient else Brush.linearGradient(
                            listOf(Color(0xFF1B263B), Color(0xFF1B263B))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF001D3D),
                    unfocusedContainerColor = Color(0xFF001D3D),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = SeaBlueLight
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            // CAMPO CONTRASEÑA
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(
                        width = if (password.isNotEmpty()) 2.dp else 1.dp,
                        brush = if (password.isNotEmpty()) SeaGradient else Brush.linearGradient(
                            listOf(Color(0xFF1B263B), Color(0xFF1B263B))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                // Lógica de transformación
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                // AÑADIMOS EL ICONO DEL OJO AQUÍ
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_visibility_on) // Necesitas estos iconos en res/drawable
                    else
                        painterResource(id = R.drawable.ic_visibility_off)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = "Mostrar contraseña", tint = SeaBlueLight)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF001D3D),
                    unfocusedContainerColor = Color(0xFF001D3D),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = SeaBlueLight
                )
            )

            Text(
                text = "¿Has olvidado tu contraseña?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clickable { onForgotPasswordClick(usuario) },
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))


            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = mantenerSesion,
                    onCheckedChange = { mantenerSesion = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SeaBlueLight,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Mantener sesión iniciada",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { mantenerSesion = !mantenerSesion }
                )
            }

            Button(
                onClick = {
                    if (usuario.isNotBlank() && password.isNotBlank()) {
                        // 2. Instanciamos usando el context de Compose
                        val prefs = PreferenciasUsuario(context)
                        prefs.guardarMantenerSesion(mantenerSesion)

                        onLoginClick(usuario, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SeaGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "INICIAR SESIÓN",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿No tienes cuenta? ", color = Color.LightGray)
                Text(
                    text = "Crear cuenta",
                    fontWeight = FontWeight.Bold,
                    color = SeaBlueLight,
                    modifier = Modifier.clickable { onCreateAccountClick() }
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    // Añadimos { _ -> } para que coincida con la nueva firma (String) -> Unit
    LoginScreen(
        onLoginClick = { _, _ -> },
        onForgotPasswordClick = { _ -> },
        onCreateAccountClick = {}
    )
}