package org.dam2.appstreaming.ui.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.colors.SeaBlueLight

/**
 * REGISTRO
 * 
 * Gestiona la creación de nuevas cuentas de usuario. Destaca por implementar validaciones
 * de seguridad robustas en el lado del cliente antes de enviar los datos a Firebase.
 *
 */
class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val context = LocalContext.current
                var nombre by remember { mutableStateOf("") }
                var usuario by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                val estado by viewModel.estadoLogin.collectAsState()

                // Controlan el estado de la UI y la habilitación del registro.
                val tieneOchoCaracteres = password.length >= 8
                val tieneMayuscula = password.any { it.isUpperCase() }
                val tieneMinuscula = password.any { it.isLowerCase() }
                val tieneNumero = password.any { it.isDigit() }
                val tieneEspecial = password.any { !it.isLetterOrDigit() }
                val contrasenasCoinciden = password == confirmPassword && confirmPassword.isNotEmpty()

                val passwordValida = tieneOchoCaracteres && tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial

                LaunchedEffect(estado) {
                    if (estado is AuthViewModel.ResultadoAuth.Exito) {
                        Toast.makeText(context, "¡Bienvenido, ${nombre}!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        viewModel.resetearEstado()
                    } else if (estado is AuthViewModel.ResultadoAuth.Error) {
                        Toast.makeText(context, (estado as AuthViewModel.ResultadoAuth.Error).mensaje, Toast.LENGTH_SHORT).show()
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF000814)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Crear Cuenta",
                            style = MaterialTheme.typography.displaySmall.copy(
                                brush = SeaGradient
                            ),
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        CustomTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre Real")
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomTextField(value = usuario, onValueChange = { usuario = it }, label = "Email")
                        Spacer(modifier = Modifier.height(12.dp))

                        // CAMPO CONTRASEÑA
                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        CustomTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirmar Contraseña",
                            isPassword = true
                        )

                        // Condiciones para la creación de la contraseña del usuario
                        if (password.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                ValidationText("Mínimo 8 caracteres", tieneOchoCaracteres)
                                ValidationText("Mayúsculas y minúsculas", tieneMayuscula && tieneMinuscula)
                                ValidationText("Al menos un número", tieneNumero)
                                ValidationText("Un carácter especial (@, #, $, etc.)", tieneEspecial)
                                ValidationText("Las contraseñas coinciden", contrasenasCoinciden)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (nombre.isNotBlank() && usuario.contains("@") && passwordValida) {
                                    viewModel.registrarse(usuario, password, nombre)
                                } else if (!contrasenasCoinciden) {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                } else if (!passwordValida) {
                                    Toast.makeText(context, "La contraseña no es lo suficientemente segura", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Rellena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            enabled = estado !is AuthViewModel.ResultadoAuth.Cargando,
                            contentPadding = PaddingValues()
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (passwordValida && nombre.isNotBlank() && contrasenasCoinciden) SeaGradient else Brush.linearGradient(
                                        listOf(Color.Gray, Color.DarkGray)
                                    )
                                ),
                                contentAlignment = Alignment.Center) {
                                if (estado is AuthViewModel.ResultadoAuth.Cargando) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("REGISTRARSE", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * INDICADOR DE VALIDACIÓN
 */
@Composable
fun ValidationText(text: String, isValid: Boolean) {
    Text(
        text = if (isValid) "✓ $text" else "○ $text",
        color = if (isValid) Color(0xFF4CAF50) else Color.Gray,
        fontSize = 12.sp,
        fontWeight = if (isValid) FontWeight.Bold else FontWeight.Normal
    )
}

/**
 * CAMPO DE TEXTO PERSONALIZADO
 */
@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (value.isNotEmpty()) SeaBlueLight else Color(0xFF1B263B),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF001D3D),
            unfocusedContainerColor = Color(0xFF001D3D),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
