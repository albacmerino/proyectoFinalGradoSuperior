package org.dam2.appstreaming.data.mapper

import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.remote.dto.RespuestaLista
import org.dam2.appstreaming.data.remote.dto.ResultadoBusqueda

/**
 * Convierte un objeto de la lista de favoritos en una Ficha de Película.
 */
fun RespuestaLista.toFichaPelicula(): FichaPelicula {
    return FichaPelicula(
        id = this.idMultimedia,
        titulo = this.titulo,
        rutaPoster = this.rutaPoster,
        rutaFondo = null,
        puntuacionMedia = 0.0,
        sinopsis = "",
        fechaLanzamiento = "",
        idsGeneros = emptyList()
    )
}

/**
 * Convierte un objeto de la lista de favoritos en una Ficha de Serie.
 */
fun RespuestaLista.toFichaSerie(): FichaSerie {
    return FichaSerie(
        id = this.idMultimedia,
        titulo = this.titulo,
        rutaPoster = this.rutaPoster,
        rutaFondo = null,
        puntuacionMedia = 0.0,
        sinopsis = "",
        fechaLanzamiento = "",
        idsGeneros = emptyList()
    )
}

/**
 * Convierte un resultado de búsqueda en una [FichaPelicula].
 */
fun ResultadoBusqueda.toFichaPelicula(): FichaPelicula = FichaPelicula(
    id = id,
    titulo = tituloMostrar,
    sinopsis = sinopsis ?: "",
    rutaPoster = rutaPoster,
    rutaFondo = rutaFondo,
    fechaLanzamiento = fechaLanzamiento,
    puntuacionMedia = puntuacion ?: 0.0,
    idsGeneros = idsGeneros
)

/**
 * Convierte un resultado de búsqueda en una [FichaSerie].
 */
fun ResultadoBusqueda.toFichaSerie(): FichaSerie = FichaSerie(
    id = id,
    titulo = tituloMostrar,
    sinopsis = sinopsis ?: "",
    rutaPoster = rutaPoster,
    rutaFondo = rutaFondo,
    fechaLanzamiento = fechaEmision,
    puntuacionMedia = puntuacion ?: 0.0,
    idsGeneros = idsGeneros
)