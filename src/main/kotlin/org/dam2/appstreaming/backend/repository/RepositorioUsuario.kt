package org.dam2.appstreaming.backend.repository

import org.dam2.appstreaming.backend.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para la entidad Usuario.
 */
@Repository
interface RepositorioUsuario : JpaRepository<Usuario, String> {
    fun findByNombreUsuario(nombreUsuario: String): Usuario?
    fun existsByNombreUsuario(nombreUsuario: String): Boolean
}
