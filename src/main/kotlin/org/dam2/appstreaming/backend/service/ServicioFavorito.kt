package org.dam2.appstreaming.backend.service

import org.dam2.appstreaming.backend.dto.RespuestaFavorito
import org.dam2.appstreaming.backend.dto.SolicitudFavorito
import org.dam2.appstreaming.backend.model.Favorito
import org.dam2.appstreaming.backend.repository.RepositorioFavorito
import org.dam2.appstreaming.backend.repository.RepositorioUsuario
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Servicio que gestiona los favoritos en la base de datos.
 */
@Service
class ServicioFavorito(
    private val repositorioFavorito: RepositorioFavorito,
    private val repositorioUsuario: RepositorioUsuario
) {

    fun obtenerFavoritos(nombreUsuario: String): List<RespuestaFavorito> {
        val usuario = repositorioUsuario.findByNombreUsuario(nombreUsuario)
            ?: throw Exception("Usuario no encontrado")
        
        return repositorioFavorito.findByUsuario(usuario).map {
            RespuestaFavorito(it.idMultimedia, it.titulo, it.rutaPoster, it.esPelicula)
        }
    }

    fun agregarFavorito(solicitud: SolicitudFavorito) {
        val usuario = repositorioUsuario.findByNombreUsuario(solicitud.nombreUsuario)
            ?: throw Exception("Usuario no encontrado")

        if (repositorioFavorito.existsByUsuarioAndIdMultimedia(usuario, solicitud.idMultimedia)) {
            return // Ya es favorito, no hacemos nada
        }

        val nuevoFavorito = Favorito(
            idMultimedia = solicitud.idMultimedia,
            titulo = solicitud.titulo,
            rutaPoster = solicitud.rutaPoster,
            esPelicula = solicitud.esPelicula,
            usuario = usuario
        )

        repositorioFavorito.save(nuevoFavorito)
    }

    @Transactional
    fun eliminarFavorito(nombreUsuario: String, idMultimedia: Int) {
        val usuario = repositorioUsuario.findByNombreUsuario(nombreUsuario)
            ?: throw Exception("Usuario no encontrado")
        
        repositorioFavorito.deleteByUsuarioAndIdMultimedia(usuario, idMultimedia)
    }
}
