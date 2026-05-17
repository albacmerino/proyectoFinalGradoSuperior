package org.dam2.appstreaming.data.mapper

import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista
import org.dam2.appstreaming.data.remote.dto.tmdb.ResultadoBusqueda

/**
 * ARCHIVO DE MAPEO DE DATOS
 * 
 * Este archivo contiene funciones encargadas de transformar objetos entre capas.
 *
 * - Desacoplamiento: Si la API de TMDB cambia, solo modificamos el mapper.
 * - Limpieza de datos: La UI solo recibe lo que necesita, eliminando campos nulos o innecesarios.
 */

/**
 * Mapea una respuesta de lista del backend a una Ficha de Película.
 * Se utiliza para poder mostrar elementos guardados en los componentes de UI genéricos.
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
 * Mapea una respuesta de lista del backend a una Ficha de Serie.
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
 * Transforma un resultado genérico de la búsqueda Multi-Search de TMDB en una Ficha de Película.
 * Extrae y unifica los campos que TMDB devuelve con nombres diferentes para películas y series.
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
 * Transforma un resultado genérico de la búsqueda Multi-Search de TMDB en una Ficha de Serie.
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
