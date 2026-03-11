package org.dam2.appstreaming.ui.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.colors.SeaBlueDark
import org.dam2.appstreaming.ui.colors.SeaBlueLight

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    LoginScreen(
                        onLoginClick = { usuario, password ->
                            // findNavController().navigate(R.id.action_login_to_home)
                        },
                        onForgotPasswordClick = {  findNavController().navigate(R.id.action_loginFragment_to_homeFragment)},
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
                onForgotPasswordClick: () -> Unit,
                onCreateAccountClick: () -> Unit
) {

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        brush = if (usuario.isNotEmpty()) SeaGradient else Brush.linearGradient(listOf(Color(0xFF1B263B), Color(0xFF1B263B))),
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
                        brush = if (password.isNotEmpty()) SeaGradient else Brush.linearGradient(listOf(Color(0xFF1B263B), Color(0xFF1B263B))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            Text(
                text = "¿Has olvidado tu contraseña?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clickable { onForgotPasswordClick() },
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // BOTÓN (Actualizado con la variable 'usuario')
            Button(
                onClick = { onLoginClick(usuario, password) },
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
    LoginScreen({ _, _ -> }, {}, {})
}