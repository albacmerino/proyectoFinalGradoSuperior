package org.dam2.appstreaming.backend.model

import jakarta.persistence.*

/**
 * Entidad que representa una pelicula o serie favorita de un usuario.
 */
@Entity
@Table(name = "Lista")
data class Lista(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val idMultimedia: Int, // ID que viene de TMDB

    @Column(nullable = false)
    val titulo: String,

    val rutaPoster: String?,

    @Column(nullable = false)
    val esPelicula: Boolean,

    @Column(nullable = false)
    val tipoLista: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    val usuario: Usuario? = null
)
