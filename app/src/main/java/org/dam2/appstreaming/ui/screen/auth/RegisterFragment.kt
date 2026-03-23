package org.dam2.appstreaming.ui.screen.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val context = LocalContext.current
                var nombre by remember { mutableStateOf("") }
                var usuario by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                val estado by viewModel.estadoLogin.collectAsState()

                LaunchedEffect(estado) {
                    if (estado is AuthViewModel.ResultadoAuth.Exito) {
                        Toast.makeText(context, "¡Éxito!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    }
                }

                MaterialTheme {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                        Spacer(Modifier.height(8.dp))
                        TextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") })
                        Spacer(Modifier.height(8.dp))
                        TextField(value = password, onValueChange = { password = it }, label = { Text("Pass") })
                        Spacer(Modifier.height(32.dp))
                        
                        Button(
                            onClick = {
                                Log.d("DEBUG_CLICK", "Botón pulsado")
                                if (usuario.isNotBlank() && password.isNotBlank()) {
                                    viewModel.registrarse(usuario, password)
                                } else {
                                    Toast.makeText(context, "Rellena los campos", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PROBAR REGISTRO")
                        }
                    }
                }
            }
        }
    }
}
