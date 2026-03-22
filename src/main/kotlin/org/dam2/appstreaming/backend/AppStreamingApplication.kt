package org.dam2.appstreaming.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Clase principal que arranca el servidor Spring Boot.
 */
@SpringBootApplication
class AppStreamingApplication

fun main(args: Array<String>) {
    runApplication<AppStreamingApplication>(*args)
}
