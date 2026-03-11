plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "org.dam2.appstreaming"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.dam2.appstreaming"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

}

dependencies {

    // Usamos BOM para que todas las librerías de Compose sean compatibles entre sí
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Compose Activity
    implementation("androidx.activity:activity-compose:1.9.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.1")

    // Lottie Compose
    implementation("com.airbnb.android:lottie-compose:6.5.0")
    //Gifs
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")

    //navegacion con fragments
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.1")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}