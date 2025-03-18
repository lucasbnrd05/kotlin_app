plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services") // Ajout du plugin Google services
}

android {
    namespace = "com.example.imctrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.imctrack"
        minSdk = 26
        targetSdk = 35
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
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.osmdroid.android)
    implementation("com.google.code.gson:gson:2.10.1")

    // Dépendances pour les appels réseau avec Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Dépendances pour le chargement d'images avec Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")

    kapt("androidx.room:room-compiler:2.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")

    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:1.9.22")

    implementation(libs.mpandroidchart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation(libs.zxing.android.embedded)
    //implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.okhttp)

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation("com.google.firebase:firebase-auth:21.1.0")

    // Firebase Auth (si tu utilises l'authentification Firebase)

// Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:21.0.0")

    // Google Play Services Auth (nécessaire pour Google Sign-In)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}

