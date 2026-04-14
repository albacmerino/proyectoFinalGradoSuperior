package org.dam2.appstreaming.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.dam2.appstreaming.data.local.dao.ListaDao
import org.dam2.appstreaming.data.local.entities.ListaEntity

@Database(entities = [ListaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listaDao(): ListaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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