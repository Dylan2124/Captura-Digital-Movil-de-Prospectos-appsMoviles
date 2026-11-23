plugins {
    // Plugin principal para aplicaciones Android
    alias(libs.plugins.android.application)
    // Plugin de compilación para Jetpack Compose en Kotlin
    alias(libs.plugins.kotlin.compose)
    // Plugin KSP (Kotlin Symbol Processing) para anotaciones (Room, etc.)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.capturaprospectos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.capturaprospectos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        // Habilita Jetpack Compose para la interfaz de usuario
        compose = true
    }
}

dependencies {
    // --- Room Database (Persistencia de datos local) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Navegación en Jetpack Compose ---
    implementation(libs.androidx.navigation.compose)

    // --- WorkManager (Tareas en segundo plano / Sincronización) ---
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // --- Jetpack Compose (BOM y componentes UI Material 3) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.compose.material:material-icons-extended")

    // --- KTX y Ciclo de vida (Core y Lifecycle) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Dependencias de Pruebas Unitarias (Unit Tests con JUnit, MockK y Coroutines Test) ---
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.14.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")

    // --- Dependencias de Pruebas Instrumentadas (AndroidTest) ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // --- Herramientas de Depuración (Debug / Tooling) ---
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // --- Retrofit y Convertidores (Consumo de APIs REST) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    // --- Interceptor HTTP para depuración de red (Logging en Logcat) ---
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
