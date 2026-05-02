plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.thekainchee.user"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thekainchee.user"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Hilt Core
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //Fragment KTS Library
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    //PinView library
    implementation("io.github.chaosleung:pinview:1.4.4")
    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // ROOM
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")


    //Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //exoplayer
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //google places
    implementation("com.google.android.libraries.places:places:3.5.0")


    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

// Enables Java 8+ APIs (e.g., LocalDate, LocalTime) on lower Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
//Material and ViewPager
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")


    //Skeleton ui Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")


    //Dot indicator
    implementation("com.tbuonomo:dotsindicator:4.2")
}


