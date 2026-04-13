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
import org.dam2.appstreaming.data.local.prefs.PreferenciasUsuario
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
    fun iniciarSesion(email: String, clave: String, nombreUsuario: String) {
        viewModelScope.launch {
            _estadoLogin.value = ResultadoAuth.Cargando
            try {
                // 1. Autenticación en la Nube (Firebase)
                val resultado = firebaseAuth.signInWithEmailAndPassword(email, clave).await()
                val firebaseUser = resultado.user

                if (firebaseUser != null) {
                    // 2. Sincronización en Local (PostgreSQL)
                    // DTO específico de registro
                    val exito = repositorio.sincronizarUsuario(
                        uid = firebaseUser.uid,
                        nombre = nombreUsuario,
                        email = firebaseUser.email
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
                    val actualizacionesPerfil = userProfileChangeRequest {
                        displayName = nombreReal
                    }
                    user.updateProfile(actualizacionesPerfil).await()

                    // --- NUEVO: Sincronizar con PostgreSQL tras el registro ---
                    repositorio.sincronizarUsuario(
                        uid = user.uid,
                        nombre = nombreReal,
                        email = user.email
                    )
                    // -----------------------------------------------------------

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

    fun cerrarSesion(prefs: PreferenciasUsuario) {
        // 1. Cerramos en Firebase
        firebaseAuth.signOut()

        // 2. Limpiamos el "Recordar sesión" para que no entre solo la próxima vez
        prefs.guardarMantenerSesion(false)

        // 3. Opcional: Resetear el estado del login
        _estadoLogin.value = ResultadoAuth.Idle
    }

    sealed class ResultadoAuth {
        object Idle : ResultadoAuth()
        object Cargando : ResultadoAuth()
        data class Exito(val datos: RespuestaAutenticacion) : ResultadoAuth()
        data class Error(val mensaje: String) : ResultadoAuth()
    }
}