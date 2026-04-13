package org.dam2.appstreaming.backend.service

import org.dam2.appstreaming.backend.dto.RespuestaAutenticacion
import org.dam2.appstreaming.backend.dto.SolicitudAutenticacion
import org.dam2.appstreaming.backend.dto.SolicitudRegistro
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

        // Creamos el usuario usando el UID de Firebase y el nombre
        val nuevoUsuario = Usuario(
            id = solicitud.uid, // Este es el firebase_uid que viene del móvil
            nombreUsuario = solicitud.nombreUsuario
        )

        val usuarioGuardado = repositorioUsuario.save(nuevoUsuario)

        return RespuestaAutenticacion(
            token = "sesion-validada-${usuarioGuardado.id}",
            nombreUsuario = usuarioGuardado.nombreUsuario
        )
    }

    fun login(solicitud: SolicitudAutenticacion): RespuestaAutenticacion {
        val usuario = repositorioUsuario.findByNombreUsuario(solicitud.nombreUsuario)
            ?: throw Exception("Usuario no encontrado")


        return RespuestaAutenticacion(
            token = "sesion-validada-${usuario.id}",
            nombreUsuario = usuario.nombreUsuario
        )
    }

    fun listarTodos(): List<Usuario> {
        return repositorioUsuario.findAll()
    }

    fun registrarSiNoExiste(solicitud: SolicitudAutenticacion) {
        if (!repositorioUsuario.existsById(solicitud.uid)) {
            val nuevoUsuario = Usuario(
                id = solicitud.uid,
                nombreUsuario = solicitud.nombreUsuario
            )
            repositorioUsuario.save(nuevoUsuario)
        }
    }
    fun sincronizarUsuario(solicitud: SolicitudRegistro) {
        // Si el usuario no existe en PostgreSQL, lo creamos
        if (!repositorioUsuario.existsById(solicitud.uid)) {
            val nuevoUsuario = Usuario(
                id = solicitud.uid, // Usamos el UID de Firebase
                nombreUsuario = solicitud.nombreUsuario,
                email = solicitud.email
            )
            repositorioUsuario.save(nuevoUsuario)
        }
    }
}
