package org.dam2.appstreaming.backend.repository


import org.dam2.appstreaming.backend.model.Lista
import org.dam2.appstreaming.backend.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para gestionar las peliculas y series favoritas en la base de datos.
 */
@Repository
interface RepositorioLista : JpaRepository<Lista, Long> {
    // Busca los elementos de una lista específica de un usuario (ej: todos sus "FAVORITOS")

    fun findByUsuarioAndTipoLista(usuario: Usuario, tipoLista: String): List<Lista>

    // Borrar un elemento de una lista específica
    fun deleteByUsuarioAndIdMultimediaAndTipoLista(usuario: Usuario, idMultimedia: Int, tipoLista: String)

    // Comprobar si existe en una lista específica
    fun existsByUsuarioAndIdMultimediaAndTipoLista(usuario: Usuario, idMultimedia: Int, tipoLista: String): Boolean
}
