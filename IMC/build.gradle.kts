// build.gradle.kts au niveau du projet
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()  // Ajoutez le dépôt Google
        mavenCentral() // Ajoutez le dépôt Maven Central
    }
    dependencies {
        // Utilisez directement la dépendance pour le plugin Google Services
        classpath("com.google.gms:google-services:4.3.15")
    }
}
