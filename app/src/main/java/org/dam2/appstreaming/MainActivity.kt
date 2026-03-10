package org.dam2.appstreaming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {super.onCreate(savedInstanceState)
        // Esta línea es la que carga el NavHostFragment del XML
        setContentView(R.layout.activity_main)
    }
}