// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "org.dam2"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.2")

    // PostgreSQL
    implementation("org.postgresql:postgresql:42.7.1")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.2")

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}
