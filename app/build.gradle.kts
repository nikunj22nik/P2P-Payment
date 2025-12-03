/*plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}*/
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}


android {
    namespace = "com.p2p.application"
    compileSdk = 34   // ya 35/36 agar machine support karti hai

    defaultConfig {
        applicationId = "com.p2p.application"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val BASE_URL = project.property("BASE_URL")
            buildConfigField("String", "BASE_URL", "${BASE_URL}")

            val MEDIA_URL = project.property("MEDIA_URL")
            buildConfigField("String", "MEDIA_URL", "${MEDIA_URL}")
        }

        debug {
            val BASE_URL = project.property("BASE_URL")
            buildConfigField("String", "BASE_URL", "${BASE_URL}")

            val MEDIA_URL = project.property("MEDIA_URL")
            buildConfigField("String", "MEDIA_URL", "${MEDIA_URL}")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding= true
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

    // Navigation dependencies only from TOML
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //circularImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")  // only stable
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Size based DP/SP
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.google.scanner)
    implementation(libs.play.services)
    implementation(libs.gsonWork)
//    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //image picker
    implementation("com.github.Dhaval2404:ImagePicker:2.1")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt ("com.google.dagger:hilt-android-compiler:2.47")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    kapt("androidx.lifecycle:lifecycle-compiler:2.3.1")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.activity:activity-ktx:1.3.1")

    //Retrofit for api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")
    // firebase crashlytics
    implementation("com.google.firebase:firebase-crashlytics:18.2.9")
    implementation("com.google.firebase:firebase-analytics:20.1.2")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1")) // example: latest BOM
    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

}