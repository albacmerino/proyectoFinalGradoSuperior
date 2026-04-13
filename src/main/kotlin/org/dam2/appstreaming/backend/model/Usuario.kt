package org.dam2.appstreaming.backend.model

import jakarta.persistence.*

/**
 * Entidad que representa a un usuario en la base de datos.
 */
@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @Column(name = "firebase_uid", nullable = false, unique = true)
    val id: String,

    @Column(unique = true, nullable = false)
    val nombreUsuario: String,

    @Column(nullable = true)
    val email: String? = null

)
