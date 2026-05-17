package org.dam2.appstreaming.ui.screen.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaAutenticacion

/**
 * VIEWMODEL DE AUTENTICACIÓN
 * 
 * Clase encargada de gestionar el flujo de login y registro de la aplicación.
 * Integra Firebase Authentication para la gestión de identidades y RepositorioBackend 
 * para la persistencia de perfiles en el sistema local/remoto.
 *
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = RepositorioBackend(application)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _estadoLogin = MutableStateFlow<ResultadoAuth>(ResultadoAuth.Idle)
    val estadoLogin: StateFlow<ResultadoAuth> = _estadoLogin.asStateFlow()

    /**
     * Inicia sesión utilizando las credenciales de Firebase.
     * Tras un login exitoso, sincroniza los datos del usuario con el repositorio.
     */
    fun iniciarSesion(email: String, clave: String, nombreUsuario: String) {
        viewModelScope.launch {
            _estadoLogin.value = ResultadoAuth.Cargando
            try {
                val resultado = firebaseAuth.signInWithEmailAndPassword(email, clave).await()
                val firebaseUser = resultado.user

                if (firebaseUser != null) {
                    val exito = repositorio.sincronizarUsuario(
                        uid = firebaseUser.uid,
                        nombre = nombreUsuario
                    )

                    if (exito) {
                        _estadoLogin.value = ResultadoAuth.Exito(
                            RespuestaAutenticacion(firebaseUser.uid, nombreUsuario)
                        )
                    } else {
                        _estadoLogin.value = ResultadoAuth.Error("Error al guardar perfil en PostgreSQL")
                    }
                }
            } catch (e: Exception) {
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error de acceso")
            }
        }
    }

    /**
     * Registra un nuevo usuario en Firebase y actualiza su perfil.
     */
    fun registrarse(email: String, clave: String, nombreReal: String) {
        viewModelScope.launch {
            _estadoLogin.value = ResultadoAuth.Cargando
            try {
                val resultado = firebaseAuth.createUserWithEmailAndPassword(email, clave).await()
                val user = resultado.user

                if (user != null) {
                    // Actualización del perfil de usuario en Firebase
                    val actualizacionesPerfil = userProfileChangeRequest {
                        displayName = nombreReal
                    }
                    user.updateProfile(actualizacionesPerfil).await()

                    // Sincronización tras el registro exitoso
                    repositorio.sincronizarUsuario(
                        uid = user.uid,
                        nombre = nombreReal
                    )

                    val respuesta = RespuestaAutenticacion(
                        token = user.uid,
                        nombreUsuario = nombreReal
                    )
                    _estadoLogin.value = ResultadoAuth.Exito(respuesta)
                }
            } catch (e: Exception) {
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error al crear cuenta")
            }
        }
    }

    fun resetearEstado() {
        _estadoLogin.value = ResultadoAuth.Idle
    }


    /**
     * Solicita a Firebase el envío de un correo para restablecer la contraseña.
     */
    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            _estadoLogin.value = ResultadoAuth.Cargando
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _estadoLogin.value = ResultadoAuth.Error("Correo de recuperación enviado")
            } catch (e: Exception) {
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error al enviar correo")
            }
        }
    }


    /**
     * Representa los diferentes estados de la autenticación.
     */
    sealed class ResultadoAuth {
        object Idle : ResultadoAuth()
        object Cargando : ResultadoAuth()
        data class Exito(val datos: RespuestaAutenticacion) : ResultadoAuth()
        data class Error(val mensaje: String) : ResultadoAuth()
    }
}
