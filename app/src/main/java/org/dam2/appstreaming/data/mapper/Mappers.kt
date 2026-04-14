package org.dam2.appstreaming.data.mapper

import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.remote.dto.RespuestaLista

/**
 * Convierte un objeto de la lista de favoritos en una Ficha de Película.
 * Los datos que falten (como la sinopsis) se cargarán luego desde la API en el detalle.
 */
fun RespuestaLista.toFichaPelicula(): FichaPelicula {
    return FichaPelicula(
        id = this.idMultimedia,
        titulo = this.titulo,
        rutaPoster = this.rutaPoster,
        rutaFondo = null, // Se cargará en el detalle
        puntuacionMedia = 0.0, // Se cargará en el detalle
        sinopsis = "", // Se cargará en el detalle
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