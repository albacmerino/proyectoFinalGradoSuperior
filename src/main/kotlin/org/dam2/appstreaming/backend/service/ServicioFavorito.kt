package org.dam2.appstreaming.backend.service

import org.dam2.appstreaming.backend.dto.RespuestaLista
import org.dam2.appstreaming.backend.dto.SolicitudLista
import org.dam2.appstreaming.backend.repository.RepositorioLista
import org.dam2.appstreaming.backend.model.Lista
import org.dam2.appstreaming.backend.repository.RepositorioUsuario
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Servicio generalizado que gestiona diferentes listas de contenido (Favoritos, Pendientes, etc.)
 */
@Service
class ServicioLista(
    private val repositorioLista: RepositorioLista,
    private val repositorioUsuario: RepositorioUsuario
) {

    /**
     * Obtiene el contenido de una lista específica para un usuario.
     */
    fun obtenerContenidoDeLista(nombreUsuario: String, tipoLista: String): List<RespuestaLista> {
        val usuario = repositorioUsuario.findByNombreUsuario(nombreUsuario)
            ?: throw Exception("Usuario no encontrado")

        // Buscamos filtrando por el usuario y el tipo de lista (ej: "FAVORITO")
        return repositorioLista.findByUsuarioAndTipoLista(usuario, tipoLista).map {
            RespuestaLista(
                idMultimedia = it.idMultimedia,
                titulo = it.titulo,
                rutaPoster = it.rutaPoster,
                esPelicula = it.esPelicula,
                tipoLista = it.tipoLista
            )
        }
    }

    /**
     * Agrega un nuevo elemento a una lista específica (Favoritos, Pendientes, etc.)
     */
    fun agregarALista(solicitud: SolicitudLista) {
        val usuario = repositorioUsuario.findByNombreUsuario(solicitud.nombreUsuario)
            ?: throw Exception("Usuario no encontrado")

        // Comprobamos si ya existe en ESA lista específica para evitar duplicados
        if (repositorioLista.existsByUsuarioAndIdMultimediaAndTipoLista(
                usuario, solicitud.idMultimedia, solicitud.tipoLista)) {
            return
        }

        val nuevoElemento = Lista(
            idMultimedia = solicitud.idMultimedia,
            titulo = solicitud.titulo,
            rutaPoster = solicitud.rutaPoster,
            esPelicula = solicitud.esPelicula,
            tipoLista = solicitud.tipoLista, // Guardamos el tipo de lista
            usuario = usuario
        )

        repositorioLista.save(nuevoElemento)
    }

    /**
     * Elimina un contenido de una lista específica de un usuario.
     */
    @Transactional
    fun eliminarDeLista(nombreUsuario: String, idMultimedia: Int, tipoLista: String) {
        val usuario = repositorioUsuario.findByNombreUsuario(nombreUsuario)
            ?: throw Exception("Usuario no encontrado")

        // Eliminamos filtrando por usuario, id de la peli y el nombre de la lista
        repositorioLista.deleteByUsuarioAndIdMultimediaAndTipoLista(usuario, idMultimedia, tipoLista)
    }
}