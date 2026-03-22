package org.dam2.appstreaming.backend.service

import org.dam2.appstreaming.backend.dto.RespuestaAutenticacion
import org.dam2.appstreaming.backend.dto.SolicitudAutenticacion
import org.dam2.appstreaming.backend.model.Usuario
import org.dam2.appstreaming.backend.repository.RepositorioUsuario
import org.springframework.stereotype.Service

/**
 * Servicio que gestiona la lógica de registro y login.
 */
@Service
class ServicioAutenticacion(private val repositorioUsuario: RepositorioUsuario) {

    fun registrar(solicitud: SolicitudAutenticacion): RespuestaAutenticacion {
        if (repositorioUsuario.existsByNombreUsuario(solicitud.nombreUsuario)) {
            throw Exception("El nombre de usuario ya existe")
        }

        val nuevoUsuario = Usuario(
            nombreUsuario = solicitud.nombreUsuario,
            contrasena = solicitud.contrasena // TODO: Encriptar en el futuro
        )

        val usuarioGuardado = repositorioUsuario.save(nuevoUsuario)

        return RespuestaAutenticacion(
            token = "token-provisional-${usuarioGuardado.id}",
            nombreUsuario = usuarioGuardado.nombreUsuario
        )
    }

    fun login(solicitud: SolicitudAutenticacion): RespuestaAutenticacion {
        val usuario = repositorioUsuario.findByNombreUsuario(solicitud.nombreUsuario)
            ?: throw Exception("Usuario no encontrado")

        if (usuario.contrasena != solicitud.contrasena) {
            throw Exception("Contraseña incorrecta")
        }

        return RespuestaAutenticacion(
            token = "token-provisional-${usuario.id}",
            nombreUsuario = usuario.nombreUsuario
        )
    }

    fun listarTodos(): List<Usuario> {
        return repositorioUsuario.findAll()
    }
}
