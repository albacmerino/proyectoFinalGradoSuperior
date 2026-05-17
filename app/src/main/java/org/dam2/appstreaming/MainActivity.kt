package org.dam2.appstreaming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * ACTIVITY PRINCIPAL
 * 
 * Implementa el patrón "Single Activity Architecture" recomendado por Google. 
 * Esta actividad actúa como el host principal para todos los fragmentos y la lógica de 
 * navegación de la aplicación.
 *
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout que contiene el NavHostFragment
        setContentView(R.layout.activity_main)
    }
}
