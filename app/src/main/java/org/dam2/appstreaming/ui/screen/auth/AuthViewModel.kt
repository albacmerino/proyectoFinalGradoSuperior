package org.dam2.appstreaming.ui.screen.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.repository.RepositorioBackend
import org.dam2.appstreaming.data.remote.dto.RespuestaAutenticacion

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = RepositorioBackend(application)

    private val _estadoLogin = MutableStateFlow<ResultadoAuth>(ResultadoAuth.Idle)
    val estadoLogin: StateFlow<ResultadoAuth> = _estadoLogin.asStateFlow()

    fun iniciarSesion(nombre: String, clave: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Iniciando sesión para: $nombre")
            _estadoLogin.value = ResultadoAuth.Cargando
            val resultado = repositorio.login(nombre, clave)
            resultado.onSuccess {
                Log.d("AuthViewModel", "Login exitoso")
                _estadoLogin.value = ResultadoAuth.Exito(it)
            }.onFailure {
                Log.e("AuthViewModel", "Error en login: ${it.message}")
                _estadoLogin.value = ResultadoAuth.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun registrarse(nombre: String, clave: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Iniciando registro para: $nombre")
            _estadoLogin.value = ResultadoAuth.Cargando
            val resultado = repositorio.registrar(nombre, clave)
            resultado.onSuccess {
                Log.d("AuthViewModel", "Registro exitoso")
                _estadoLogin.value = ResultadoAuth.Exito(it)
            }.onFailure {
                Log.e("AuthViewModel", "Error en registro: ${it.message}")
                _estadoLogin.value = ResultadoAuth.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun resetearEstado() {
        _estadoLogin.value = ResultadoAuth.Idle
    }

    sealed class ResultadoAuth {
        object Idle : ResultadoAuth()
        object Cargando : ResultadoAuth()
        data class Exito(val datos: RespuestaAutenticacion) : ResultadoAuth()
        data class Error(val mensaje: String) : ResultadoAuth()
    }
}
