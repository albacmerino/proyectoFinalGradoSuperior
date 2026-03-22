package org.dam2.appstreaming.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.dam2.appstreaming.data.local.dao.DaoFavorito
import org.dam2.appstreaming.data.local.entities.EntidadFavorito

/**
 * Clase principal de la base de datos Room. Une entidad y DAO para crear la bbdd
 * física en el dispositivo
 */
@Database(entities = [EntidadFavorito::class], version = 1, exportSchema = false)
abstract class BaseDatosApp : RoomDatabase() {
    abstract fun daoFavorito(): DaoFavorito

    companion object {
        @Volatile
        private var INSTANCIA: BaseDatosApp? = null

        fun obtenerBaseDatos(contexto: Context): BaseDatosApp {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatosApp::class.java,
                    "base_datos_app"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
