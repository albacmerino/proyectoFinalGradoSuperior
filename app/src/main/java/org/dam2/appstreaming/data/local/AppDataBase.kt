package org.dam2.appstreaming.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.dam2.appstreaming.data.local.dao.ListaDao
import org.dam2.appstreaming.data.local.entities.ListaEntity

/**
 * BASE DE DATOS DE LA APLICACIÓN (Room Database)
 * 
 * Clase abstracta que sirve como punto de acceso principal a la BBDD Room persistida.
 * Utiliza la librería Room de Jetpack para mapear objetos Kotlin a tablas de base de datos (ORM).
 *
 * - Patrón Singleton: Garantiza una única instancia de la BBDD para evitar conflictos de escritura.
 * - Thread Safety: Uso de @Volatile y synchronized para asegurar la integridad en entornos multihilo.
 */
@Database(entities = [ListaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Punto de acceso al DAO de listas.
     */
    abstract fun listaDao(): ListaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Método para obtener la instancia de la BBDD.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seastream_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
