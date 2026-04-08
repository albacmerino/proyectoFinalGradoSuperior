package org.dam2.appstreaming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.google.firebase.auth.FirebaseAuth
import org.dam2.appstreaming.data.local.prefs.PreferenciasUsuario

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos preferencias y Firebase
        val prefs = PreferenciasUsuario(this)
        val auth = FirebaseAuth.getInstance()

        // 2. LOGICA DE MANTENER SESIÓN
        // Si hay un usuario pero el usuario NO marcó el checkbox la última vez
        if (auth.currentUser != null && !prefs.obtenerMantenerSesion()) {
            auth.signOut() // Borramos la sesión de Firebase para que pida Login
        }

        setContentView(R.layout.activity_main)

        // 3. AUTO-LOGIN
        // Volvemos a comprobar: si tras el paso anterior aún hay usuario, vamos al Home
        if (auth.currentUser != null) {
            window.decorView.post {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController

                navController.navigate(R.id.homeFragment, null, navOptions {
                    popUpTo(R.id.loginFragment) { inclusive = true }
                })
            }
        }
    }
}