package org.dam2.appstreaming.ui.screen.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Necesitas añadir esta dependencia en build.gradle si no la tienes
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.RespuestaAutenticacion

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio original para el Backend (Spring Boot)
    private val repositorio = RepositorioBackend(application)

    // Instancia de Firebase
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _estadoLogin = MutableStateFlow<ResultadoAuth>(ResultadoAuth.Idle)
    val estadoLogin: StateFlow<ResultadoAuth> = _estadoLogin.asStateFlow()

    /**
     * Iniciar sesión con Firebase
     * nombre: En Firebase debe ser un email (puedes añadir "@gmail.com" si solo usas nombres)
     */
    fun iniciarSesion(email: String, clave: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Iniciando sesión en Firebase para: $email")
            _estadoLogin.value = ResultadoAuth.Cargando

            try {
                val resultado = firebaseAuth.signInWithEmailAndPassword(email, clave).await()
                val user = resultado.user

                if (user != null) {
                    Log.d("AuthViewModel", "Firebase Login exitoso. Nombre en nube: ${user.displayName}")

                    // Aquí cogemos el nombre real que guardamos al registrar
                    val respuesta = RespuestaAutenticacion(
                        token = user.uid,
                        nombreUsuario = user.displayName ?: user.email ?: "Usuario"
                    )
                    _estadoLogin.value = ResultadoAuth.Exito(respuesta)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en Firebase Login: ${e.message}")
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error de autenticación")
            }
        }
    }


    /**
     * Registrar usuario en Firebase
     */
    fun registrarse(email: String, clave: String, nombreReal: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Registrando en Firebase a: $email")
            _estadoLogin.value = ResultadoAuth.Cargando

            try {
                // 1. Crear usuario
                val resultado = firebaseAuth.createUserWithEmailAndPassword(email, clave).await()
                val user = resultado.user

                if (user != null) {
                    // 2. Guardar el nombre real en el perfil de Firebase (Nube)
                    val actualizacionesPerfil = userProfileChangeRequest {
                        displayName = nombreReal
                    }
                    user.updateProfile(actualizacionesPerfil).await()

                    Log.d("AuthViewModel", "Registro y nombre guardado: ${user.displayName}")

                    val respuesta = RespuestaAutenticacion(
                        token = user.uid,
                        nombreUsuario = nombreReal
                    )
                    _estadoLogin.value = ResultadoAuth.Exito(respuesta)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en Firebase Registro: ${e.message}")
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error al crear cuenta")
            }
        }
    }

    fun resetearEstado() {
        _estadoLogin.value = ResultadoAuth.Idle
    }


    fun cerrarSesion() {
        firebaseAuth.signOut()
        _estadoLogin.value = ResultadoAuth.Idle
    }

    // Dentro de AuthViewModel.kt
    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            _estadoLogin.value = ResultadoAuth.Cargando
            try {// Firebase envía el email automáticamente
                firebaseAuth.sendPasswordResetEmail(email).await()
                _estadoLogin.value = ResultadoAuth.Error("Correo de recuperación enviado")
                // Usamos .Error temporalmente para mostrar el mensaje en el Toast de la UI
            } catch (e: Exception) {
                _estadoLogin.value = ResultadoAuth.Error(e.message ?: "Error al enviar correo")
            }
        }
    }

    sealed class ResultadoAuth {
        object Idle : ResultadoAuth()
        object Cargando : ResultadoAuth()
        data class Exito(val datos: RespuestaAutenticacion) : ResultadoAuth()
        data class Error(val mensaje: String) : ResultadoAuth()
    }
}