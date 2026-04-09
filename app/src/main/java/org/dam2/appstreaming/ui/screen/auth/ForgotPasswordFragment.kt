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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.colors.SeaBlueLight

class ForgotPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var email by remember { mutableStateOf("") }
                val estado by viewModel.estadoLogin.collectAsState()

                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF000814)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Recuperar Acceso",
                            style = MaterialTheme.typography.headlineMedium.copy(brush = SeaGradient),
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Introduce tu email y te enviaremos un enlace para restablecer tu contraseña.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Tu email registrado", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().border(1.dp, SeaBlueLight, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF001D3D),
                                unfocusedContainerColor = Color(0xFF001D3D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (email.contains("@")) {
                                    viewModel.recuperarPassword(email)
                                    Toast.makeText(context, "Si el email existe, recibirás un correo en breve.", Toast.LENGTH_LONG).show()
                                    findNavController().popBackStack() // Volver al login
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(SeaGradient), contentAlignment = Alignment.Center) {
                                Text("ENVIAR ENLACE", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}