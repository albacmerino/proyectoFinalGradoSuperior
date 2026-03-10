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

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    RegisterScreen(
                        onRegisterClick = { nombre, usuario, password ->
                            // Lógica de registro futura
                            // findNavController().navigate(R.id.action_register_to_home)
                        },
                        onBackToLoginClick = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Validación simple de contraseña
    val isPasswordSafe = password.length >= 6

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
                text = "Unirse a la Corriente",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = SeaGradient,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Crea tu cuenta en SeaStream",
                style = MaterialTheme.typography.bodyMedium,
                color = SeaBlueLight.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // CAMPO NOMBRE
            SeaTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre Completo")

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO USUARIO
            SeaTextField(value = usuario, onValueChange = { usuario = it }, label = "Usuario")

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO CONTRASEÑA
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña (min. 6 caracteres)", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(
                        width = if (password.isNotEmpty()) 2.dp else 1.dp,
                        brush = if (isPasswordSafe) SeaGradient else Brush.linearGradient(listOf(Color(0xFF1B263B), Color(0xFF1B263B))),
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

            if (password.isNotEmpty() && !isPasswordSafe) {
                Text(
                    text = "La contraseña es demasiado corta",
                    color = Color.Red.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // BOTÓN REGISTRO
            Button(
                onClick = { if (isPasswordSafe) onRegisterClick(nombre, usuario, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp),
                enabled = nombre.isNotEmpty() && usuario.isNotEmpty() && isPasswordSafe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isPasswordSafe && usuario.isNotEmpty()) SeaGradient else Brush.linearGradient(listOf(Color.Gray, Color.Gray))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CREAR CUENTA", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ya tengo cuenta. Volver atrás",
                color = SeaBlueLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackToLoginClick() }
            )
        }
    }
}

@Composable
fun SeaTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                width = if (value.isNotEmpty()) 2.dp else 1.dp,
                brush = if (value.isNotEmpty()) SeaGradient else Brush.linearGradient(listOf(Color(0xFF1B263B), Color(0xFF1B263B))),
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
}